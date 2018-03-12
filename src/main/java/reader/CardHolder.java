package reader;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import cah.Card;
import lombok.Getter;

@Getter
public class CardHolder {
	private Stack<Card> whiteCards = new Stack<Card>();
	private Stack<Card> blackCards = new Stack<Card>();
	private CardReader whiteReader = new WhiteCardReader();;
	private CardReader blackReader = new BlackCardReader();

	public void read(String filename, boolean isWhite) throws FileNotFoundException {
		List<Card> cards;
		if (isWhite) {
			cards = whiteReader.readCards(filename);
			whiteCards.addAll(cards);
			Collections.shuffle(whiteCards);
		} else {
			cards = blackReader.readCards(filename);
			blackCards.addAll(cards);
			Collections.shuffle(blackCards);
		}
	}

	public CardHolder(String whiteFileName, String blackFileName) throws FileNotFoundException {
		read(whiteFileName, true);
		read(blackFileName, false);
	}

	public Card getBlackCard() {
		return blackCards.pop();
	}

	public Card getWhiteCard() {
		return whiteCards.pop();
	}
}
