import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Classe q engloba uma maquina
public class Game {
    public final int numCards = 40;
    public final int numPlayers = 4;
    public final int cardPerPlayer = numCards / numPlayers;

    public List<Card> cards = new ArrayList<>();
    
    private Player player;
    private Network.Node node;

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

}