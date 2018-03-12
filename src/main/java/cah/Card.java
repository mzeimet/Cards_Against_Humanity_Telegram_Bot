package cah;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Card {

	private String text;
	private int gapCount;
	private boolean isWhite;

	public Card(String line, boolean isWhite) {
		this.isWhite = isWhite;
		text = line;
		if (!isWhite && line.contains("_")) {
			gapCount = line.split("_").length - 1;
		} else if (!isWhite) {
			gapCount = line.split("_").length - 1;
		}
	}

	public int getRelativeGapCount() {
		return gapCount == 0 ? 1 : gapCount;
	}
}
