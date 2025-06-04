import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Node {
    private int id;
    private int port;
    private String nextNodeIp;
    private int nextNodePort;
    public boolean connected = false;
    public int connections = 1;
    private boolean gameEnded = false;
    private DatagramSocket socket;
    private Game game;

    public Node(int port, int nextNodePort, String nextNodeIp, boolean isDealer, Game game) {
        this.port = port;
        this.nextNodeIp = nextNodeIp;
        this.nextNodePort = nextNodePort;
        this.game = game;
        this.id = -1;

        try {
            this.socket = new DatagramSocket(this.port);
            if (Program.DEBUG)
                System.out.println("Node initialized on port " + port);
        } catch (IOException e) {
            System.err.println("Error initializing socket: " + e.getMessage());
        }
        if (isDealer) {
            id = 0;
        }
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNextNodeIp() {
        return nextNodeIp;
    }

    public int getNextNodePort() {
        return nextNodePort;
    }

    // Send message
    public void sendMessage(String message) {
        try {
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName(nextNodeIp);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, nextNodePort);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    // Listen for message
    public void listen() {
        System.out.println("Listening...");
        try {
            if (Program.DEBUG)
                System.out.println("Node listening on port " + port);

            while (!gameEnded) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                game.handler.handleMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Listening error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void assignIDs() {
        System.out.println("Establishing connection...");
        // espera conexao
        while (connections < game.numPlayers) {
            try {
                for (int i = 1; i < game.numPlayers; i++) {
                    game.handler.createAndSendMessage(i, Message.idMessage(true));
                }
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (Program.DEBUG)
            System.out.println("All nodes assigned. IDs confirmed.");
        game.handler.broadcastMessage(Message.MessageType.CONNECTED);
        connected = true;
    }

}
