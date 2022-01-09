package au.com.redmars;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Freecell extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	Deck d;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(560 * 8 + (20 * 10), (560 * 8 + (20 * 10)) / 2); //temp camera world units
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		d = new Deck();	
		d.Deal();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.3F, 1, 0.3F, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// for (int i=0; i < 52; ++i) {
		batch.disableBlending();	
		// }
		
		d.deck[0].image.setPosition(0, 0);
		d.deck[0].image.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		d.cardTileSet.dispose();	
	}
}
