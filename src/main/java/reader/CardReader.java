package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cah.Card;

public abstract class CardReader {

	private Scanner scanner;

	public List<Card> readCards(String filename) throws FileNotFoundException {
		File f = new File(filename);
		scanner = new Scanner(f);
		scanner.useDelimiter("\n");
		List<Card> cards = splitCards(readLines());
		scanner.close();
		return cards;
	}

	private List<Card> splitCards(ArrayList<String> lines) {
		ArrayList<Card> cards = new ArrayList<Card>();
		for (String line : lines) {
			cards.add(getCardFromLine(line));
		}
		return cards;
	}

	protected abstract Card getCardFromLine(String line);

	private ArrayList<String> readLines() {
		ArrayList<String> ret = new ArrayList<String>();
		while (scanner.hasNext()) {
			ret.add(scanner.next());
		}
		return ret;
	}

}
