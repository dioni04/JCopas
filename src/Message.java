/*
 * Format: {MESSAGE-CONTENT}/{SRC-ID}/{DEST-ID}
 */

public class Message {
    private int src;
    private int dest;
    private String content;

    enum MessageType {
        ACK("ACK"), HELLO("HELLO"), GAMESTART("START"),
        BATON("BATON"), CARD("CARD"), POINTS("POINTS"),
        ROUNDBEGIN("RBEGIN"), ROUNDEND("REND"), END("END");

        private final String key;

        MessageType(String key) {
            this.key = key;
        }

        String getKey() {
            return this.key;
        }
    }

    public Message(int src, int dest, String content) {
        this.src = src;
        this.dest = dest;
        this.content = content;
    }

    public int getSrc() {
        return src;
    }

    public int getDest() {
        return dest;
    }

    public String getContent() {
        return content;
    }

    public String cardMessage(Card card) {
        String msg = MessageType.CARD.getKey() + "-";
        msg += card.getRank().getKey();
        msg += card.getSuit().getKey();
        return msg;
    }

    public String pointsMessage(int points) {
        String msg = MessageType.POINTS.getKey() + "-" + Integer.toString(points);
        return msg;
    }

    public String simpleMessage(MessageType type) {
        String msg = type.getKey();
        return msg;
    }

    public String messageBuild() {
        return content + "/" + Integer.toString(src) + "/" + Integer.toString(dest);
    }
}
