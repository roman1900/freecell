package au.com.redmars;

import java.util.ArrayList;
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
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    private List<Sprite> homeCellImages = new ArrayList<>();
    private Boolean gameover = false;

    
    Card dragging;
    Card viewing;
    Card[] deck = new Card[deckSize + 4];
    List<Column> board = new ArrayList<>();
    List<Column> homeCells = new ArrayList<>();
    Texture smallCardTileSet = new Texture("cards/Plebes_Update003-FIX_1x_V1_moved_face_colour_match.png");
    Texture largeCardTileSet = new Texture("cards/Plebes_Update003-FIX_4x_V1.png");
    Texture smallCardBack = new Texture("cards/PixelPlebes_V1_1x__Card_Back.png");
    Sound pickupSound;
    Sound putDownSound;
    Sound noGoSound;
    Undo undo;

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
    public void moveChain(Card src, Column dst, List<Undo.Location> turn) {

        Column srcCol = board.get(src.col);
        List<Card> movingCards = srcCol.cards.subList(srcCol.cards.indexOf(src), srcCol.cards.size());
        movingCards.forEach(c -> {
            Undo.Location l = undo.new Location(c, c.col);
            turn.add(l);
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

    private Boolean canAutoMoveHome(Card c) {
        List<Column> suitHC = new ArrayList<>();
        Integer lowest;
        switch (c.colour) {
            case 0:
            case 3:
                suitHC.add(homeCells.get(1));
                suitHC.add(homeCells.get(2));
                break;
            case 1:
            case 2:
                suitHC.add(homeCells.get(0));
                suitHC.add(homeCells.get(3));
                break;
        }
        if (suitHC.stream()
                .anyMatch(x -> x.cards.isEmpty())) {
            lowest = 1;
        } else {
            lowest = suitHC.stream()
                    .min((b, d) -> b.cards.get(b.cards.size() - 1).faceValue
                            - d.cards.get(d.cards.size() - 1).faceValue)
                    .get().cards.stream().map(d -> d.faceValue).max(Integer::compare).get() + 1;
        }
        return c.faceValue <= lowest;
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
    public void autoComplete(List<Undo.Location> turn) {
        board.stream().filter(c -> !c.cards.isEmpty()).forEach(c -> {
            Card last = lastCard(c.cards);
            Column hc = homeCells.get(last.suit);
            if (nextHomeCellCard(hc.cards) == last.faceValue && canAutoMoveHome(last)) {
                Undo.Location l = undo.new Location(last, last.col);
                turn.add(l);
                hc.cards.add(last);
                c.cards.remove(last);
                refreshBoard();
                putDownSound.play();
                last.image.setPosition(hc.hitbox.x, hc.hitbox.y);
                autoComplete(turn);
            }
        });
        gameover = true;
        board.forEach(x -> {if (!x.cards.isEmpty()){gameover=false;} } );
    }

    @Override
    public void refreshBoard() {
        board.forEach(b -> {
            y = startY;
            if (b.index >= boardColumns)
                y = b.hitbox.y; // This is a free or home cell
            cardGap = (int)(cardHeight * .3); // The gap between cards in a column
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
        homeCellImages.clear();
        float y = camera.viewportHeight - cardMargin * 2 - cardHeight;
        float x = boardMargin;
        for (int i = 0; i < boardColumns; ++i) { // 8 playing columns
            board.add(new Column(i, deckSize, new Rectangle(x, 0, cardWidth, y)));
            x = x + cardWidth + cardMargin;
        }
        for (int i = 0; i < freeCells; ++i) { // 4 Freecell columns
            Rectangle hitbox = new Rectangle(boardMargin + (cardWidth * i) + (cardMargin * .5F * i),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
            board.add(new Column(i + boardColumns, 1, hitbox));
        }
        for (int i = 4; i < 8; ++i) { // 4 Home cells
            Rectangle hitbox = new Rectangle((boardMargin + cardMargin * 3.5F) + (cardWidth * i) + (cardMargin * .5F * i),
                    camera.viewportHeight - cardMargin - cardHeight, cardWidth, cardHeight);
            homeCells.add(new Column(i - 4, suitSize, hitbox));
            Sprite s = new Sprite(smallCardTileSet, 0, cardHeight * (i - 4), cardWidth, cardHeight);
            s.setPosition(hitbox.x, hitbox.y);
            s.setAlpha(0.35f);
            homeCellImages.add(s);
        }
    }

    @Override
    public void Deal() {
        setupBoard();
        gameover = false;
        this.undo = new Undo();
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
            //putDownSound.play();
        }
        refreshBoard();
    }

    @Override
    public void drawBoard() {
        batch.begin();
        for (int fc = 0; fc < 4; ++fc) {
            batch.draw(smallCardBack, boardMargin + (cardWidth * fc) + (cardMargin * .5F * fc), camera.viewportHeight - cardMargin - cardHeight);
        }
        for (int fc = 4; fc < 8; ++fc) {
            homeCellImages.get(fc - 4).draw(batch);
        }
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
    public void setNoGoSound(Sound sound) {
        noGoSound = sound;
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
    public Sound getNoGoSound() {
        return noGoSound;
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
    public Boolean moveEvent() {
        currentMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(currentMouse);
        if (!Objects.isNull(dragging)) {
            // Add a new turn to the undo stack
            List<Undo.Location> turn = new ArrayList<>();
            Optional<Column> destination = board.stream()
                    .filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
            destination.ifPresentOrElse(dst -> {
                if (dst.cards.isEmpty() || dragging.canDropHere(dst.cards.get(dst.cards.size() - 1))) {
                    int chainLength = chainLength(dragging);
                    if ((chainLength > 1 && dst.maxCards > 1 && canMoveChain(dst, chainLength))
                            || (chainLength == 1 && dst.cards.size() < dst.maxCards)) {
                        moveChain(dragging, dst, turn);
                        putDownSound.play();
                    }
                } else {
                    noGoSound.play();
                }
            }, () -> {
                Optional<Column> homecell = homeCells.stream()
                        .filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
                homecell.ifPresent(x -> {
                    if (x.index == dragging.suit
                            && ((x.cards.isEmpty() && dragging.faceValue == 0) || (!x.cards.isEmpty()
                                    && x.cards.get(x.cards.size() - 1).faceValue == dragging.faceValue - 1))) {
                        Undo.Location l = undo.new Location(dragging, dragging.col);
                        x.cards.add(dragging);
                        board.get(dragging.col).cards.remove(dragging);
                        putDownSound.play();
                        dragging.image.setPosition(x.hitbox.x, x.hitbox.y);
                        turn.add(l);
                    } else {
                        noGoSound.play();
                    }
                });
            });
            refreshBoard();
            dragging = null;
            startDragMousePos = null;
            autoComplete(turn);
            if (turn.size() > 0)
                undo.turns.add(turn);
        }
        viewing = null;
        return gameover;
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
        smallCardTileSet.dispose();
        smallCardBack.dispose();
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
            deck[c] = new Card(a, b, new Sprite(smallCardTileSet, srcX, srcY, cardWidth, cardHeight));
            deck[c].image.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }
    }

    @Override
    public void undoMove() {
        if (undo.turns.size() > 0) {
            List<Undo.Location> lastTurn = undo.turns.get(undo.turns.size() - 1);
            lastTurn.forEach(l -> {
                Column src = board.get(l.card.col);
                Column dst = board.get(l.previousColumn);
                if (src == dst) { // Is now on a home cell
                    src = homeCells.get(l.card.suit);
                } else {
                    l.card.col = l.previousColumn;
                }
                dst.cards.add(l.card);
                src.cards.remove(l.card);
            });
            undo.turns.remove(lastTurn);
            refreshBoard();
        }
    }

}
