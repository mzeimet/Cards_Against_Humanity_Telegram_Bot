package reader;

import cah.Card;

public class BlackCardReader extends CardReader {

	@Override
	protected Card getCardFromLine(String line) {
		line = line.replace("\n", "").replace("\r", "");
		return new Card(line, false);
	}

}
