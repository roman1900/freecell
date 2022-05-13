package au.com.redmars;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Card {
    Integer faceValue;
    Integer colour; // 0 - Black, 1 - Red
    Integer suit; // 0 - Clubs, 1 - Diamonds, 2 - Hearts, 3 - Spades
    Sprite image;
    private static final int FRAME_COLS = 8;
    private static final int FRAME_ROWS = 1;
    Animation<TextureRegion> animation;
    Texture animationSheet;
    TextureRegion frame;
    boolean canGrab;
    Integer col;
    float stateTime;

    public boolean isChained(Card card) {
        return faceValue - card.faceValue == 1 && colour != card.colour;
    }

    public boolean canDropHere(Card card) {
        return card.faceValue - faceValue == 1 && colour != card.colour;
    }

    private String suitString() {
        switch (suit) {
            case 0:
                return "Hearts";
            case 1:
                return "Diamonds";
            case 2:
                return "Spades";
            case 3:
                return "Clubs";
            default:
                return "Dunno";
        }
    }

    private String cardColour() {
        return colour == 0 ? "Black" : "Red";
    }

    public void createAnimation(Texture animationSheet) {
        this.animationSheet = animationSheet;
        TextureRegion[][] tmp = TextureRegion.split(animationSheet, 
            animationSheet.getWidth() / FRAME_COLS,
            animationSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] aniFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                aniFrames[index++] = tmp[i][j];
            }
        }
        animation = new Animation<TextureRegion>(.1f, aniFrames);
        stateTime = 0f;
    }

    public TextureRegion getFrame(float delta) {
        stateTime += delta;
        return animation.getKeyFrame(stateTime, true);
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
        this.animation = null;
    }

    Card() {
        canGrab = false;
    }

    public String toString() {
        return String.format("{Card: {faceValue: %d,colour: %s,suit: %s,image: {},canGrab: %b, Column: %d}}",
                faceValue, cardColour(), suitString(), canGrab, col);
    }
}
