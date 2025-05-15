public class Card {
    public enum Suit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, QUEEN, JACK, KING, SEVEN, ACE
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    public int getValue() {
        if (this.getRank() == Rank.QUEEN && this.getSuit() == Suit.SPADES)
            return 10;
        else if (this.getSuit() == Suit.HEARTS)
            return 1;
        return 0;
    }
}
