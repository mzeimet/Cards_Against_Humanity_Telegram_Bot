package cah;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import constants.Commands;
import constants.Private;
import constants.Text;
import reader.CardHolder;

public class CAHBot extends TelegramLongPollingBot {

	private Game game;
	private long groupChatId;
	private int lastMessageId;
	private Stack<Integer> deleteList;

	public CAHBot(CardHolder cards) {
		this.game = new Game(cards);
		deleteList = new Stack<Integer>();
	}

	@Override
	public String getBotUsername() {
		return Private.BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return Private.BOT_TOKEN;
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			if (update.hasMessage()) {
				processMessage(update.getMessage());
			} else if (update.hasCallbackQuery()) {
				processCallback(update.getCallbackQuery());
			} else if (update.hasInlineQuery()) {
				if (update.getInlineQuery().getFrom().getId() == Private.MY_CHAT_ID) {
					proccessInlineQuery(update.getInlineQuery());
				}
			}
		} catch (Exception e) {
			// TODO: Exception handling
			e.printStackTrace();
		}
	}

	private void proccessInlineQuery(InlineQuery inlineQuery) throws TelegramApiException {
		AnswerInlineQuery reply = new AnswerInlineQuery().setInlineQueryId(inlineQuery.getId());
		if (!inlineQuery.getQuery().equals(Text.PLAY_A_GAME)) {
			reply.setSwitchPmText(Text.SWITCH_PM_TEXT).setSwitchPmParameter(Commands.SWITCH_PM)
					.setResults(new ArrayList<>());
		} else {
			reply.setResults(new ArrayList<>()); // TODO
		}
		execute(reply);
	}

	private void processCallback(CallbackQuery callbackQuery) {
		AnswerCallbackQuery q = new AnswerCallbackQuery();
		q.setCallbackQueryId(callbackQuery.getId());
	}

	private void processMessage(Message message) throws TelegramApiException {
		SendMessage reply = new SendMessage().setChatId(message.getChatId());
		if (!message.getChat().isGroupChat() && message.getText().contains(Commands.JOIN_GAME)) {
			if (isFromAdmin(message) && message.getText().contains(Commands.SWITCH_PM)) {
				reply.setText("Mögliche Optionen:");
				configureGame(reply);
			} else {
				try {
					String newPlayerName = addPlayerToGame(message);
					reply.setText(Text.YOU_WERE_ADDED);
					updateStartText(newPlayerName);
				} catch (Exception e) {
					e.printStackTrace();
					reply.setText(Text.ADD_ERROR);
				}
			}
		} else if (message.getChat().isGroupChat() && isFromAdmin(message)) {
			if (message.getText().contains(Commands.START_GAME)) {
				reply.setText(Text.WANTS_TO_START_GAME + "0" + Text.WANTS_TO_START_GAME_2);
				this.groupChatId = message.getChatId();
			} else if (message.getText().contains(Commands.DEAL)) {
				clearMessages();
				pickLeader();
				dealCards();
				reply.setText(Text.SWITCH_CARDS);
			}
		}
		int messageId = execute(reply).getMessageId();
		if (message.getChatId() == groupChatId) {
			lastMessageId = messageId;
			deleteList.add(messageId);
		}
	}

	private void dealCards() throws TelegramApiException {
		for (SendMessage msg : game.dealCardMessages()) {
			execute(msg);
		}
	}

	private void pickLeader() throws TelegramApiException {
		SendMessage message = new SendMessage().setChatId(groupChatId);
		String leaderName = game.getNextLeader().getName();
		message.setText(leaderName + Text.CHOSE_LEADER);
		execute(message);
	}

	private void clearMessages() throws TelegramApiException {
		while (!deleteList.isEmpty()) {
			execute(new DeleteMessage(groupChatId, deleteList.pop()));
		}
	}

	private boolean isFromAdmin(Message message) {
		return message.getFrom().getId() == Private.MY_CHAT_ID;
	}

	private void configureGame(SendMessage reply) {
		List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
		InlineKeyboardButton button = new InlineKeyboardButton();
		button.setText(Text.RETURN_TO_CHAT);
		button.setSwitchInlineQuery(Text.PLAY_A_GAME);
		List<InlineKeyboardButton> l = new ArrayList<InlineKeyboardButton>();
		l.add(button);
		buttons.add(l);
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		inlineKeyboardMarkup.setKeyboard(buttons);
		reply.setReplyMarkup(inlineKeyboardMarkup);
	}

	private void updateStartText(String newPlayerName) throws TelegramApiException {
		EditMessageText edit = new EditMessageText().setMessageId(lastMessageId).setChatId(groupChatId);
		// SendMessage reply = new SendMessage().setChatId(groupChatId);
		edit.setText(Text.WANTS_TO_START_GAME + game.getPlayerCount() + Text.WANTS_TO_START_GAME_2); // TODO:
																										// Überschreiben
																										// statt neue
																										// nachricht
		execute(edit);
		SendMessage reply = new SendMessage().setChatId(groupChatId);
		reply.setText(newPlayerName + Text.JOINED_THE_GAME);
		deleteList.add(execute(reply).getMessageId());
	}

	private String addPlayerToGame(Message m) {
		game.addPlayer(new Player(m.getFrom(), m.getChatId()));

		return m.getFrom().getFirstName() != null ? m.getFrom().getFirstName() : m.getFrom().getUserName();
	}
}
