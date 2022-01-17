package au.com.redmars;

import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Spider implements Tableau {

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

	public void refreshColumn(List<Card> col) {

	}

	public void setupBoard(OrthographicCamera camera) {

	}

	public void Deal(OrthographicCamera camera) {

	}

	public void drawBoard(Batch batch, ShapeRenderer shapeRenderer, OrthographicCamera camera) {

	}

	public Spider() {
		
	}
}
