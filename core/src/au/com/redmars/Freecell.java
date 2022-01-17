package au.com.redmars;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class Freecell implements Tableau{
    
    private final Integer boardColumns = 8;
    private final Integer freeCells = 4;
    private final Integer cardMargin = 56;
    private float startY = 0;
    private float y = 0; 

    Card dragging;
    Card viewing;
    Card[] deck = new Card[deckSize + 4];
    List<Column> board = new ArrayList<>(); 
    List<Column> homeCells = new ArrayList<>();
    Texture cardTileSet = new Texture("classic_13x4x560x780.png");

    public int getCardMargin() {
        return cardMargin;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public long countFreeCells() {  
        return board.stream().filter(x -> x.maxCards == 1).filter(x -> x.cards.isEmpty()).count();
    }

    public long countEmptyColumns(Column dst) { 
        return board.stream().filter(x -> x.maxCards != 1).filter(x -> x.cards.isEmpty()).filter(x -> !x.equals(dst)).count();
    }

    @Override
    public int chainLength(Card card) {
        return board.get(card.col).cards.size() - board.get(card.col).cards.indexOf(card);
    }
    
    // See :
    // https://boardgames.stackexchange.com/questions/45155/freecell-how-many-cards-can-be-moved-at-once
    // For formula for spaces required to move chains

    @Override
    public boolean canMoveChain(Column dst, Integer chainLength) {
        if (countEmptyColumns(dst) == 0) {
            return countFreeCells() + 1 >= chainLength ? true : false;
        } else {
            return (Math.pow(2.0D , countEmptyColumns(dst)) * (countFreeCells() + 1) >= chainLength) ? true : false;
        }
    }

    @Override
    public void moveChain(Card src, Column dst) {
        
        Column srcCol = board.get(src.col);
        List<Card> movingCards = srcCol.cards.subList(srcCol.cards.indexOf(src), srcCol.cards.size());
        movingCards.forEach(c -> { 
            c.col = dst.index; 
        });
        dst.cards.addAll(movingCards);
        srcCol.cards.removeAll(movingCards);
        refreshBoard();
    }

    private Integer lowestHomeCell() {
        if (homeCells.stream().filter(c -> c.cards.isEmpty()).findFirst().isPresent()) return 1;
        return homeCells.stream().min((b,c) -> b.cards.get(b.cards.size() - 1).faceValue - c.cards.get(c.cards.size() - 1).faceValue)
            .get().cards.stream().map(c -> c.faceValue).max(Integer::compare).get() + 1; 
    }

    public Integer nextHomeCellCard(List<Card> cardList) {
        if (cardList.isEmpty()) return 0;
        return cardList.get(cardList.size() - 1).faceValue + 1;
    }

    private Card lastCard(List<Card> cardList) {
        return cardList.get(cardList.size() - 1);
    }

    @Override
    public void autoComplete() {
        board.stream().filter(c -> !c.cards.isEmpty()).forEach(c -> {
            Card last = lastCard(c.cards);
            Column hc = homeCells.get(last.suit);
            System.out.printf("%d lowest home cell\n", lowestHomeCell());
            if (last.faceValue <= lowestHomeCell() && nextHomeCellCard(hc.cards) == last.faceValue) {
                hc.cards.add(last);
				c.cards.remove(last);
				refreshBoard();
				last.image.setPosition(hc.hitbox.x, hc.hitbox.y);
                autoComplete();
            }
        });
    }

    @Override
    public void refreshBoard() {
        board.forEach(b -> {
            y = startY;
            if (b.index >= boardColumns) y = b.hitbox.y; //This is a free or home cell
            b.cards.forEach(c -> {c.image.setPosition(b.hitbox.x, y);y = y - 160;});
            refreshColumn(b.cards);
            if (!b.cards.isEmpty()) b.populateHitBoxes();
        });
    }

    @Override
    public void refreshColumn(List<Card> col) {
        ListIterator<Card> iterator = col.listIterator(col.size());
        Card topCard = new Card();
        Boolean endOfList = true;
        while (iterator.hasPrevious()) {

            Card c = iterator.previous();

            if (endOfList) { // The last card can always be grabbed
                c.canGrab = true;
                endOfList = false;
            } else if (topCard.canGrab && c.isChained(topCard)) {
                c.canGrab = true;
            } else {
                c.canGrab = false;
            }
            topCard = c;
        }
    }

    @Override
    public void setupBoard(OrthographicCamera camera) {
        board.clear();
        homeCells.clear();
        float y = camera.viewportHeight - cardMargin * 2 - cardHeight;
        float x = cardMargin * 2;
        for (int i = 0; i < boardColumns; ++i) { //8 playing columns
            board.add(new Column(i,deckSize,new Rectangle(x,0,cardWidth,y)));
            x = x + cardWidth + cardMargin;
        }
        for (int i = 0; i < freeCells; ++i) { //4 Freecell columns
            Rectangle hitbox = new Rectangle((cardMargin * 2) + (560 * i) + (cardMargin * .5F * i),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
            board.add(new Column(i+boardColumns,1,hitbox));
        }
        for (int i = 4; i < 8; ++i) { //4 Home cells
            Rectangle hitbox = new Rectangle((cardMargin * 5.5F) + (560 * i) + (cardMargin * .5F * i),
					camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
            homeCells.add(new Column(i - 4,suitSize,hitbox));
        }

    }

    @Override
    public void Deal(OrthographicCamera camera) {
        setupBoard(camera);
        Random seeder = new Random();
        Long seed = seeder.nextLong();
        System.out.printf("Current seed: %d\n",seed);
        Random r = new Random(seed);
        startY = camera.viewportHeight - cardMargin * 2 - cardHeight * 2;
        for (int i = deckSize - 1; i > 0; i--) {

            // Pick a random index from 0 to i
            int j = r.nextInt(i);

            // Swap arr[i] with the element at random index
            Card temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
        for (int i = 0; i < deckSize; ++i) {
            board.get(i % boardColumns).cards.add(deck[i]);
            deck[i].col = i % boardColumns;
        }
        refreshBoard();
    }

    @Override
    public void drawBoard(Batch batch,ShapeRenderer shapeRenderer,OrthographicCamera camera) {
        shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		for (int fc = 0; fc < 4; ++fc) {
			shapeRenderer.rect((cardMargin * 2) + (560 * fc) + (cardMargin * .5F * fc),
					camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
		}
		for (int fc = 4; fc < 8; ++fc) {
			shapeRenderer.rect((cardMargin * 5.5F) + (560 * fc) + (cardMargin * .5F * fc),
					camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
		}
		shapeRenderer.end();
        batch.begin();
		batch.disableBlending();
        board.forEach(column -> column.cards.forEach(card -> {
			card.image.draw(batch);
		}));
        homeCells.forEach(hc -> { 
			if (!hc.cards.isEmpty()) {
				hc.cards.get(hc.cards.size()-1).image.draw(batch);
			}
		});
        batch.end();
    }

    Freecell() {
        for (int c = 0; c < deckSize; ++c) {
            int a = c % 13;
            int b = c / 13;
            int srcX = a * cardWidth;
            int srcY = b * cardHeight;
            deck[c] = new Card(a, b, new Sprite(cardTileSet, srcX, srcY, cardWidth, cardHeight));
        }
    }
}
