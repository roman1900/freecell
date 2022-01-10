package au.com.redmars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private float y = 0;
    private float x = 10.0F;

    Card[] deck = new Card[deckSize];
    List<List <Card>> board = new ArrayList<>();
    Texture cardTileSet = new Texture("modern_13x4x560x780.png");

    public void Deal(OrthographicCamera camera) {
        Random r = new Random();
        for (int i = deckSize-1; i > 0; i--) {
              
            // Pick a random index from 0 to i
            int j = r.nextInt(i);
              
            // Swap arr[i] with the element at random index
            Card temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
        x = 10.0F;
		float startY = camera.viewportHeight - 30 - cardHeight * 2;
		y = startY;
        for (int i = 0; i < deck.length; ++i) {
            if (i <boardColumns) {
                board.add(new ArrayList<Card>());
            }
            board.get(i % boardColumns).add(deck[i]);
        }
        for(int row = 0; row < boardColumns; ++row) {
            board.get(row).forEach(card -> {
                card.image.setPosition(x, y);
                y = y - 160;
            });
            List<Card> col = board.get(row);
            Iterator<Card> iterator = col.iterator();
            while (iterator.hasNext()) {
                Card c = iterator.next();
                if (!iterator.hasNext()) {
                    c.canGrab = true;
                    c.hitbox = new Rectangle(c.image.getX(), c.image.getY(), cardWidth, cardHeight);
                }
            }
            x = x + cardWidth + 10;
            y = startY;
        }
    }
    Deck(){
        for (int c = 0; c < deckSize; ++c){
            int a = c % 13;
            int b = c / 13;
            int srcX =  a*cardWidth;
            int srcY = b*cardHeight;

            deck[c] = new Card(a,b,new Sprite(cardTileSet,srcX,srcY,cardWidth,cardHeight));
        }
    }
}
