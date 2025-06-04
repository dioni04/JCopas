public class Program {
    public static final boolean DEBUG = false;
    public static final boolean AUTO = false;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java Program <port> <nextNodeIp> <nextNodePort> [dealer(0-1)]");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String nextNodeIp = args[1];
        int nextNodePort = Integer.parseInt(args[2]);
        boolean isDealer = (args.length > 3) && "1".equalsIgnoreCase(args[3]);

        new Game(port, nextNodePort, nextNodeIp, isDealer);
    }
}