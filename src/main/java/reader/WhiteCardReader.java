package reader;

import cah.Card;

public class WhiteCardReader extends CardReader {

	@Override
	protected Card getCardFromLine(String line) {
		line = line.replace("\r", "").replaceAll("\n", "").replace(".", "");
		return new Card(line, true);
	}

}
