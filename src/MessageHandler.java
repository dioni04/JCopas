public class MessageHandler {

    private Game game;

    public MessageHandler(Game game) {
        this.game = game;
    }

    // Main message handling method
    public void handleMessage(String message) {
        Message parsedMessage = parseMessage(message);
        if (parsedMessage == null) {
            System.out.println("Invalid message format");
            // NACK
            return;
        }
        if (parsedMessage.getDest() != this.game.getNode().getId()) {
            // Send along
            game.getNode().sendMessage(message);
            return;
        }
        switch (parsedMessage.getContent().split("-")[0]) {
            case "ACK":
                handleAck(parsedMessage);
                break;
            case "HELLO":
                handleHello(parsedMessage);
                break;
            case "START":
                handleGameStart(parsedMessage);
                break;
            case "BATON":
                handleBaton(parsedMessage);
                break;
            case "CARD":
                handleCard(parsedMessage);
                break;
            case "POINTS":
                handlePoints(parsedMessage);
                break;
            case "RBEGIN":
                handleRoundBegin(parsedMessage);
                break;
            case "REND":
                handleRoundEnd(parsedMessage);
                break;
            case "END":
                handleEnd(parsedMessage);
                break;
            default:
                System.out.println("Unknown message type: " + parsedMessage.getContent());
        }
    }

    // Parse message from raw string
    private Message parseMessage(String message) {
        try {
            String[] parts = message.split("/");
            if (parts.length != 3)
                return null;

            String content = parts[0];
            int src = Integer.parseInt(parts[1]);
            int dest = Integer.parseInt(parts[2]);
            return new Message(src, dest, content);
        } catch (Exception e) {
            System.err.println("Error parsing message: " + e.getMessage());
            return null;
        }
    }

    // Individual message handling methods
    private void handleAck(Message msg) {
        System.out.println("Received ACK from Node " + msg.getSrc());
    }

    private void handleHello(Message msg) {
        System.out.println("Received HELLO from Node " + msg.getSrc());
    }

    private void handleGameStart(Message msg) {
        System.out.println("Game started by Node " + msg.getSrc());
        game.startGame();
    }

    private void handleBaton(Message msg) {
        System.out.println("Baton received from Node " + msg.getSrc());
        game.getNode().receiveBaton();
    }

    private void handleCard(Message msg) {
        String cardDetails = msg.getContent().split("-")[1];
        
        var suit = Card.Suit.getByKey(cardDetails.substring(0, 1));
        var rank = Card.Rank.getByKey(cardDetails.substring(1));
        
        Card card = new Card(suit, rank);

        if (game.getPlayer().receivingCards) {
            game.getPlayer().receiveCard(card);           
            System.out.println("Received card: " + cardDetails + " from Node " + msg.getSrc());
            return;
        }
        game.cardsPlayed.add(card);
    }

    private void handlePoints(Message msg) {
        int points = Integer.parseInt(msg.getContent().split("-")[1]);
        System.out.println("Received points: " + points + " from Node " + msg.getSrc());
        game.updatePoints(points);
    }

    private void handleRoundBegin(Message msg) {
        System.out.println("Round beginning.");
        game.getPlayer().roundStart();
    }

    private void handleRoundEnd(Message msg) {
        System.out.println("Round ending.");
        game.cardsPlayed.clear();
        game.getPlayer().roundEnd();
    }

    private void handleEnd(Message msg) {
        System.out.println("Game over. Finalizing...");
        game.getPlayer().endGame();
    }
}
