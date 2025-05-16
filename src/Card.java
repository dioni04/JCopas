public class Card {
    public enum Suit {
        SPADES("S"), HEARTS("H"), DIAMONDS("D"), CLUBS("C");

        private final String key;

        Suit(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

    public enum Rank {
        TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"),
        QUEEN("Q"), JACK("J"), KING("K"), SEVEN("7"), ACE("A");

        private final String key;

        Rank(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
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

    public String toMessage() {
        String ret = "CARD-";
        ret += this.rank.getKey();
        ret += this.suit.getKey();
        return ret;
    }

    public int getValue() {
        if (this.getRank() == Rank.QUEEN && this.getSuit() == Suit.SPADES)
            return 10;
        else if (this.getSuit() == Suit.HEARTS)
            return 1;
        return 0;
    }
}
