package au.com.redmars;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Freecell extends ApplicationAdapter {
	SpriteBatch batch;
	
	Deck d;

	@Override
	public void create () {
		batch = new SpriteBatch();
		d = new Deck();	
		d.Deal();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.3F, 1, 0.3F, 1);
		batch.begin();
		// for (int i=0; i < 52; ++i) {
		batch.disableBlending();	
		// }
		d.deck[0].image.setPosition(-50, -50);
		d.deck[0].image.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		d.cardTileSet.dispose();	
	}
}
