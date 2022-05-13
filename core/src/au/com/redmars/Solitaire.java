package au.com.redmars;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Solitaire extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public BitmapFont largeFont;
	public ShapeRenderer shapeRenderer;
	public Music bgMusic;

	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("font.fnt"));
		largeFont = new BitmapFont(Gdx.files.internal("Ageny-128pt.fnt"));
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("CHIPTUNE_Loop_The_Bards_Tale.ogg"));
		bgMusic.setLooping(true);
		bgMusic.play();
		
		batch.enableBlending();
		shapeRenderer = new ShapeRenderer();
		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
