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

	public synchronized void addPlayer(Player player) {
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
			messages.add(dealCardsToPlayer(player));
		}
		return messages;
	}

	private SendMessage dealCardsToPlayer(Player player) {
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
		return message;
	}

	public synchronized SendMessage[] switchPick(int pickNumber, int playerId) {
		List<Pick> picksFromPlayer = currentPicks.stream().filter(x -> x.getPlayer().getUserId() == playerId)
				.collect(Collectors.toList());
		currentPicks = currentPicks.stream().filter(x -> x.getPlayer().getUserId() != playerId)
				.collect(Collectors.toList());
		SendMessage msg = new SendMessage();
		Pick removedPick = picksFromPlayer.remove(pickNumber);
		msg.setText(removedPick.getPlayer().getName() + Text.DOESNT_KNOW_CARD + removedPick.getCard().getText());
		Card card = cards.getWhiteCard();
		Pick newPick = new Pick(removedPick.getPlayer(), card);
		picksFromPlayer.add(newPick);
		currentPicks.addAll(picksFromPlayer);
		SendMessage[] ret = new SendMessage[2];
		ret[0] = msg;
		ret[1] = getResendMessage(playerId, true);
		return ret;
	}

	private SendMessage getResendMessage(int playerId, boolean canSwitch) {
		List<Pick> picksFromPlayer = currentPicks.stream().filter(x -> x.getPlayer().getUserId() == playerId)
				.collect(Collectors.toList());
		List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
		String cardText = "";
		for (int i = 1; i <= picksFromPlayer.size(); i++) {
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText("" + i);
			if (canSwitch) {
				button.setCallbackData(Commands.SWITCH_CARD + "?" + i);
			} else {
				button.setCallbackData(Commands.CHOOSE_CARD + "?" + i);
			}
			List<InlineKeyboardButton> l = new ArrayList<InlineKeyboardButton>();
			l.add(button);
			buttons.add(l);
			cardText += "" + i + ": " + picksFromPlayer.get(i-1).getCard().getText() + "\n";
		}
		InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
		markup.setKeyboard(buttons);
		long playerChatId = picksFromPlayer.get(0).getPlayer().getPrivateChatId();
		SendMessage message = new SendMessage(playerChatId, Text.YOUR_CARDS + "\n" + cardText);
		message.setReplyMarkup(markup);
		message.setChatId(playerChatId);
		return message;
	}
}