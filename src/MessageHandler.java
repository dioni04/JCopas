
public class MessageHandler {

    private Game game;

    public MessageHandler(Game game) {
        this.game = game;
    }

    public void sendMessage(int dest, String content) {
        var msg = new Message(game.getNode().getId(), dest, content);
        game.getNode().sendMessage(msg.messageBuild());
    }

    public void handleMessage(String message) {
        Message parsedMessage = parseMessage(message);
        if (parsedMessage == null) {
            System.out.println("Invalid message format");
            return;
        }

        boolean isBroadcast = parsedMessage.getDest() == -1;
        boolean isForMe = parsedMessage.getDest() == game.getId();
        boolean isSetupPhase = !game.getNode().connected || game.getId() == -1;

        // Se nao recebeu id ainda ou mensagem é pra este nodo
        if (isSetupPhase || isForMe || isBroadcast) {
            switch (parsedMessage.getContent().split("-")[0].trim()) {
                case "HELLO":
                    handleHello(parsedMessage);
                    break;
                case "ID":
                    handleID(parsedMessage);
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
                case "TRICKEND":
                    handleTrickEnd(parsedMessage);
                    break;
                default:
                    System.out.println("Unknown message type: " + parsedMessage.getContent());
            }
        }
        // Send along if not full loop
        if (parsedMessage.getSrc() != game.getId())
            game.getNode().sendMessage(message);
    }

    // Parse message from raw string
    private static Message parseMessage(String message) {
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

    private void handleHello(Message msg) {
        System.out.println("Received HELLO from Node " + msg.getSrc());
    }

    private void handleID(Message msg) {
        if (game.getId() == 0) {
            game.getNode().connections++;
            return;
        }
        System.out.println("Received ID from Node " + msg.getSrc());
        game.getNode().connected = true;
        game.getNode().setId(msg.getDest());
        return;
    }

    private void handleGameStart(Message msg) {
        System.out.println("Game started by Node " + msg.getSrc());
        // game.startGame();
    }

    private void handleBaton(Message msg) {
        System.out.println("Baton received from Node " + msg.getSrc());
        game.getNode().receiveBaton();
        game.getPlayer().playTurn();
    }

    private void handleCard(Message msg) {
        String cardDetails = msg.getContent().split("-")[1];

        var suit = Card.Suit.getByKey(cardDetails.substring(0, 1));
        var rank = Card.Rank.getByKey(cardDetails.substring(1));

        Card card = new Card(suit, rank);
        System.out.println("Received card: " + card.toString() + " from Node " + msg.getSrc());

        if (game.getPlayer().receivingCards) {
            game.getPlayer().receiveCard(card);
            return;
        }
        if (game.cardsPlayed.isEmpty())
            game.setCurrentSuit(suit);
        game.cardsPlayed.add(card);
    }

    private void handlePoints(Message msg) {
        System.out.println("Received points from Node " + msg.getSrc());
        game.getPlayer().gainPoints();
    }

    private void handleRoundBegin(Message msg) {
        System.out.println("Round beginning.");
        game.getPlayer().roundStart();
    }

    private void handleRoundEnd(Message msg) {
        System.out.println("Round ending.");
        game.getPlayer().roundEnd();
    }

    private void handleEnd(Message msg) {
        System.out.println("Game over. Finalizing...");
        game.endGame();
    }

    private void handleTrickEnd(Message msg) {
        System.out.println("Trick ending.");

        game.getPlayer().trickEnd(false);
    }

    public void broadcastMessage(Message.MessageType type) {
        game.handler.sendMessage(-1, Message.simpleMessage(type));
    }

    public void broadcastMessage(Card card) {
        game.handler.sendMessage(-1, Message.cardMessage(card));
    }

}
