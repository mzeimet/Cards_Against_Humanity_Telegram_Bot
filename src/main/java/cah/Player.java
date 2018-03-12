package cah;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.objects.User;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Player {

	private long privateChatId;
	private int userId;
	private String name;

	private List<Card> cards;

	public Player(User from, Long chatId) {
		privateChatId = chatId;
		userId = from.getId();
		name = from.getFirstName();
		cards = new ArrayList<Card>();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Player)) {
			return false;
		}
		return ((Player) o).userId == userId;
	}
	
	public void addCard(Card card) {
		cards.add(card);
	}

}
