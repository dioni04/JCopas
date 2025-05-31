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
        this.handler = new MessageHandler(this);
        this.player = new Player(this, isDealer);
        if(isDealer)
            assignIDs();
    }

    public Node getNode() {
        return node;
    }

    public int getId(){
        return node.getId();
    }

    public int getNextNode(){
        return (node.getId() + 1) % numPlayers;
    }

    public List<Node> getNeighbors() {
        return neighbors;
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
    
    public Card.Suit getCurrentSuit() {
        return currentSuit;
    }

    private void assignIDs(){
        for(int i = 1; i < numPlayers; i++){
            handler.sendMessage(i, Message.idMessage(true));
        }
    }

    public void addNeighbor(Node neighbor) {
        this.neighbors.add(neighbor);
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
                handler.sendMessage(node.getId(), Message.cardMessage(card));
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