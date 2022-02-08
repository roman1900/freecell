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
	Integer widthRequired;
	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		width = Gdx.graphics.getWidth(); //getDisplayMode().width; 
		height = Gdx.graphics.getHeight(); //getDisplayMode().height;
		
		widthRequired = Tableau.cardWidth * 8 + Tableau.cardMargin * 11;
		if (widthRequired > width) {
			float ratio = widthRequired / width;
			camera = new OrthographicCamera(widthRequired, height * ratio);	
		}
		else {
			camera = new OrthographicCamera(width, height);
		}
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
		System.out.printf("viewport size: %f x %f\n",viewport.getWorldWidth(),viewport.getWorldHeight());
		solitaire = new Freecell(batch, camera, shapeRenderer);
		solitaire.setPickupSound(Gdx.audio.newSound(Gdx.files.internal("draw.wav")));
		solitaire.setPutDownSound(Gdx.audio.newSound(Gdx.files.internal("playcard.wav")));
		solitaire.Deal();
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
