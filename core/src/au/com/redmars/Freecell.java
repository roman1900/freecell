package au.com.redmars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Freecell implements Tableau {

    private final Integer boardColumns = 8;
    private final Integer freeCells = 4;
    private float startY = 0;
    private float y = 0;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Vector3 currentMouse;
    private Color cursorColor;
    private Batch batch;
    private Integer cardGap;
    private float boardMargin;
    private Vector3 startDragMousePos;
    Card dragging;
    Card viewing;
    Card[] deck = new Card[deckSize + 4];
    List<List<Column>> undo = new ArrayList<>();
    List<Column> board = new ArrayList<>();
    List<Column> homeCells = new ArrayList<>();
    Texture cardTileSet = new Texture("classic_13x4x560x780.png");
    Sound pickupSound;
    Sound putDownSound;

    public int getCardMargin() {
        return cardMargin;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public float getBoardMargin() {
        return boardMargin;
    }

    public void setBoardMargin(float margin) {
        boardMargin = margin;
    }

    public long countFreeCells() {
        return board.stream().filter(x -> x.maxCards == 1).filter(x -> x.cards.isEmpty()).count();
    }

    public long countEmptyColumns(Column dst) {
        return board.stream().filter(x -> x.maxCards != 1).filter(x -> x.cards.isEmpty()).filter(x -> !x.equals(dst))
                .count();
    }

    @Override
    public int chainLength(Card card) {
        return board.get(card.col).cards.size() - board.get(card.col).cards.indexOf(card);
    }

    // See :
    // https://boardgames.stackexchange.com/questions/45155/freecell-how-many-cards-can-be-moved-at-once
    // For formula for spaces required to move chains

    @Override
    public boolean canMoveChain(Column dst, Integer chainLength) {
        if (countEmptyColumns(dst) == 0) {
            return countFreeCells() + 1 >= chainLength ? true : false;
        } else {
            return (Math.pow(2.0D, countEmptyColumns(dst)) * (countFreeCells() + 1) >= chainLength) ? true : false;
        }
    }

    @Override
    public void moveChain(Card src, Column dst) {

        Column srcCol = board.get(src.col);
        List<Card> movingCards = srcCol.cards.subList(srcCol.cards.indexOf(src), srcCol.cards.size());
        movingCards.forEach(c -> {
            c.col = dst.index;
        });
        dst.cards.addAll(movingCards);
        srcCol.cards.removeAll(movingCards);
    }

    public void drawChain(Card src, Vector3 currentMouse) {
        Vector3 offset = new Vector3(startDragMousePos);
        offset.sub(currentMouse);
        Column srcCol = board.get(src.col);
        List<Card> movingCards = srcCol.cards.subList(srcCol.cards.indexOf(src), srcCol.cards.size());
        batch.begin();
        movingCards.forEach(c -> {
            c.image.setX(c.image.getX() - offset.x);
            c.image.setY(c.image.getY() - offset.y);
            c.image.draw(batch);
        });
        batch.end();
        startDragMousePos = new Vector3(currentMouse);
    }

    private Integer lowestHomeCell() {
        if (homeCells.stream().filter(c -> c.cards.isEmpty()).findFirst().isPresent())
            return 1;
        return homeCells.stream()
                .min((b, c) -> b.cards.get(b.cards.size() - 1).faceValue - c.cards.get(c.cards.size() - 1).faceValue)
                .get().cards.stream().map(c -> c.faceValue).max(Integer::compare).get() + 1;
    }

    public Integer nextHomeCellCard(List<Card> cardList) {
        if (cardList.isEmpty())
            return 0;
        return cardList.get(cardList.size() - 1).faceValue + 1;
    }

    private Card lastCard(List<Card> cardList) {
        return cardList.get(cardList.size() - 1);
    }

    @Override
    public void autoComplete() {
        board.stream().filter(c -> !c.cards.isEmpty()).forEach(c -> {
            Card last = lastCard(c.cards);
            Column hc = homeCells.get(last.suit);
            if (last.faceValue <= lowestHomeCell() && nextHomeCellCard(hc.cards) == last.faceValue) {
                hc.cards.add(last);
                c.cards.remove(last);
                refreshBoard();
                putDownSound.play();
                last.image.setPosition(hc.hitbox.x, hc.hitbox.y);
                autoComplete();
            }
        });
    }

    @Override
    public void refreshBoard() {
        board.forEach(b -> {
            y = startY;
            if (b.index >= boardColumns)
                y = b.hitbox.y; // This is a free or home cell
            cardGap = 160; // The gap between cards in a column
            while (y - (cardGap * (b.cards.size() - 1)) < 0 && cardGap > 20) { // Adjust the gap if the cards would be
                                                                               // placed off screen
                cardGap -= 5;
            }
            b.cards.forEach(c -> {
                c.image.setPosition(b.hitbox.x, y);
                y = y - cardGap;
            });
            refreshColumn(b.cards);
            if (!b.cards.isEmpty())
                b.populateHitBoxes(cardGap);
        });
        List<Column> copy = new ArrayList<>();
        board.forEach(c -> copy.add((Column)c.clone()));
        undo.add(copy);
        System.out.println("UNDO LIST:  ----------");
        undo.forEach(c -> System.out.println(c.toString()));
    }

    @Override
    public void refreshColumn(List<Card> col) {
        ListIterator<Card> iterator = col.listIterator(col.size());
        Card topCard = new Card();
        Boolean endOfList = true;
        while (iterator.hasPrevious()) {

            Card c = iterator.previous();

            if (endOfList) { // The last card can always be grabbed
                c.canGrab = true;
                endOfList = false;
            } else if (topCard.canGrab && c.isChained(topCard)) {
                c.canGrab = true;
            } else {
                c.canGrab = false;
            }
            topCard = c;
        }
    }

    @Override
    public void setupBoard() {
        board.clear();
        homeCells.clear();
        float y = camera.viewportHeight - cardMargin * 2 - cardHeight;
        float x = boardMargin;
        for (int i = 0; i < boardColumns; ++i) { // 8 playing columns
            board.add(new Column(i, deckSize, new Rectangle(x, 0, cardWidth, y)));
            x = x + cardWidth + cardMargin;
        }
        for (int i = 0; i < freeCells; ++i) { // 4 Freecell columns
            Rectangle hitbox = new Rectangle((cardMargin * 2) + (560 * i) + (cardMargin * .5F * i),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
            board.add(new Column(i + boardColumns, 1, hitbox));
        }
        for (int i = 4; i < 8; ++i) { // 4 Home cells
            Rectangle hitbox = new Rectangle((cardMargin * 5.5F) + (560 * i) + (cardMargin * .5F * i),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
            homeCells.add(new Column(i - 4, suitSize, hitbox));
        }

    }

    @Override
    public void Deal() {
        setupBoard();
        Random seeder = new Random();
        Long seed = seeder.nextLong();
        System.out.printf("Current seed: %d\n", seed);
        Random r = new Random(seed);
        startY = camera.viewportHeight - cardMargin * 2 - cardHeight * 2;
        for (int i = deckSize - 1; i > 0; i--) {

            // Pick a random index from 0 to i
            int j = r.nextInt(i);

            // Swap arr[i] with the element at random index
            Card temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
        for (int i = 0; i < deckSize; ++i) {
            board.get(i % boardColumns).cards.add(deck[i]);
            deck[i].col = i % boardColumns;
            putDownSound.play();
        }
        refreshBoard();
    }

    @Override
    public void drawBoard() {
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (int fc = 0; fc < 4; ++fc) {
            shapeRenderer.rect((cardMargin * 2) + (560 * fc) + (cardMargin * .5F * fc),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
        }
        for (int fc = 4; fc < 8; ++fc) {
            shapeRenderer.rect((cardMargin * 5.5F) + (560 * fc) + (cardMargin * .5F * fc),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
        }
        shapeRenderer.end();
        batch.begin();
        batch.disableBlending();
        board.forEach(column -> column.cards.forEach(card -> {
            card.image.draw(batch);
        }));
        homeCells.forEach(hc -> {
            if (!hc.cards.isEmpty()) {
                hc.cards.get(hc.cards.size() - 1).image.draw(batch);
            }
        });
        batch.end();
    }

    @Override
    public void setPickupSound(Sound sound) {
        pickupSound = sound;
    }

    @Override
    public void setPutDownSound(Sound sound) {
        putDownSound = sound;
    }

    @Override
    public Sound getPickupSound() {
        return pickupSound;
    }

    @Override
    public Sound getPutDownSound() {
        return putDownSound;
    }

    @Override
    public void touchEvent() {
        shapeRenderer.begin(ShapeType.Filled);
        currentMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(currentMouse);
        if (Gdx.input.justTouched()) { // Trying to Grab something or right click to view something
            cursorColor = Color.RED;
            Optional<Column> column = board.stream()
                    .filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
            column.ifPresent(c -> {
                if (Gdx.input.justTouched()) {
                    c.touchingCard(currentMouse).ifPresent(f -> {
                        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) { // Trying to view card
                            viewing = f;
                        } else if (f.canGrab) {
                            pickupSound.play();
                            startDragMousePos = new Vector3(currentMouse);
                            cursorColor = Color.PURPLE;
                            dragging = f;
                        }
                    });
                }
            });
        }
        if (Objects.nonNull(dragging) && currentMouse != startDragMousePos) {
            drawChain(dragging, currentMouse);
        }
        shapeRenderer.setColor(cursorColor);
        shapeRenderer.rect(currentMouse.x - 25, currentMouse.y - 25, 50, 50);
        shapeRenderer.end();
    }

    @Override
    public void moveEvent() {
        currentMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(currentMouse);
        if (!Objects.isNull(dragging)) {
            Optional<Column> destination = board.stream()
                    .filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
            destination.ifPresentOrElse(dst -> {
                if (dst.cards.isEmpty() || dragging.canDropHere(dst.cards.get(dst.cards.size() - 1))) {
                    int chainLength = chainLength(dragging);
                    if ((chainLength > 1 && dst.maxCards > 1 && canMoveChain(dst, chainLength))
                            || (chainLength == 1 && dst.cards.size() < dst.maxCards)) {
                        moveChain(dragging, dst);
                        putDownSound.play();
                    }
                }
            }, () -> {
                Optional<Column> homecell = homeCells.stream()
                        .filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
                homecell.ifPresent(x -> {
                    if (x.index == dragging.suit
                            && ((x.cards.isEmpty() && dragging.faceValue == 0) || (!x.cards.isEmpty()
                                    && x.cards.get(x.cards.size() - 1).faceValue == dragging.faceValue - 1))) {
                        x.cards.add(dragging);
                        board.get(dragging.col).cards.remove(dragging);
                        putDownSound.play();
                        dragging.image.setPosition(x.hitbox.x, x.hitbox.y);
                    }
                });
            });
            refreshBoard();
            dragging = null;
            startDragMousePos = null;
            autoComplete();
        }
        viewing = null;
    }

    @Override
    public void viewEvent() {
        if (!Objects.isNull(viewing)) {
            batch.begin();
            viewing.image.draw(batch);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        cardTileSet.dispose();
        putDownSound.dispose();
        pickupSound.dispose();
    }

    Freecell(Batch batch, OrthographicCamera camera, ShapeRenderer shapeRenderer) {
        this.camera = camera;
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.boardMargin = camera.viewportWidth / 2 - (cardWidth * 4.0F + cardMargin * 3.5F);
        for (int c = 0; c < deckSize; ++c) {
            int a = c % 13;
            int b = c / 13;
            int srcX = a * cardWidth;
            int srcY = b * cardHeight;
            deck[c] = new Card(a, b, new Sprite(cardTileSet, srcX, srcY, cardWidth, cardHeight));
        }
    }

    @Override
    public void undoMove() {
        undo.forEach(x -> System.out.printf("Hashcode: %s\n", x.hashCode()));
        undo.remove(undo.size() - 1);
        board = new ArrayList<>();
        undo.get(undo.size() - 1).forEach(c -> board.add((Column)c.clone()));
        refreshBoard();
    }

}
