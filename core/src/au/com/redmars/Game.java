package au.com.redmars;

import java.util.Objects;
import java.util.Optional;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	Freecell d;
	ShapeRenderer shapeRenderer;
	Viewport viewport;
	Vector3 currentMouse;
	Color cursorColor;
	float width;
	float height;
	@Override
	public void create() {
		batch = new SpriteBatch();
		d = new Freecell();
		float ratio = Gdx.graphics.getDisplayMode().width / Gdx.graphics.getDisplayMode().height; 
		width = Gdx.graphics.getDisplayMode().width; //(d.getCardWidth() * 8) + (d.getCardMargin() * 11);
		height = Gdx.graphics.getDisplayMode().height;
		// if (width > Gdx.graphics.getDisplayMode().width) {
		// 	height = width / ratio;
		// } else {
		// 	width = Gdx.graphics.getDisplayMode().width;
		// }
		camera = new OrthographicCamera(width * 2.0F, height * 2.0F);
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

	

	@Override
	public void render() {
		ScreenUtils.clear(0.3F, 1, 0.3F, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		d.drawBoard(batch,shapeRenderer,camera);
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			d.Deal(camera);
		}
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyJustPressed(Keys.M)) {
			if (Gdx.graphics.getWidth() != width) {
				Gdx.graphics.setWindowedMode((int)width, (int)height);
			} 
		}
		if (Gdx.input.isKeyJustPressed(Keys.B)) {
			Gdx.graphics.setWindowedMode(0, 0);
		}
		if (Gdx.input.isTouched()) {
			shapeRenderer.begin(ShapeType.Filled);
			currentMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(currentMouse);
			if (Gdx.input.justTouched()) { //Trying to Grab something or right click to view something
				cursorColor = Color.RED;
				Optional<Column> column = d.board.stream()
						.filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
				column.ifPresent(c -> {
					if (Gdx.input.justTouched()) {
						c.touchingCard(currentMouse).ifPresent(f -> {
							if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) { //Trying to view card
								d.viewing = f;
							} else if (f.canGrab) {
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
				Optional<Column> destination = d.board.stream()
						.filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
				destination.ifPresentOrElse(dst -> {
					if (dst.cards.isEmpty() || d.dragging.canDropHere(dst.cards.get(dst.cards.size()-1))) {
						int chainLength = d.chainLength(d.dragging);
						if ((chainLength > 1 && dst.maxCards > 1 && d.canMoveChain(dst, chainLength)) || (chainLength == 1 && dst.cards.size() < dst.maxCards) ) {
							System.out.printf("Moving Chain of %d Cards to Column %d starting with Card %s\n", chainLength, dst.index, d.dragging.toString());
							d.moveChain(d.dragging, dst);
						}
					}
				}, () -> {
					Optional<Column> homecell = d.homeCells.stream()
						.filter(b -> b.hitbox.contains(currentMouse.x, currentMouse.y)).findFirst();
					homecell.ifPresent(x -> {
						if (x.index == d.dragging.suit && ((x.cards.isEmpty() && d.dragging.faceValue == 0) || (!x.cards.isEmpty() && x.cards.get(x.cards.size() -1).faceValue == d.dragging.faceValue - 1))) {
							x.cards.add(d.dragging);
							d.board.get(d.dragging.col).cards.remove(d.dragging);
							d.refreshBoard();
							d.dragging.image.setPosition(x.hitbox.x, x.hitbox.y);
						}
					});
				});
				d.dragging = null;
				d.autoComplete();
			}
			d.viewing = null;
		}
		if (!Objects.isNull(d.viewing)) {
			batch.begin();
			d.viewing.image.draw(batch);
			batch.end();
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		d.cardTileSet.dispose();
		shapeRenderer.dispose();
	}
}
