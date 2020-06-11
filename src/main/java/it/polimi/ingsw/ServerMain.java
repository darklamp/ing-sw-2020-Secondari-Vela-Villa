package it.polimi.ingsw;

import it.polimi.ingsw.Network.Server;

import java.util.concurrent.TimeUnit;


public class ServerMain {
    public static boolean verbose() {
        return verbose;
    }

    private static boolean verbose = false;

    public static boolean persistence() {
        return persistence;
    }

    private static boolean persistence = false;


    /**
     * Launch the server
     */
    public static void main(String[] args) {
        Server server;
        int port = 1337;
        String ip = "0.0.0.0";
        String[] a;
        short moveTimer = 2;
        TimeUnit timerTimeUnit = TimeUnit.MINUTES;
        boolean console = false;
        String MOTD = null;
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
                persistence = true;
            } else if (s.toLowerCase().contains("motd")) {
                try {
                    a = s.split("=");
                    MOTD = a[1];
                } catch (Exception e) {
                    System.err.println("[CRITICAL] Invalid MOTD supplied.");
                    System.exit(0);
                }
            } else if (s.contains("time")) {
                try {
                    a = s.split("=");
                    String[] b = a[1].split(",");
                    moveTimer = Short.parseShort(b[0]);
                    timerTimeUnit = switch (b[1]) {
                        case "s", "S" -> TimeUnit.SECONDS;
                        case "m", "M" -> TimeUnit.MINUTES;
                        case "h", "H" -> TimeUnit.HOURS;
                        default -> throw new IllegalArgumentException();
                    };
                } catch (Exception e) {
                    System.err.println("[CRITICAL] Invalid timer supplied.");
                    System.exit(0);
                }
            }
        }
        server = new Server(port, ip, moveTimer, timerTimeUnit, MOTD);
        if (console) server.startConsole();
        server.run();
    }
}