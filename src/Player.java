import java.util.*;

public class Player {
    private boolean hasBaton = false;
    private int points = 0;
    private List<Card> cards = new ArrayList<>();

    public void receiveBaton() {
        hasBaton = true;
    }

    public void passBaton() {
        hasBaton = false;
        // Pass baton to next player
    }

    public void gainPoints(ArrayList<Card> cards) {
        for (var card : cards) {
            points += card.getValue();
        }
        // End Game
        // if(points >= 100)
    }

    public void receiveCard(Card card) {
        this.cards.add(card);
    }
}