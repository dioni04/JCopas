import java.util.*;

public class Player {
    private int points = 0;
    private boolean dealer;
    public boolean receivingCards = true;
    private List<Card> hand = new ArrayList<>();
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
        var cards = game.getCardsPlayed();
        for (var card : cards) {
            points += card.getValue();
        }
        this.dealer = true;
        receivingCards = true;
        // End Game
        if (points >= 100)
            game.endGame();
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

    public void roundEnd() {
        receivingCards = true;
        var highest = game.cardsPlayed.getFirst();
        for (Card card : game.cardsPlayed) {
        }
    }

    public void playTurn() {

        System.out.println("Playing my turn!");

        if (hand.isEmpty()) {
            System.out.println("No cards left to play.");
            return;
        }
        if(dealer)
            System.out.println("You're first!");

        game.printTable();
        System.out.println("Your hand:");
        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            System.out.println(i + ": " + c.toString());
        }

        int choice = -1;

        while (choice < 0 || choice >= hand.size()) {
            System.out.print("Choose a card to play (enter the index): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 0 || choice >= hand.size()) {
                    System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        Card playedCard = hand.remove(choice);
        
        if(dealer)
            game.setCurrentSuit(playedCard.getSuit());

        System.out.println("You played: " + playedCard.getRank().getKey() + playedCard.getSuit().getKey());

        // joga carta, pontua e tal

        // no fim de sua vez, passa o bastao em Node

    }

    public void endGame() {
        System.out.println("Game ended! You ended with " + Integer.toString(points) + " points.");
        scanner.close();
    }
}