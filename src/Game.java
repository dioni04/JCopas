import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// machine's class
public class Game {
    public final int numCards = 52;
    public final int numPlayers = 4;
    public final int cardPerPlayer = numCards / numPlayers;

    public List<Card> cards = new ArrayList<>();
    private List<Network.Node> neighbors = new ArrayList<>();

    private Player player = new Player();
    private Network.Node node;

    public Game(int port, int nextNodePort, String nextNodeIp, boolean isDealer) {
        this.node = new Network.Node(port, nextNodePort, nextNodeIp, isDealer);
    }

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
        for (var node : neighbors) {
            for (int i = 0; i < cardPerPlayer; i++) {
                player.receiveCard(cards.removeLast());
            }
        }
    }

    public Network.Node getNode() {
        return node;
    }
}