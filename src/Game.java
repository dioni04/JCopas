import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    public final int numPlayers = 4;
    public List<Player> players = new ArrayList<>();
    public List<Card> cards = new ArrayList<>();

    public static List<Card> createShuffledDeck() {
        List<Card> deck = new ArrayList<>();

        // Populate the deck
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }

        // Shuffle the deck
        Collections.shuffle(deck);
        return deck;
    }

    public void distributeCards() {
        for (Player player : players) {
            player.receiveCard(cards.removeFirst());
        }
    }
}