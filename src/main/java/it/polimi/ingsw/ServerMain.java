package it.polimi.ingsw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.ServerConf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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
     *
     * @param args command line args (see README for available params)
     */
    public static void main(String[] args) {
        Server server;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ServerConf serverConf;
        int port;
        String ip, MOTD;
        String[] a;
        short moveTimer;
        TimeUnit timerTimeUnit;
        boolean console;
        try {
            serverConf = mapper.readValue(new File(System.getProperty("user.dir") + "/santorini.yaml"), ServerConf.class);
            System.out.println("Configuration file parsed.");
        } catch (IOException e) {
            serverConf = new ServerConf();
        }
        verbose = serverConf.verbose || serverConf.v;
        persistence = serverConf.disk;
        port = serverConf.port;
        console = serverConf.console;
        moveTimer = serverConf.moveTimer;
        MOTD = serverConf.MOTD;
        ip = serverConf.ip;
        List<String> tempgods = serverConf.gods;
        GameTable.completeGodList = tempgods.stream().map(String::toUpperCase).collect(Collectors.toCollection(ArrayList::new));
        timerTimeUnit = (serverConf.timeunit.equals("h")) ? TimeUnit.HOURS : (serverConf.timeunit.equals("s") ? TimeUnit.SECONDS : TimeUnit.MINUTES);

        for (String s : args) {
            if (s.toLowerCase().contains("motd")) {
                try {
                    a = s.split("=");
                    MOTD = a[1];
                } catch (Exception e) {
                    System.err.println("[CRITICAL] Invalid MOTD supplied.");
                    System.exit(0);
                }
            } else if (s.contains("ip")) {
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