import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Network {

    public static String getLocalIP() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Unable to get local IP address: " + e.getMessage());
            return "127.0.0.1"; // Fallback to localhost
        }
    }

    public static class Node {
        private int id;
        private String ip;
        private int port;
        private String nextNodeIp;
        private int nextNodePort;
        private boolean hasBaton;
        private DatagramSocket socket;
        private Game game;

        public Node(int port, int nextNodePort, String nextNodeIp, boolean isDealer, Game game) {
            this.ip = getLocalIP();
            this.port = port;
            this.nextNodeIp = nextNodeIp;
            this.nextNodePort = nextNodePort;
            this.hasBaton = isDealer;
            this.game = game;
            try {
                this.socket = new DatagramSocket(this.port);
                System.out.println("Node initialized on port " + port);
                handshake();
            } catch (IOException e) {
                System.err.println("Error initializing socket: " + e.getMessage());
            }
            if (isDealer) {
                id = 0;
                System.out.println("Starting as dealer, sending first baton...");
                sendMessage(Message.MessageType.BATON.getKey());
            }
        }

        public int getId() {
            return id;
        }
        
        // Send message
        public void sendMessage(String message) {
            try {
                byte[] buffer = message.getBytes();
                InetAddress address = InetAddress.getByName(nextNodeIp);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, nextNodePort);
                socket.send(packet);
                System.out.println("Sent message to " + nextNodeIp + ":" + nextNodePort);
            } catch (IOException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
        }

        // Listen for message
        public void listen() {
            try {
                System.out.println("Node listening on port " + port);

                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength());
                    game.handler.handleMessage(message);
                }
            } catch (Exception e) {
                System.err.println("Listening error: " + e.getMessage());
            }
        }

        private void handshake() {
            boolean acknowledged = false;
            int attempts = 0;
            int maxAttempts = 3;

            while (!acknowledged && attempts < maxAttempts) {
                try {
                    // Send HELLO message to the next node
                    sendMessage(Message.MessageType.HELLO.getKey());

                    // Wait for ACK
                    socket.setSoTimeout(1000); // 1 second timeout for response
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    try {
                        socket.receive(packet);
                        String response = new String(packet.getData(), 0, packet.getLength());
                        if ("ACK".equals(response)) {
                            acknowledged = true;
                            System.out.println("Handshake successful with " + nextNodeIp + ":" + nextNodePort);
                        }
                    } catch (IOException e) {
                        System.out.println("No response from " + nextNodeIp + ":" + nextNodePort + ". Retrying...");
                        attempts++;
                    }
                } catch (IOException e) {
                    System.err.println("Error during handshake: " + e.getMessage());
                }
            }

            if (!acknowledged) {
                System.err.println("Failed to establish connection with " + nextNodeIp + ":" + nextNodePort);
            }
        }

        public void receiveBaton() {
            hasBaton = true;
        }

        public void passBaton() {
            hasBaton = false;
            // Pass baton to next player
        }
    }
}
