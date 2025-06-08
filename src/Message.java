/*
 * Format: {MESSAGE-CONTENT}/{SRC-ID}/{DEST-ID}
 */

public class Message {
    private int src;
    private int dest;
    private String content;

    enum MessageType {
        IDASSIGN("ID"), MOON("MOON"),
        BATON("BATON"), CARD("CARD"),
        ROUNDBEGIN("RBEGIN"), ROUNDEND("REND"), END("END"),
        TRICKEND("TRICKEND"), CONNECTED("CONNECTED");

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

    public static String cardMessage(Card card) {
        String msg = MessageType.CARD.getKey() + "-";
        msg += card.getRank().getKey();
        msg += "_";
        msg += card.getSuit().getKey();
        return msg;
    }

    public static String simpleMessage(MessageType type) {
        String msg = type.getKey();
        return msg;
    }

    //Flag de validacao no final
    public static String idMessage(boolean valid) {
        String end = valid ? "1" : "0";
        String msg = MessageType.IDASSIGN.getKey() + "-" + end;
        return msg;
    }

    public String messageBuild() {
        return content + "/" + Integer.toString(src) + "/" + Integer.toString(dest);
    }
}
