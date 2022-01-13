package au.com.redmars;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Deck {
    private final Integer deckSize = 52;
    private final Integer suitSize = 13;
    private final Integer cardHeight = 780;
    private final Integer cardWidth = 560;
    private final Integer boardColumns = 8;
    private final Integer freeCells = 4;
    private final Integer cardMargin = 56;
    private float startY = 0;
    private float y = 0;

    Card dragging;
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

    
    public int chainLength(Card card) {
        return board.get(card.col).cards.size() - board.get(card.col).cards.indexOf(card);
    }
    
    // See :
    // https://boardgames.stackexchange.com/questions/45155/freecell-how-many-cards-can-be-moved-at-once
    // For formula for spaces required to move chains

    public boolean canMoveChain(Column dst, Integer chainLength) {
        if (countEmptyColumns(dst) == 0) {
            return countFreeCells() + 1 >= chainLength ? true : false;
        } else {
            return (Math.pow(2.0D , countEmptyColumns(dst)) * (countFreeCells() + 1) >= chainLength) ? true : false;
        }
    }

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

    public void refreshBoard() {
        board.forEach(b -> {
            y = startY;
            if (b.index >= boardColumns) y = b.hitbox.y; //This is a free or home cell
            b.cards.forEach(c -> {c.image.setPosition(b.hitbox.x, y);y = y - 160;});
            refreshColumn(b.cards);
            if (!b.cards.isEmpty()) b.populateHitBoxes();
        });
    }
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

    Deck() {
        for (int c = 0; c < deckSize; ++c) {
            int a = c % 13;
            int b = c / 13;
            int srcX = a * cardWidth;
            int srcY = b * cardHeight;
            deck[c] = new Card(a, b, new Sprite(cardTileSet, srcX, srcY, cardWidth, cardHeight));
        }
    }
}
