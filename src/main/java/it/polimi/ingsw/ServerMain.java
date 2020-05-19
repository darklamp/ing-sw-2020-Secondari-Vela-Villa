package it.polimi.ingsw;

import it.polimi.ingsw.Network.Server;

public class ServerMain {
    public static void main(String[] args) {
        Server server;
        int port = 1337;
        String ip = "localhost";
        String[] a;
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
            }
        }
        server = new Server(port, ip);
        server.run();
    }
}