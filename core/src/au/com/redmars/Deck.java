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
    List<List<Card>> board = new ArrayList<>();
    Texture cardTileSet = new Texture("classic_13x4x560x780.png");

    public void print() {
        for (int fc = boardColumns; fc < boardColumns + freeCells; ++fc) {
            System.out.printf("%3d.%d", board.get(fc).get(0).isFreeCell ? -1 : board.get(fc).get(0).faceValue, board.get(fc).get(0).isFreeCell ? -1 :board.get(fc).get(0).suit);
        }
        System.out.println();
        for (int i = 0; i < maxColLength; ++i) {
            for (int b = 0; b < boardColumns; ++b) {
                if (board.get(b).size() > i)
                    System.out.printf("%3d.%d", board.get(b).get(i).faceValue, board.get(b).get(i).suit);
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
            result = board.get(i).isEmpty() || board.get(i).get(0).isFreeCell ? result + 1 : result;
        }
        return result;
    }
    public int countEmptyColumns() {
        int result = 0;
        for (int i = 0; i < boardColumns; ++i) {
            result = board.get(i).isEmpty() ? result + 1 : result ;
        }
        return result;
    }
    // See : https://boardgames.stackexchange.com/questions/45155/freecell-how-many-cards-can-be-moved-at-once
    // For formula for spacew required to move chains
    public boolean canMoveChain(Card card) {
        Integer chainLength = board.get(card.col).size() - board.get(card.col).indexOf(card);
        if (countEmptyColumns() == 0) {
            return countFreeCells()+1 >= chainLength ? true : false;
        }
        else {
            return ((2^countEmptyColumns())*(countFreeCells()+1) >= chainLength) ? true : false;
        }
    }

    public boolean isLastCard(Card c) {
        return board.get(c.col).contains(c) && board.get(c.col).indexOf(c) == board.get(c.col).size() - 1;
    }

    public void Deal(OrthographicCamera camera) {
        board.clear();
        Random r = new Random();
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
            board.get(i % boardColumns).add(deck[i]);
            deck[i].col = i % boardColumns;
            if (board.get(i % boardColumns).size() > maxColLength)
                maxColLength++;
        }
        for (int row = 0; row < boardColumns; ++row) {
            board.get(row).forEach(card -> {
                card.image.setPosition(x, y);
                y = y - 160;
            });
            List<Card> col = board.get(row);
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
                }
                topCard = c;
            }
            x = x + cardWidth + cardMargin;
            y = startY;
        }
        for (int c = deckSize; c < deckSize + 4; ++c) {
            deck[c] = new Card();
            board.add(new ArrayList<Card>());
            board.get(boardColumns+c-deckSize).add(deck[c]);
            deck[c].col = boardColumns+c-deckSize;
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
