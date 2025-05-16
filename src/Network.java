import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Network {

    enum MessageType {
        BATON, CARD, POINTS, END
    }

    public class Node {
        private String ip;
        private int port;
        private String nextNodeIp;
        private int nextNodePort;
        private boolean hasBaton;

        // Send message
        public void sendMessage(String message) {
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] buffer = message.getBytes();
                InetAddress address = InetAddress.getByName(nextNodeIp);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, nextNodePort);
                socket.send(packet);
                System.out.println("Sent message to " + nextNodeIp + ":" + nextNodePort);
            } catch (IOException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
        }

        private void getMessageType(String message) {
            // If is baton
            if ("BATON".equals(message)) {
                hasBaton = true;
                System.out.println("Baton received. I can now play.");
            }
            else if (hasBaton) {
                
            }
        }

        // Listen for message
        public void listen() {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                System.out.println("Node listening on port " + port);

                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength());
                    getMessageType(message);
                }
            } catch (Exception e) {
                System.err.println("Listening error: " + e.getMessage());
            }
        }
    }
}
