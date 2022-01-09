package au.com.redmars;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Card {
    Integer faceValue;
    Integer suit;
    Sprite image;
    Card(Integer faceValue, Integer suit,Sprite image) {
        this.faceValue = faceValue;
        this.suit = suit;
        this.image = image;
    }
}
