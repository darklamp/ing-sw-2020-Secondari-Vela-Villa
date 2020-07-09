/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

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
        return serverConf.disk;
    }

    public static int getMaxPlayersNumber() {
        return serverConf.maxPlayers;
    }

    private static ServerConf serverConf = new ServerConf();


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
        String[] a;
        TimeUnit timerTimeUnit;
        try {
            serverConf = mapper.readValue(new File(System.getProperty("user.dir") + "/santorini.yaml"), ServerConf.class);
            logger.debug("Configuration file parsed.");
        } catch (IOException e) {
            logger.debug("Configuration file not found / invalid.");
        }
        Level verbosityLevel = switch (serverConf.verb.toLowerCase()) {
            case "error" -> ERROR;
            case "debug" -> DEBUG;
            case "warn" -> WARN;
            default -> INFO;
        };
        List<String> tempgods = serverConf.gods;
        GameTable.completeGodList = tempgods.stream().map(String::toUpperCase).collect(Collectors.toCollection(ArrayList::new));
        timerTimeUnit = (serverConf.timeunit.equals("h")) ? TimeUnit.HOURS : (serverConf.timeunit.equals("s") ? TimeUnit.SECONDS : TimeUnit.MINUTES);
        for (String s : args) {
            if (s.toLowerCase().contains("motd")) {
                try {
                    a = s.split("=");
                    serverConf.MOTD = a[1];
                } catch (Exception e) {
                    logger.error("Invalid motd supplied.");
                    System.exit(-1);
                }
            } else if (s.contains("ip")) {
                a = s.split("=");
                serverConf.ip = a[1];
            } else if (s.contains("port")) {
                a = s.split("=");
                try {
                    serverConf.port = Integer.parseInt(a[1]);
                    if (serverConf.port < 0 || serverConf.port > 65535) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    logger.error("Invalid port supplied.");
                    System.exit(0);
                }
            } else if (s.contains("console")) {
                serverConf.console = true;
            } else if (s.contains("verb")) {
                verbosityLevel = switch (s.split("=")[1].toLowerCase()) {
                    case "debug" -> DEBUG;
                    case "error" -> ERROR;
                    case "warn" -> WARN;
                    default -> INFO;
                };
                serverConf.verb = s.split("=")[1].toLowerCase();
            } else if (s.contains("disk")) {
                serverConf.disk = true;
            } else if (s.contains("maxPlayers")) {
                a = s.split("=");
                try {
                    int temp = Integer.parseInt(a[1]);
                    if (temp == 2 || temp == 3) serverConf.maxPlayers = temp;
                    else throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    logger.error("Invalid player number supplied.");
                    System.exit(-1);
                }
            } else if (s.contains("time")) {
                try {
                    a = s.split("=");
                    String[] b = a[1].split(",");
                    serverConf.moveTimer = Short.parseShort(b[0]);
                    timerTimeUnit = switch (b[1]) {
                        case "s", "S" -> TimeUnit.SECONDS;
                        case "m", "M" -> TimeUnit.MINUTES;
                        case "h", "H" -> TimeUnit.HOURS;
                        default -> throw new IllegalArgumentException();
                    };
                    serverConf.timeunit = b[1].toLowerCase();
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
        } else if (size == 2) serverConf.maxPlayers = 2;

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

        server = new Server(serverConf.port, serverConf.ip, serverConf.moveTimer, timerTimeUnit, serverConf.MOTD);
        if (serverConf.console) server.startConsole();
        server.run();
    }
}