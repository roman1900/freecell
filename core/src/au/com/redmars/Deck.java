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
    private final Integer cardHeight = 780;
    private final Integer cardWidth = 560;
    private final Integer boardColumns = 8;
    private final Integer freeCells = 4;
    private final Integer cardMargin = 56;
    private float y = 0;
    private float x = 0;
    private Integer maxColLength = 0;

    Card dragging;
    Card[] deck = new Card[deckSize + 4];
    List<Column> nboard = new ArrayList<>(); 
    List<List<Card>> board = new ArrayList<>();
    Texture cardTileSet = new Texture("classic_13x4x560x780.png");

    public void print() {
        for (int fc = boardColumns; fc < boardColumns + freeCells; ++fc) {
            System.out.printf("%3d.%d", nboard.get(fc).cards.isEmpty() ? 0 : nboard.get(fc).cards.get(0).faceValue,
            nboard.get(fc).cards.isEmpty() ? 0 : nboard.get(fc).cards.get(0).suit);
        }
        System.out.println();
        for (int i = 0; i < maxColLength; ++i) {
            for (int b = 0; b < boardColumns; ++b) {
                if (nboard.get(b).cards.size() > i)
                    System.out.printf("%3d.%d", nboard.get(b).cards.get(i).faceValue, nboard.get(b).cards.get(i).suit);
            }
            System.out.println();
        }
    }

    public int getCardMargin() {
        return cardMargin;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public int countFreeCells() {  
        int result = 0;
        for (int i = boardColumns; i < boardColumns + freeCells; ++i) {
            result = board.get(i).isEmpty() ? result + 1 : result;
        }
        return result;
    }

    public int countEmptyColumns() { //TODO: Exclude the destination column
        int result = 0;
        for (int i = 0; i < boardColumns; ++i) {
            result = board.get(i).isEmpty() ? result + 1 : result;
        }
        return result;
    }

    // See :
    // https://boardgames.stackexchange.com/questions/45155/freecell-how-many-cards-can-be-moved-at-once
    // For formula for spacew required to move chains
    public boolean canMoveChain(Card card) {
        Integer chainLength = board.get(card.col).size() - board.get(card.col).indexOf(card);
        if (countEmptyColumns() == 0) {
            return countFreeCells() + 1 >= chainLength ? true : false;
        } else {
            return ((2 ^ countEmptyColumns()) * (countFreeCells() + 1) >= chainLength) ? true : false;
        }
    }

    public boolean isLastCard(Card c) {
        return board.get(c.col).contains(c) && board.get(c.col).indexOf(c) == board.get(c.col).size() - 1;
    }

    public void moveChain(Card src, Card dst) {
        
        List<Card> srcCol = board.get(src.col);
        List<Card> dstCol = board.get(dst.col);
        List<Card> movingCards = srcCol.subList(srcCol.indexOf(src), srcCol.size());
        movingCards.forEach(c -> { 
            c.col = dst.col; 
            c.image.setX(dst.image.getX());
            c.image.setY(dst.image.getY() - 160 - (src.image.getY() - c.image.getY()) );
        });
        dstCol.addAll(movingCards);
        srcCol.removeAll(movingCards);
        refreshColumn(dstCol);
        refreshColumn(srcCol);
    }
    public void refreshColumn(List<Card> col) {
        ListIterator<Card> iterator = col.listIterator(col.size());
        Card topCard = new Card();
        Boolean endOfList = true;
        while (iterator.hasPrevious()) {

            Card c = iterator.previous();

            if (endOfList) { // The last card can always be grabbed
                c.canGrab = true;
                c.hitbox = new Rectangle(c.image.getX(), c.image.getY(), cardWidth, cardHeight);
                endOfList = false;
            } else if (topCard.canGrab && c.isChained(topCard)) {
                c.canGrab = true;
                c.hitbox = new Rectangle(c.image.getX(), c.image.getY(), cardWidth, cardHeight);
            } else {
                c.canGrab = false;
                c.hitbox = null;
            }
            topCard = c;
        }
    }
    public void setupBoard(OrthographicCamera camera) {
        nboard.clear();
        float y = camera.viewportHeight - cardMargin * 2 - cardHeight;
        float x = cardMargin * 2;
        for (int i = 0; i < boardColumns; ++i) { //8 playing columns
            nboard.add(new Column(i,52,new Rectangle(x,0,cardWidth,y)));
            x = x + cardWidth + cardMargin;
        }
        for (int i = 0; i < freeCells; ++i) { //4 Freecell columns
            Rectangle hitbox = new Rectangle((cardMargin * 2) + (560 * i) + (cardMargin * .5F * i),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
            nboard.add(new Column(i+boardColumns,1,hitbox));
        }

    }
    public void Deal(OrthographicCamera camera) {
        board.clear();
        setupBoard(camera);
        Random seeder = new Random();
        Long seed = seeder.nextLong();
        System.out.printf("Current seed: %d\n",seed);
        Random r = new Random(seed);
        
        for (int i = deckSize - 1; i > 0; i--) {

            // Pick a random index from 0 to i
            int j = r.nextInt(i);

            // Swap arr[i] with the element at random index
            Card temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
        x = cardMargin * 2;
        float startY = camera.viewportHeight - cardMargin * 2 - cardHeight * 2;
        y = startY;
        for (int i = 0; i < deckSize; ++i) {
            if (i < boardColumns) {
                board.add(new ArrayList<Card>());
            }
            nboard.get(i % boardColumns).cards.add(deck[i]);
            board.get(i % boardColumns).add(deck[i]);
            deck[i].col = i % boardColumns;
            if (board.get(i % boardColumns).size() > maxColLength)
                maxColLength++;
        }
        ///////////
        nboard.forEach(b -> {
            b.cards.forEach(c -> c.image.setPosition(b.hitbox.x, y));
            y = y - 160;
            refreshColumn(b.cards);
            if (!b.cards.isEmpty()) b.populateHitBoxes();
        });
        ///////////
        y = startY;
        for (int col = 0; col < boardColumns; ++col) {
            board.get(col).forEach(card -> {
                card.image.setPosition(x, y);
                y = y - 160;
            });
            refreshColumn(board.get(col));
            x = x + cardWidth + cardMargin;
            y = startY;
        }
        for (int c = deckSize; c < deckSize + 4; ++c) {
            deck[c] = new Card();
            board.add(new ArrayList<Card>());
            board.get(boardColumns + c - deckSize).add(deck[c]);
            deck[c].col = boardColumns + c - deckSize;
        }
        for (int c = deckSize; c < deckSize + 4; ++c) {
            deck[c].hitbox = new Rectangle(
                    (cardMargin * 2) + (560 * (c - deckSize)) + (cardMargin * .5F * (c - deckSize)),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
        }
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
