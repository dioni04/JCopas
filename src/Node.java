import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Node {
    private int id;
    private String ip;
    private int port;
    private String nextNodeIp;
    private int nextNodePort;
    private boolean hasBaton;
    public boolean connected = false;
    public int connections = 1;
    private boolean gameEnded = false;
    private DatagramSocket socket;
    private Game game;

    public Node(int port, int nextNodePort, String nextNodeIp, boolean isDealer, Game game) {
        this.ip = getLocalIP();
        this.port = port;
        this.nextNodeIp = nextNodeIp;
        this.nextNodePort = nextNodePort;
        this.hasBaton = isDealer;
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
        for (int i = 1; i < game.numPlayers; i++) {
            game.handler.createAndSendMessage(i, Message.idMessage(true));
        }
        // espera conexao
        while (connections < game.numPlayers) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        connected = true;
        if (Program.DEBUG)
            System.out.println("All nodes assigned. IDs confirmed.");
    }

    public void receiveBaton() {
        hasBaton = true;

    }

    public void passBaton() {
        hasBaton = false;
        var msg = new Message(this.id, this.id + 1, Message.MessageType.BATON.getKey());
        sendMessage(msg.messageBuild());
    }

    public static String getLocalIP() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Unable to get local IP address: " + e.getMessage());
            return "127.0.0.1"; // Fallback to localhost
        }
    }
}
