package cah;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import constants.Commands;
import constants.Text;
import reader.CardHolder;

public class Game {

	private CardHolder cards;

	private Card currentBlackCard;

	private List<Pick> currentPicks;

	private List<Player> players;

	private Stack<Player> possibleLeaders;

	private Player currentLeader;

	private final int CARD_COUNT = 10;

	public Game(CardHolder cards) {
		this.cards = cards;
		players = new ArrayList<Player>();
		currentPicks = new ArrayList<Pick>();
	}

	public void addPlayer(Player player) {
		if (players.contains(player)) {
			throw new IllegalArgumentException();
		} else {
			players.add(player);
			System.out.println(Text.ADDED_PLAYER + player.getName() + " id:" + player.getPrivateChatId());
		}
	}

	public void spreadCards() {
		for (Player player : players) {
			for (int i = 0; i < CARD_COUNT; i++) {
				player.addCard(cards.getWhiteCard());
			}
		}
	}

	public int getPlayerCount() {
		return players.size();
	}

	public Player getNextLeader() {
		if (possibleLeaders == null || possibleLeaders.size() == 0) {
			possibleLeaders = new Stack<Player>();
			possibleLeaders.addAll(players);
		}
		Collections.shuffle(possibleLeaders);
		currentLeader = possibleLeaders.pop();
		return currentLeader;
	}

	public List<Player> getPlayers() {
		return players.stream().filter(x -> !x.equals(currentLeader)).collect(Collectors.toList());
	}

	public List<SendMessage> dealCardMessages() {
		List<SendMessage> messages = new ArrayList<SendMessage>();
		for (Player player : players.stream().filter(x -> !x.equals(currentLeader)).collect(Collectors.toList())) {
			List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
			String cardText = "";
			for (int i = 1; i <= CARD_COUNT; i++) {
				Card card = cards.getWhiteCard();
				currentPicks.add(new Pick(player, card));
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText("" + i);
				button.setCallbackData(Commands.SWITCH_CARD + "?" + i);
				List<InlineKeyboardButton> l = new ArrayList<InlineKeyboardButton>();
				l.add(button);
				buttons.add(l);
				cardText += "" + i + ": " + card.getText() + "\n";
			}
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
			markup.setKeyboard(buttons);
			SendMessage message = new SendMessage(player.getPrivateChatId(), Text.YOUR_CARDS + "\n" + cardText);
			message.setReplyMarkup(markup);
			messages.add(message);
		}
		return messages;
	}
}