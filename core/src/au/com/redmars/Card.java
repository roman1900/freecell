package au.com.redmars;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Card {
    Integer faceValue;
    Integer suit;
    Sprite image;
    boolean canGrab;
    Rectangle hitbox; 
    Card(Integer faceValue, Integer suit,Sprite image) {
        this.faceValue = faceValue;
        this.suit = suit;
        this.image = image;
        this.canGrab = false;
    }
}
