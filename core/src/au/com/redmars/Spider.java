package au.com.redmars;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Spider implements Tableau {

	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private Vector3 currentMouse;
	private Color cursorColor;
	private Batch batch;

	@Override
	public int chainLength(Card card) {
		return 0;
	}

	@Override
	public boolean canMoveChain(Column dst, Integer chainLength) {
		return false;
	}

	@Override
	public void moveChain(Card src, Column dst) {

	}

	@Override
	public void autoComplete() {

	}

	@Override
	public void refreshBoard() {

	}

	@Override
	public void refreshColumn(List<Card> col) {

	}

	@Override
	public void setupBoard() {

	}

	@Override
	public void drawBoard() {

	}

	public Spider(Batch batch, OrthographicCamera camera, ShapeRenderer shapeRenderer) {
		this.camera = camera;
		this.batch = batch;
		this.shapeRenderer = shapeRenderer;
	}

	@Override
	public void Deal() {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void viewEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
