package it.polimi.ingsw;

import it.polimi.ingsw.Network.Server;


public class ServerMain {
    public static boolean verbose() {
        return verbose;
    }

    private static boolean verbose = false;

    public static boolean isRestartFromDisk() {
        return restartFromDisk;
    }

    private static boolean restartFromDisk = false;


    /**
     * Launch the server
     */
    public static void main(String[] args) {
        Server server;
        int port = 1337;
        String ip = "localhost";
        String[] a;
        boolean console = false;
        for (String s : args) {
            if (s.contains("ip")) {
                a = s.split("=");
                ip = a[1];
            } else if (s.contains("port")) {
                a = s.split("=");
                try {
                    port = Integer.parseInt(a[1]);
                    if (port < 0 || port > 65535) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.err.println("[CRITICAL] Invalid port supplied.");
                    System.exit(0);
                }
            } else if (s.contains("console")) {
                console = true;
            } else if (s.contains("v")) {
                verbose = true;
            } else if (s.contains("disk")) {
                restartFromDisk = true;
            }
        }
        server = new Server(port, ip);
        if (console) server.startConsole();
        server.run();
    }
}