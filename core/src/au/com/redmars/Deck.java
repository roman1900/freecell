package au.com.redmars;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Deck {
    private final Integer deckSize = 52;
    private final Integer cardHeight = 780;
    private final Integer cardWidth = 560;
    Card[] deck = new Card[deckSize];
    Texture cardTileSet = new Texture("modern_13x4x560x780.png");

    public void Deal() {
        Random r = new Random();
        for (int i = deckSize-1; i > 0; i--) {
              
            // Pick a random index from 0 to i
            int j = r.nextInt(i);
              
            // Swap arr[i] with the element at random index
            Card temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
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
