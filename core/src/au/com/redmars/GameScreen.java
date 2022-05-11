package au.com.redmars;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen{
	final Solitaire game;
	OrthographicCamera camera;
	Tableau solitaire;
	float width;
	float height;
	Integer widthRequired;
	private float timer = 0f;
    private float period = 1f;
    private LocalTime elapsed = LocalTime.now();
	private Boolean gameOver = false;

	public GameScreen(final Solitaire game) {
		this.game = game;
		width = Gdx.graphics.getWidth(); 
		height = Gdx.graphics.getHeight(); 
		widthRequired = Tableau.cardWidth * 11 + Tableau.cardMargin * 11;
		if (widthRequired > width) {
			float ratio = widthRequired / width;
			camera = new OrthographicCamera(widthRequired, height * ratio);	
		}
		else {
			camera = new OrthographicCamera(width, height);
		}
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
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
		timer += Gdx.graphics.getDeltaTime();
        if (timer >= period) {
			//elapsed = elapsed.plus((long)(timer*1000), ChronoUnit.MILLIS);
            timer = 0;
        }

		game.batch.setProjectionMatrix(camera.combined);
		game.shapeRenderer.setProjectionMatrix(camera.combined);
		game.batch.begin();
		//TODO:Fix timing when game spans dates. ie. start at 11:58pm and finish the next day
		long seconds = ChronoUnit.SECONDS.between(elapsed,LocalTime.now());

		game.largeFont.draw(game.batch,LocalTime.ofSecondOfDay(seconds).toString(),100,200);
		game.batch.end();
		solitaire.drawBoard();
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			elapsed = LocalTime.now();
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
			gameOver = solitaire.moveEvent();
		}
		solitaire.viewEvent();
		if (gameOver) {
			elapsed = LocalTime.now();
			solitaire.Deal();
		}
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
