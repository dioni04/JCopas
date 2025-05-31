import java.util.*;

public class Player {
    private int points = 0;
    private boolean dealer;
    public boolean receivingCards = true;
    private List<Card> scores = new ArrayList<>();
    private List<Card> hand = new ArrayList<>();
    private Card lastPlayed;
    private Game game;
    Scanner scanner = new Scanner(System.in);

    public Player(Game game, boolean dealer) {
        this.game = game;
        this.dealer = dealer;

    }

    public int getPoints() {
        return points;
    }

    public void playCard(int index) {

    }

    public boolean isFirst() {
        for (Card card : hand) {
            if (card.getRank() == Card.Rank.TWO && card.getSuit() == Card.Suit.SPADES)
                return true;
        }
        return false;
    }

    public void gainPoints() {
        for (var card : scores) {
            points += card.getValue();
        }
        receivingCards = true;
        // End Game
        if (points >= 100) {
            game.handler.broadcastSimpleMessage(Message.MessageType.END);
            game.endGame();
        }
    }

    public void receiveCard(Card card) {
        this.hand.add(card);
    }

    public void roundStart() {
        receivingCards = false;
        // Se for dealer
        if (dealer)
            game.distributeCards();
    }

    public Card findHighestCard() {
        var high = game.getCardsPlayed().get(0);
        for (Card card : game.getCardsPlayed()) {
            if (card.getSuit() == game.getCurrentSuit() && card.getRank().getValue() > high.getRank().getValue())
                high = card;
        }
        return high;
    }

    public void trickEnd() {
        if (this.lastPlayed == findHighestCard()) {
            for (Card card : game.getCardsPlayed()) {
                scores.add(card);
            }
            dealer = true;
        } else
            dealer = false;
    }

    public boolean hasSuit(Card.Suit suit) {
        for (Card card : hand) {
            if (card.getSuit() == suit)
                return true;
        }
        return false;
    }

    public void roundEnd() {
        receivingCards = true;
        trickEnd();
    }

    public void printHand() {
        int i = 0;
        System.out.println("Your hand:");
        if (dealer) {
            for (Card card : hand) {
                System.out.println(i + ": " + card.toString());
                i++;
            }
            return;
        }
        String notValid = hasSuit(game.getCurrentSuit()) ? " [Invalid]" : " [Discard]";
        for (Card card : hand) {
            if (game.getCurrentSuit() == card.getSuit())
                System.out.println(i + ": " + card.toString() + notValid);
            System.out.println(i + ": " + card.toString());
            i++;
        }
    }

    public void playTurn() {

        if (hand.isEmpty()) {
            System.out.println("No cards left to play.");
            game.handler.broadcastSimpleMessage(Message.MessageType.ROUNDEND);
            return;
        }
        if (game.getCardsPlayed().size() == game.numPlayers) {
            game.handler.broadcastSimpleMessage(Message.MessageType.TRICKEND);
            trickEnd();
        }

        if (dealer)
            System.out.println("You're first!");

        System.out.println("Playing my turn!");

        game.printTable();
        printHand();

        int choice = -1;
        boolean valid = false;
        var suit = game.getCurrentSuit();

        while (!valid) {
            System.out.print("Choose a card to play (enter the index): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 0 || choice >= hand.size()
                        || (!dealer && hasSuit(suit) && hand.get(choice).getSuit() != suit))
                    System.out.println("Invalid choice. Try again.");
                else
                    valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        Card playedCard = hand.remove(choice);

        if (dealer)
            game.setCurrentSuit(playedCard.getSuit());

        System.out.println("You played: " + playedCard.getRank().getKey() + playedCard.getSuit().getKey());

        game.handler.sendMessage(game.getNextNode(), Message.cardMessage(playedCard));

        // joga carta, pontua e tal

        // no fim de sua vez, passa o bastao em Node
        game.handler.sendMessage(game.getNextNode(), Message.MessageType.BATON.getKey());

    }

    public void endGame() {
        System.out.println("Game ended! You ended with " + Integer.toString(points) + " points.");
        scanner.close();
    }
}