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

        public Suit getByKey(String key) {
            return Suit.valueOf(key);
        }
    }

    public enum Rank {
        TWO("2", 1), THREE("3", 2), FOUR("4", 3), FIVE("5", 4), SIX("6", 5),
        SEVEN("7", 6), EIGHT("8", 7), NINE("9", 8), TEN("10", 9),
        JACK("J", 10), QUEEN("Q", 11), KING("K", 12), ACE("A", 13);

        private final String key; // For messages
        private final int value; // For checking which card is higher

        Rank(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public int getValue() {
            return this.value;
        }

        public Rank getByKey(String key) {
            return Rank.valueOf(key);
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

    public int getValue() {
        if (this.getRank() == Rank.QUEEN && this.getSuit() == Suit.SPADES)
            return 10;
        else if (this.getSuit() == Suit.HEARTS)
            return 1;
        return 0;
    }
}
