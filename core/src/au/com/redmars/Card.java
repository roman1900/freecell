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
    boolean isFreeCell;
    Integer row;
    public boolean isChained(Card card) {
        return faceValue - card.faceValue == 1 && colour != card.colour;
    }
    public boolean canDropHere(Card card) {
        return card.isFreeCell || (card.faceValue - faceValue == 1 && colour != card.colour);
    }
    private String suitString() {
        switch (suit) {
            case 0:
                return "Clubs";
            case 1:
                return "Diamonds";
            case 2:
                return "Hearts";
            case 3:
                return "Spades";
            default:
                return "Dunno";
        }
    }
    private String cardColour() {
        return colour == 0 ? "Black" : "Red";
    }
    public String toString() {
        return isFreeCell ? String.format("FreeCell %d",row) :
        String.format("Value:%2d Colour: %s Suit: %s Row: %d", faceValue, cardColour(), suitString(), row);
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
        this.isFreeCell = false;
    }
    Card() {
        canGrab = false;
        isFreeCell = true;
    }
}
