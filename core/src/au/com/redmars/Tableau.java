package au.com.redmars;

import java.util.List;

import com.badlogic.gdx.audio.Sound;

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

	public void setPickupSound(Sound sound);

	public void setPutDownSound(Sound sound);

	public Sound getPickupSound();

	public Sound getPutDownSound();

	public void refreshColumn(List<Card> col);

	public void setupBoard();

	public void Deal();

	public void drawBoard();

	public void touchEvent();

	public void moveEvent();

	public void viewEvent();

	public void dispose();
}
