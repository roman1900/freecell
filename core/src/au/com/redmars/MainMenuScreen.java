package au.com.redmars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

	final Solitaire game;
	OrthographicCamera camera;

	public MainMenuScreen(final Solitaire game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 400);
	}
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		GlyphLayout gLayout = new GlyphLayout(game.font,"Freecell");
		game.font.draw(game.batch, "Freecell", camera.viewportWidth / 2 - gLayout.width / 2, 300);
		gLayout = new GlyphLayout(game.font,"Tap anywhere to begin!");
		game.font.draw(game.batch, "Tap anywhere to begin!", camera.viewportWidth / 2 - gLayout.width / 2, 300-game.font.getLineHeight());
		game.batch.end();

		if (Gdx.input.isTouched()) {
			game.setScreen(new GameScreen(game));
			dispose();
		}
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
}
