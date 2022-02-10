package au.com.redmars;

import java.util.ArrayList;
import java.util.List;

public class Undo {
	
	public class Location {
		Card card;
		Integer previousColumn;
		Location(Card card,Integer previousColumn) {
			this.card = card;
			this.previousColumn = previousColumn;
		}
	}

	List<List<Location>> turns;

	public Undo() {
		turns = new ArrayList<>();
	}
	
}
