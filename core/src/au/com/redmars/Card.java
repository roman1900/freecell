package au.com.redmars;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Card {
    Integer faceValue;
    Integer colour; // 0 - Black, 1 - Red
    Integer suit; //0 - Clubs, 1 - Diamonds, 2 - Hearts, 3 - Spades
    Sprite image;
    boolean canGrab;
    Rectangle hitbox; 
    public boolean isChained(Card card) {
        return faceValue - card.faceValue == 1 && colour != card.colour;
    }
    Card(Integer faceValue, Integer suit,Sprite image) {
        this.faceValue = faceValue;
        this.suit = suit;
        switch (suit) {
            case 0:
                this.colour = 0;
                break;
            case 1:
                this.colour = 1;
                break;
            case 2:
                this.colour = 1;
                break;
            case 3:
                this.colour = 0;
                break;
        }
        this.image = image;
        this.canGrab = false;
    }
    Card() {
        canGrab = false;
    }
}
