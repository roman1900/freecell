package au.com.redmars;

import java.util.Objects;
import java.util.Optional;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Freecell extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	Deck d;
	ShapeRenderer shapeRenderer;
	Viewport viewport;
	Vector3 currentMouse;
	Color cursorColor;

	@Override
	public void create() {
		batch = new SpriteBatch();
		d = new Deck();
		float width = (d.getCardWidth() * 8) + (d.getCardMargin() * 11);
		camera = new OrthographicCamera(width, width * .6F);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
		d.Deal(camera);
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		System.out.printf("new size: %d x %d\n", width, height);
	}

	public void drawCells() {
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		for (int fc = 0; fc < 4; ++fc) {
			shapeRenderer.rect((d.getCardMargin() * 2) + (560 * fc) + (d.getCardMargin() * .5F * fc),
					camera.viewportHeight - d.getCardMargin() - d.getCardHeight(), d.getCardWidth(), d.getCardHeight());
		}
		for (int fc = 4; fc < 8; ++fc) {
			shapeRenderer.rect((d.getCardMargin() * 5.5F) + (560 * fc) + (d.getCardMargin() * .5F * fc),
					camera.viewportHeight - d.getCardMargin() - d.getCardHeight(), d.getCardWidth(), d.getCardHeight());
		}
		shapeRenderer.end();
	}

	@Override
	public void render() {
		ScreenUtils.clear(0.3F, 1, 0.3F, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		drawCells();
		batch.begin();
		batch.disableBlending();
		d.nboard.forEach(column -> column.cards.forEach(card -> {
			card.image.draw(batch);
		}));
		batch.end();
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			d.Deal(camera);
		}
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			Gdx.app.exit();
		}
		if (Gdx.input.isTouched()) {
			shapeRenderer.begin(ShapeType.Filled);
			currentMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(currentMouse);
			if (Gdx.input.justTouched()) { //Trying to Grab something
				cursorColor = Color.RED;
				Optional<Column> column = d.nboard.stream()
						.filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
				column.ifPresent(c -> {
					if (Gdx.input.justTouched()) {
						c.touchingCard(currentMouse).ifPresent(f -> {
							if (f.canGrab) {
								cursorColor = Color.PURPLE;
								System.out.printf("Grabbing: %s\n", f.toString());
								d.dragging = f;
							}
						});
					}
				});
			}
			shapeRenderer.setColor(cursorColor);
			shapeRenderer.rect(currentMouse.x - 25, currentMouse.y - 25, 50, 50);
			shapeRenderer.end();
		}
		else {
			Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(mouse);
			if (!Objects.isNull(d.dragging)) {
				Optional<Column> destination = d.nboard.stream()
						.filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
				destination.ifPresent(dst -> {
					if (dst.cards.isEmpty() || d.dragging.canDropHere(dst.cards.get(dst.cards.size()-1))) {
						//TODO: Check if enough space exists to move the chain of cards
						System.out.printf("Dropping onto column: %d\n", dst.index);
					}
				});
				d.dragging = null;
			}
			// if (!Objects.isNull(d.dragging)) {
			// Arrays.asList(d.deck).stream()
			// .filter(c -> c != d.dragging && d.isLastCard(c) && d.dragging.canDropHere(c)
			// && d.canMoveChain(d.dragging))
			// .forEach(c -> {if (c.hitbox.contains(mouse.x,mouse.y)) {
			// //TODO: Move Card from one board row to another
			// System.out.println("Can drop: "+d.dragging.toString()+" here:
			// "+c.toString());
			// d.moveChain(d.dragging, c);
			// }
			// });
			// System.out.println("Stopped dragging: "+d.dragging.toString());
			// d.dragging = null;
			// }
		}

	}

	@Override
	public void dispose() {
		batch.dispose();
		d.cardTileSet.dispose();
		shapeRenderer.dispose();
	}
}
