package au.com.redmars;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	Tableau solitaire;
	ShapeRenderer shapeRenderer;
	Viewport viewport;
	Vector3 currentMouse;
	Color cursorColor;
	float width;
	float height;
	Sound draw;
	Sound playcard;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		// float ratio = Gdx.graphics.getDisplayMode().width /
		// Gdx.graphics.getDisplayMode().height;
		width = Gdx.graphics.getDisplayMode().width; // (d.getCardWidth() * 8) + (d.getCardMargin() * 11);
		height = Gdx.graphics.getDisplayMode().height;
		// if (width > Gdx.graphics.getDisplayMode().width) {
		// height = width / ratio;
		// } else {
		// width = Gdx.graphics.getDisplayMode().width;
		// }
		camera = new OrthographicCamera(width * 2.0F, height * 2.0F);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
		solitaire = new Freecell(batch, camera, shapeRenderer);
		solitaire.Deal();
		solitaire.setPickupSound(Gdx.audio.newSound(Gdx.files.internal("draw.wav")));
		solitaire.setPutDownSound(Gdx.audio.newSound(Gdx.files.internal("playcard.wav")));
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
		solitaire.drawBoard();
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			solitaire.Deal();
		}
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyJustPressed(Keys.M)) {
			if (Gdx.graphics.getWidth() != width) {
				Gdx.graphics.setWindowedMode((int) width, (int) height);
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.B)) {
			Gdx.graphics.setWindowedMode(0, 0);
		}
		if (Gdx.input.isTouched()) {
			solitaire.touchEvent();
		} else {
			solitaire.moveEvent();
		}
		solitaire.viewEvent();
	}

	@Override
	public void dispose() {
		batch.dispose();
		solitaire.dispose();
		shapeRenderer.dispose();
		solitaire.getPickupSound().dispose();
		solitaire.getPutDownSound().dispose();
	}
}
