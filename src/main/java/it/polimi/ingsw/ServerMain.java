package it.polimi.ingsw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.ServerConf;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.Level.*;


public class ServerMain {

    public static boolean persistence() {
        return persistence;
    }

    private static boolean persistence = false;

    public static int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }

    /**
     * Holds maximum number of players allowed for games.
     */
    private static int maxPlayersNumber = 3;

    /**
     * Launches the server.
     * The server uses a file (error.log) to keep a permanent track of errors.
     * Every other log message is written to stdout.
     *
     * @param args command line args (see README for available params)
     */
    public static void main(String[] args) {
        Server server;
        Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
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
            logger.debug("Configuration file parsed.");
        } catch (IOException e) {
            serverConf = new ServerConf();
            logger.debug("Configuration file not found / invalid.");
        }
        Level verbosityLevel = switch (serverConf.verb.toLowerCase()) {
            case "error" -> ERROR;
            case "debug" -> DEBUG;
            case "warn" -> WARN;
            default -> INFO;
        };
        maxPlayersNumber = serverConf.maxPlayers;
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
                    logger.error("Invalid motd supplied.");
                    System.exit(-1);
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
                    logger.error("Invalid port supplied.");
                    System.exit(0);
                }
            } else if (s.contains("console")) {
                console = true;
            } else if (s.contains("verb")) {
                verbosityLevel = switch (s.split("=")[1].toLowerCase()) {
                    case "debug" -> DEBUG;
                    case "error" -> ERROR;
                    case "warn" -> WARN;
                    default -> INFO;
                };
            } else if (s.contains("disk")) {
                persistence = true;
            } else if (s.contains("maxPlayers")) {
                a = s.split("=");
                try {
                    int temp = Integer.parseInt(a[1]);
                    if (temp == 2 || temp == 3) maxPlayersNumber = temp;
                    else throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    logger.error("Invalid player number supplied.");
                    System.exit(-1);
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
                    logger.error("Invalid timer supplied.");
                    System.exit(-1);
                }
            }
        }

        // check that there are at least 2 (not necessarily distinct) gods available
        int size = tempgods.size();
        if (size <= 1) {
            logger.error("Invalid number of gods. There must be at least 1.");
            System.exit(-1);
        } else if (size == 2) maxPlayersNumber = 2;

        // set logger verbosity
        Configurator.setRootLevel(verbosityLevel);

        // print configuration parameters
        logger.info("Starting server with the following parameters: ");

        //noinspection unchecked
        for (Field f : ReflectionUtils.getAllFields(ServerConf.class)) {
            try {
                logger.info(f.getName() + ": " + f.get(serverConf));
            } catch (IllegalAccessException ignored) {
            }
        }

        server = new Server(port, ip, moveTimer, timerTimeUnit, MOTD);
        if (console) server.startConsole();
        server.run();
    }
}