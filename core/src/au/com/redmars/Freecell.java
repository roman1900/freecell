package au.com.redmars;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;

public class Freecell extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	Deck d;
	ShapeRenderer shapeRenderer;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(560 * 8 + (20 * 10), (560 * 8 + (20 * 10)) *.75F); //temp camera world units
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		d = new Deck();	
		d.Deal(camera);
		shapeRenderer = new ShapeRenderer();
	}
	
	@Override
	public void render () {
		
		ScreenUtils.clear(0.3F, 1, 0.3F, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.disableBlending();	
		for (Card c: d.deck) {
		 	c.image.draw(batch);
		}
		if(Gdx.input.isTouched()) {
			batch.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(0,0,50,50);
			shapeRenderer.end();
			batch.begin();
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		d.cardTileSet.dispose();	
	}
}
