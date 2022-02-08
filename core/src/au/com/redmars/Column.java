package au.com.redmars;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

//This Class represents a column on the Solitaire Board
//The Column may be on:
//	the playing area 0 - 7
//	the Free Cells 8 - 11
//  the Home Cells 12 - 15

public class Column {
	public Integer index;
	public Integer maxCards;
	public List<Card> cards;
	public Rectangle[] cardHitBoxes;
	public Rectangle hitbox;
	
	public void populateHitBoxes(float cardGap) {
		float topOfStack = hitbox.y + hitbox.height;
		cardHitBoxes = new Rectangle[cards.size()];
		int index = 1;
		while (index < cards.size()) {
			cardHitBoxes[index - 1] = new Rectangle(hitbox.x,topOfStack - cardGap * index, hitbox.width, cardGap);
			index++;
		}
		cardHitBoxes[index - 1] = new Rectangle(hitbox.x,topOfStack - (cardGap * (index - 1)) - 780, hitbox.width, 780);
	}

	public Optional<Card> touchingCard(Vector3 touch) {
		if(cards.isEmpty()) return Optional.empty();
		for(int i = 0; i < cards.size(); ++i) {
			if (cardHitBoxes[i].contains(touch.x,touch.y)) return Optional.ofNullable(cards.get(i));
		}
		return Optional.empty();
	}

	Column(Integer index, Integer maxCards, Rectangle hitbox) {
		this.index = index;
		this.maxCards = maxCards;
		this.hitbox = hitbox;
		cards = new ArrayList<>();
	}
}
