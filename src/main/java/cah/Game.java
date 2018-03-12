package cah;

import java.util.ArrayList;
import java.util.List;

import constants.Text;
import reader.CardHolder;

public class Game {

	private CardHolder cards;

	private Card currentBlackCard;

	private List<Pick> currentPicks;

	private List<Player> players;

	private final int CARD_COUNT = 10;

	public Game(CardHolder cards) {
		this.cards = cards;
		players = new ArrayList<Player>();
	}

	public void addPlayer(Player player) {
		if (players.contains(player)) {
			throw new IllegalArgumentException();
		} else {
			players.add(player);
			System.out.println(Text.ADDED_PLAYER + player.getName());
		}
	}

	public void spreadCards() {
		for (Player player : players) {
			for (int i = 0; i < CARD_COUNT; i++) {
				player.addCard(cards.getWhiteCard());
			}
		}
	}
}