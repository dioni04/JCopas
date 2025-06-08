import java.util.*;

public class Player {
    private int points = 0;
    private boolean dealer;
    public boolean receivingCards = true;
    private List<Card> scores = new ArrayList<>();
    private List<Card> hand = new ArrayList<>();
    private Card lastPlayed = null;
    private Game game;
    Scanner scanner = new Scanner(System.in);

    public Player(Game game, boolean dealer) {
        this.game = game;
        this.dealer = dealer;

    }

    public void moonHit() {
        points += 50;
        if (points >= 100) {
            game.handler.broadcastMessage(Message.MessageType.END);
            game.endGame();
        }
    }

    public boolean isMoonHit() {
        int cards = 0;
        for (Card card : scores) {
            if (card.getSuit() == Card.Suit.HEARTS)
                cards++;
        }
        if (cards == 13)
            return true;
        return false;
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
        int p = 0;

        if (isMoonHit()) {
            System.out.println("You hit the moon! Other players receive 50 points");
            game.handler.broadcastMessage(Message.MessageType.MOON);
            scores.clear();
            return;
        }
        for (var card : scores) {
            p += card.getValue();
        }
        scores.clear();
        points += p;
        System.out.printf("You won %d points.\nTotal: %d points\n", p, points);
        // End Game
        if (points >= 100) {
            game.handler.broadcastMessage(Message.MessageType.END);
            game.endGame();
        }
    }

    public void receiveCard(Card card) {
        System.out.println("Received card: " + card.toString());
        this.hand.add(card);
    }

    public void roundStart() {
        receivingCards = false;
        // Se for dealer
        if (dealer) {
            playTurn();
        }
    }

    public Card findHighestCard() {
        var high = game.getCardsPlayed().get(0);
        for (Card card : game.getCardsPlayed()) {
            if (card.getSuit() == game.getCurrentSuit() && card.getRank().getValue() > high.getRank().getValue())
                high = card;
        }
        return high;
    }

    public void trickEnd(boolean rend) {
        if (this.lastPlayed == findHighestCard()) {
            System.out.println("Your card " + lastPlayed.toString() + " was highest, adding cards to scores...");
            for (Card card : game.getCardsPlayed()) {
                scores.add(card);
            }
            dealer = true;
        } else
            dealer = false;

        lastPlayed = null;
        game.setCurrentSuit(null);
        game.cardsPlayed.clear();

        if (!rend && dealer) {
            playTurn();
        }
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
        trickEnd(true);
        System.out.println("Round end!");
        gainPoints();
        hand.clear();
        if (Program.DEBUG) {
            System.out.println("DEBUG mode, press ENTER to continue");
            scanner.nextLine();
        }
        if (Program.AUTO)
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        if (dealer) {
            game.distributeCards();
            game.handler.broadcastMessage(Message.MessageType.ROUNDBEGIN);
            roundStart();
        }

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
            if (game.getCurrentSuit() != card.getSuit())
                System.out.println(i + ": " + card.toString() + notValid);
            else
                System.out.println(i + ": " + card.toString());
            i++;
        }
    }

    // Acha primeira carta valida para jogar, usado com flag AUTO
    private int getFirstValid() {
        int i = 0;
        var suit = game.getCurrentSuit();

        if (suit != null) {
            if (!hasSuit(suit))
                return 0;
            for (Card card : hand) {
                if (card.getSuit() == suit)
                    return i;
                i++;
            }
        }

        return 0;
    }

    public void playTurn() {

        if (hand.isEmpty()) {
            System.out.println("No cards left to play.");
            game.handler.broadcastMessage(Message.MessageType.ROUNDEND);
            roundEnd();
            return;
        }
        if (game.cardsPlayed.size() == game.numPlayers) {
            System.out.println("All players played, ending trick.");
            game.handler.broadcastMessage(Message.MessageType.TRICKEND);
            trickEnd(false);
            return;
        }

        if (dealer)
            System.out.println("You're first!");

        System.out.println("Playing my turn!");

        game.printTable();
        printHand();

        int choice = -1;
        boolean valid = false;
        var suit = game.getCurrentSuit();

        System.out.print("Choose a card to play (enter the index): ");

        if (Program.AUTO) {
            valid = true;
            choice = getFirstValid();
        }

        while (!valid) {
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

        if (game.getCardsPlayed().isEmpty())
            game.setCurrentSuit(playedCard.getSuit());

        System.out.println("You played: " + playedCard.toString());
        this.lastPlayed = playedCard;
        game.handler.broadcastMessage(playedCard);
        game.getCardsPlayed().add(playedCard);
        // joga carta, pontua e tal

        // no fim de sua vez, passa o bastao em Node
        game.handler.createAndSendMessage(game.getNextNode(), Message.MessageType.BATON.getKey());

    }

    public void endGame() {
        scanner.close();
    }
}