package au.com.redmars;

import java.util.List;

import com.badlogic.gdx.audio.Sound;
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
	private Sound pickupSound;
	private Sound putDownSound;

	@Override
	public int chainLength(Card card) {
		return 0;
	}

	@Override
	public boolean canMoveChain(Column dst, Integer chainLength) {
		return false;
	}

	@Override
	public void moveChain(Card src, Column dst,List<Undo.Location> turn) {

	}

	@Override
	public void autoComplete(List<Undo.Location> turn) {

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
	public void setPickupSound(Sound sound) {
		pickupSound = sound;
	}

	@Override
	public void setPutDownSound(Sound sound) {
		putDownSound = sound;
	}

	@Override
	public Sound getPickupSound() {
		return pickupSound;
	}

	@Override
	public Sound getPutDownSound() {
		return putDownSound;
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
	public Boolean moveEvent() {
		// TODO Auto-generated method stub
		return false;

	}

	@Override
	public void viewEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public float getBoardMargin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBoardMargin(float margin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void undoMove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNoGoSound(Sound sound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Sound getNoGoSound() {
		// TODO Auto-generated method stub
		return null;
	}
}
