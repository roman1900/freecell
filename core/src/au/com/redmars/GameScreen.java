package au.com.redmars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen{
	final Solitaire game;
	OrthographicCamera camera;
	Viewport viewport;
	Tableau solitaire;
	float width;
	float height;
	Integer widthRequired;

	public GameScreen(final Solitaire game) {
		this.game = game;
		width = Gdx.graphics.getWidth(); //getDisplayMode().width; 
		height = Gdx.graphics.getHeight(); //getDisplayMode().height;
		widthRequired = Tableau.cardWidth * 11 + Tableau.cardMargin * 11;
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
		solitaire = new Freecell(game.batch, camera, game.shapeRenderer);
		solitaire.setPickupSound(Gdx.audio.newSound(Gdx.files.internal("draw.wav")));
		solitaire.setPutDownSound(Gdx.audio.newSound(Gdx.files.internal("down.wav")));
		solitaire.setNoGoSound(Gdx.audio.newSound(Gdx.files.internal("ding.mp3")));
		solitaire.Deal();
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.3F, 1, 0.3F, 1);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.shapeRenderer.setProjectionMatrix(camera.combined);
		solitaire.drawBoard();
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			solitaire.Deal();
		}
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyJustPressed(Keys.U)) {
			solitaire.undoMove();
		}
		if (Gdx.input.isKeyJustPressed(Keys.B)) {
			if (Gdx.graphics.getWidth() != width) {
				Gdx.graphics.setWindowedMode((int) width, (int) height);
			} else {
				Gdx.graphics.setWindowedMode(0, 0);
			}
		}
		if (Gdx.input.isTouched()) {
			solitaire.touchEvent();
		} else {
			solitaire.moveEvent();
		}
		solitaire.viewEvent();
		
	}
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void dispose() {
		solitaire.dispose();
	}
}
