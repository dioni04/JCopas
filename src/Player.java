import java.util.*;

public class Player {
    private int points = 0;
    private boolean dealer = false;
    private boolean receivingCards = true;
    private List<Card> cards = new ArrayList<>();

    public void playCard(int index) {

    }

    public void gainPoints(ArrayList<Card> cards) {
        for (var card : cards) {
            points += card.getValue();
        }
        this.dealer = true;
        receivingCards = true;
        // End Game
        // if(points >= 100)
    }

    public void receiveCard(Card card) {
        this.cards.add(card);
    }
}