package au.com.redmars;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Card {
    Integer faceValue;
    Integer colour; // 0 - Black, 1 - Red
    Integer suit; // 0 - Clubs, 1 - Diamonds, 2 - Hearts, 3 - Spades
    Sprite image;
    boolean canGrab;
    Integer col;

    public boolean isChained(Card card) {
        return faceValue - card.faceValue == 1 && colour != card.colour;
    }

    public boolean canDropHere(Card card) {
        return card.faceValue - faceValue == 1 && colour != card.colour;
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

    Card(Integer faceValue, Integer suit, Sprite image) {
        this.faceValue = faceValue;
        this.suit = suit;
        switch (suit) {
            case 0:
                this.colour = 0;
                break;
            case 1:
                this.colour = 0;
                break;
            case 2:
                this.colour = 1;
                break;
            case 3:
                this.colour = 1;
                break;
        }
        this.image = image;
        this.canGrab = false;
    }

    Card() {
        canGrab = false;
    }

    public String toString() {
        return String.format("{Card: {faceValue: %d,colour: %s,suit: %s,image: {},canGrab: %b, Column: %d}}",
                faceValue, cardColour(), suitString(), canGrab, col);
    }
}
