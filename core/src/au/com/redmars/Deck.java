package au.com.redmars;

import java.util.Random;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Deck {
    private final Integer deckSize = 52;
    private final Integer cardHeight = 780;
    private final Integer cardWidth = 560;
    Card[] deck = new Card[deckSize];
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
        float x = 10.0F;
		float startY = camera.viewportHeight - 30 - cardHeight * 2;
		float y = startY;
		for (int i = 0; i < deck.length; ++i) {
			deck[i].image.setPosition(x, y);
			x = x + cardWidth + 10;
			if (x > camera.viewportWidth - cardWidth) x = 10;
			y = startY - (160 * ((i + 1)/ 8));
			 
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
