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

    private Player player;
    private Node node;
    private Card.Suit currentSuit;

    public MessageHandler handler;

    public Game(int port, int nextNodePort, String nextNodeIp, boolean isDealer) {
        this.node = new Node(port, nextNodePort, nextNodeIp, isDealer, this);
        new Thread(() -> node.listen()).start();

        this.handler = new MessageHandler(this);
        this.player = new Player(this, isDealer);

        if (isDealer) {
            node.assignIDs();
            distributeCards();
            handler.broadcastMessage(Message.MessageType.ROUNDBEGIN);
            player.roundStart();
        }
    }

    public Node getNode() {
        return node;
    }

    public int getId() {
        return node.getId();
    }

    public int getNextNode() {
        return (node.getId() + 1) % numPlayers;
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

        int cardIndex = 0;

        while (cardIndex < numCards) {
            int targetId = cardIndex % numPlayers;
            Card card = cards.removeLast();

            if (targetId == getId()) {
                player.receiveCard(card); // Give it to self
            } else {
                handler.createAndSendMessage(targetId, Message.cardMessage(card));
            }

            cardIndex++;
        }

        cards.clear();
    }

    public void printTable() {
        System.out.println("Cards Played:");

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
        player.endGame();
        node.setGameEnded(true);
    }
}