package au.com.redmars;

import java.util.Arrays;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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

	@Override
	public void create () {
		batch = new SpriteBatch();
		float width = (560 * 12) + (20 * 10);
		camera = new OrthographicCamera(width,width * .5F); //temp camera world units
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
		d = new Deck();	
		d.Deal(camera);
		shapeRenderer = new ShapeRenderer();
	}
	
	@Override 
	public void resize (int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void render () {
		
		ScreenUtils.clear(0.3F, 1, 0.3F, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		for (int fc = 0; fc < 8; ++fc) {
			shapeRenderer.rect(30+ (560 * fc) + (20 * fc),camera.viewportHeight - 30 - 780,560,780);
		}
		shapeRenderer.end();
		batch.begin();
		
		batch.disableBlending();
		d.board.forEach(column -> column.forEach(card -> card.image.draw(batch)));
		if(Gdx.input.isTouched()) {
			batch.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.RED);
			Vector3 mouse = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
			camera.unproject(mouse);
			Arrays.asList(d.deck).stream().filter(c -> c.canGrab).forEach(c -> {if (c.hitbox.contains(mouse.x,mouse.y)) shapeRenderer.setColor(Color.GREEN);});
			shapeRenderer.rect(mouse.x,mouse.y,50,50);
			shapeRenderer.end();
			batch.begin();
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		d.cardTileSet.dispose();	
		shapeRenderer.dispose();
	}
}
