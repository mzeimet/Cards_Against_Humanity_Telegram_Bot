package cah;

import java.io.FileNotFoundException;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import reader.CardHolder;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, TelegramApiRequestException {
		CardHolder cards = new CardHolder("white.txt", "black.txt");

		ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();

		botsApi.registerBot(new CAHBot(cards));

//		for (Card card : cards.getBlackCards()) {
//			Card[] fillcards = new Card[card.getRelativeGapCount()];
//			for (int i = 0; i < card.getRelativeGapCount(); i++) {
//				fillcards[i] = cards.getWhiteCard();
//			}
//			String answers = " ";
//			for (Card c : fillcards) {
//				answers += c.getText() + ", ";
//			}
//			System.out.println("" + card.getText() + answers);
//		}
	}

}
