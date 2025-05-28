import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// machine's class
public class Game {
    public final int numCards = 52;
    public final int numPlayers = 4;
    public final int cardPerPlayer = numCards / numPlayers;

    public List<Card> cards = new ArrayList<>();
    public List<Card> cardsPlayed = new ArrayList<>();
    private List<Node> neighbors = new ArrayList<>();

    private Player player;
    private Node node;
    private Card.Suit currentSuit;

    public MessageHandler handler;

    public Game(int port, int nextNodePort, String nextNodeIp, boolean isDealer) {
        this.node = new Node(port, nextNodePort, nextNodeIp, isDealer, this);
        this.node.setId(0);
        this.handler = new MessageHandler(this);
        this.player = new Player(this, isDealer);
        if(isDealer)
            assignIDs();
    }

    public Node getNode() {
        return node;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Card> getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCurrentSuit(Card.Suit currentSuit) {
        this.currentSuit = currentSuit;
    }

    private void assignIDs(){
        for(int i = 1; i < numPlayers; i++){
            var msg = new Message(0, i, Message.idMessage("1"));
            node.sendMessage(msg.messageBuild());
        }
    }

    public static List<Card> createShuffledDeck() {
        List<Card> deck = new ArrayList<>();

        // Populate the deck
        for (var suit : Card.Suit.values()) {
            for (var rank : Card.Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    public void distributeCards() {
        cards.clear();
        cards = createShuffledDeck();

        // Manda cartas para cada nodo
        for (var node : neighbors) {
            for (int i = 0; i < cardPerPlayer; i++) {
                var card = cards.removeLast();
                var msg = new Message(node.getId(), i, Message.cardMessage(card));
                node.sendMessage(msg.messageBuild());
            }
        }
        // Resto das cartas para dealer
        for (Card card : cards) {
            this.player.receiveCard(card);
        }
    }

    public void printTable() {
        System.out.println("Cards Played :");

        for (Card card : cardsPlayed) {
            System.out.println(card.toString());
        }
    }

    public void endGame() {
        int points = player.getPoints();
        if (points >= 100)
            System.out.println("You lost!");
        else
            System.out.println("You won!");
        System.out.println("Ended with " + points + " points.");
        node.setGameEnded(true);
    }
}