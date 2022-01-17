package au.com.redmars;

import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Tableau {

	public final Integer deckSize = 52;
    public final Integer suitSize = 13;
    public final Integer cardHeight = 780;
    public final Integer cardWidth = 560;

	public int chainLength(Card card);
	public boolean canMoveChain(Column dst, Integer chainLength);
	public void moveChain(Card src, Column dst);
	public void autoComplete();
	public void refreshBoard();
	public void refreshColumn(List<Card> col);
	public void setupBoard(OrthographicCamera camera);
	public void Deal(OrthographicCamera camera);
	public void drawBoard(Batch batch,ShapeRenderer shapeRenderer,OrthographicCamera camera);
}
