package cah;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import constants.Commands;
import constants.Private;
import constants.Text;
import reader.CardHolder;

public class CAHBot extends TelegramLongPollingBot {

	private Game game;

	public CAHBot(CardHolder cards) {
		this.game = new Game(cards);
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
			}
		} catch (Exception e) {
			//TODO: Exception handling
		}
	}

	private void processCallback(CallbackQuery callbackQuery) {
		AnswerCallbackQuery q = new AnswerCallbackQuery();
		q.setCallbackQueryId(callbackQuery.getId());
	}

	private void processMessage(Message message) throws TelegramApiException {
		SendMessage reply = new SendMessage().setChatId(message.getChatId());
		if (!message.getChat().isGroupChat() && message.getText().contains(Commands.JOIN_GAME)) {
			try {
				addPlayerToGame(message);
				reply.setText(Text.YOU_WERE_ADDED);
				updateStartText();
			} catch (Exception e) {
				reply.setText(Text.ADD_ERROR);
			}
		}
		execute(reply);
	}

	private void updateStartText() {
		//TODO: Spieler wurde hinzugefügt, erhöhe Anzahl in Text
		
	}

	private void addPlayerToGame(Message m) {
		game.addPlayer(new Player(m.getFrom(),m.getChatId()));
	}
}
