package au.com.redmars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

	final Solitaire game;
	// OrthographicCamera camera;
	Stage stage; 
	Skin skin;
	public MainMenuScreen(final Solitaire game) {
		this.game = game;
		// camera = new OrthographicCamera();
		// camera.setToOrtho(false, 1280, 720);
		this.stage = new Stage(new FillViewport(640,480),game.batch);
		
		Table menuTable = new Table();
		menuTable.setFillParent(true);
		menuTable.setWidth(800);
		menuTable.setDebug(true);
		skin = new Skin(Gdx.files.internal("skin/skin.json"));
		Slider volume = new Slider(0, 1, .01f, false,skin);
		Label vLabel = new Label("Music", skin);
		volume.setValue(game.bgMusic.getVolume());
		volume.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (volume.isDragging()) {
					game.bgMusic.setVolume(volume.getValue());
				}
			}
		});
		Label sLabel = new Label("Effects", skin);
		Slider effectsVolume = new Slider(0, 1, .01f, false,skin);
		effectsVolume.setValue(game.fxVolume);
		effectsVolume.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (effectsVolume.isDragging()) {
					game.fxVolume = effectsVolume.getValue();
				} else {
					//TODO(#6): Play sound effect to test volume
				}
			}
		});
		TextButton Start = new TextButton("Start",skin);
		Start.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new GameScreen(game));
			 	dispose();
			}
		});
		menuTable.row().pad(10,0,10,0);
		menuTable.add(vLabel).colspan(1).fillX().uniformX();
		menuTable.add(volume).colspan(2).fillX().uniformX();
		menuTable.row().pad(10,0,10,0);
		menuTable.add(sLabel).colspan(1).fillX().uniformX().padRight(10);
		menuTable.add(effectsVolume).colspan(2).fillX().uniformX();
		menuTable.row();
		menuTable.add(Start).colspan(3).fill();
		stage.addActor(menuTable);
		Gdx.input.setInputProcessor(stage);
	}
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		stage.act();
		stage.draw();
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height,true);
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
