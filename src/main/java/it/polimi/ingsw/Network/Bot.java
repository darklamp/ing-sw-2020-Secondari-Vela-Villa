package it.polimi.ingsw.Network;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.News;
import it.polimi.ingsw.ServerMain;
import it.polimi.ingsw.Utility.Pair;
import it.polimi.ingsw.View.CellView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static it.polimi.ingsw.Client.ClientState.*;

public class Bot extends SocketClientConnection {

    public Bot() {
        try {
            pythonSocket = new Socket("localhost", 13337);
            pr = pythonSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(pythonSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ;

    private StringBuilder toSend = new StringBuilder();
    private String receive;
    private ClientState oldState = WAIT;
    private CellView[][] oldBoard = null;
    private CellView[][] newBoard = null;
    private final int[] cpuMove = new int[3];
    private int builderIndex;
    private Pair end;
    private Socket pythonSocket;
    private OutputStream pr;
    private BufferedReader in;
    private String news;
    private boolean flagTable = false;
    private boolean first = true;
    private ClientState opponentState;


    @Override
    public void send(final Object message) {
        if (message instanceof GameStateMessage g) {
            opponentState = g.get((short) 1);
        } else if (message instanceof CellView[][]) {
            if (!flagTable) {
                if (oldBoard == null) oldBoard = (CellView[][]) message;
                else oldBoard = newBoard;
                newBoard = (CellView[][]) message;
            } else flagTable = false;
            if (opponentState == BUILD) getMoveChange();
            else if (opponentState == WAIT) {
                if (this.getPlayer().getState() == MOVE) {
                    getBuildChange();
                    try {
                        pr.write(toSend.toString().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        receive = in.readLine();
                        if (ServerMain.verbose()) System.out.println("Received from bot: " + receive);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    parse();
                    news = "MOVE@@@" + parseMove();
                    if (ServerMain.verbose()) System.out.println("Sending move: " + news);
                    setNews(new News(news, this), "INPUT");
                } else if (this.getPlayer().getState() == BUILD) {
                    news = "BUILD@@@" + parseBuild();
                    if (ServerMain.verbose()) System.out.println("Sending build: " + news);
                    setNews(new News(news, this), "INPUT");
                    toSend.delete(0, toSend.length());
                } else flagTable = true;
            }
        }
    }

    @Override
    public void run() {
    }


    private void getMoveChange() {
        Pair start = null;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (oldBoard[i][j].getPlayer() != newBoard[i][j].getPlayer()) {
                    if (newBoard[i][j].getPlayer() == -1) {
                        start = new Pair(i, j);
                        builderIndex = oldBoard[i][j].isFirst() ? 0 : 1;
                    } else end = new Pair(i, j);
                }
            }
        }
        Pair diff = new Pair(start.getFirst() - end.getFirst(), start.getSecond() - end.getSecond());
        toSend.append(builderIndex);
        getBinary(diff);
    }

    private void getBuildChange() {
        Pair cell = null;
        boolean flag = false;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (oldBoard[i][j].getHeight() != newBoard[i][j].getHeight()) {
                    cell = new Pair(i, j);
                    flag = true;
                    break;
                }
            }
            if (flag) break;
        }
        Pair diff = new Pair(end.getFirst() - cell.getFirst(), end.getSecond() - cell.getSecond());
        getBinary(diff);
    }

    private void getBinary(Pair diff) {
        int o = switch (diff.getFirst()) {
            case 1 -> switch (diff.getSecond()) {
                case 1 -> 0;
                case -1 -> 2;
                default -> 1;
            };
            case -1 -> switch (diff.getSecond()) {
                case 1 -> 5;
                case -1 -> 7;
                default -> 6;
            };
            default -> diff.getSecond() == 1 ? 3 : 4;
        };
        if (o < 4) toSend.append(Integer.toBinaryString(0));
        if (o == 1 || o == 0) toSend.append(Integer.toBinaryString(0));
        toSend.append(Integer.toBinaryString(o));
    }

    private Pair getPair(boolean b) {
        return switch (cpuMove[b ? 1 : 2]) {
            case 0 -> new Pair(1, 1);
            case 1 -> new Pair(1, 0);
            case 10 -> new Pair(1, -1);
            case 11 -> new Pair(0, 1);
            case 100 -> new Pair(0, -1);
            case 101 -> new Pair(-1, 1);
            case 110 -> new Pair(-1, 0);
            default -> new Pair(-1, -1);
        };
    }

    private void parse() {
        int integer = Integer.parseInt(receive);
        String ss = Integer.toBinaryString(integer);
        int length = ss.length();
        cpuMove[2] = Integer.parseInt(ss.substring(length - 3, length));
        if (length < 7) {
            cpuMove[0] = 1;
            cpuMove[1] = Integer.parseInt(ss.substring(0, length - 3));
        } else {
            cpuMove[0] = 2;
            cpuMove[1] = Integer.parseInt(ss.substring(1, 4));
        }
        if (ServerMain.verbose()) System.out.println("Parsed: " + cpuMove[0] + "," + cpuMove[1] + "," + cpuMove[2]);
    }

    private String parseMove() {
        Pair p = getPair(true);
        Cell c = this.getPlayer().getBuilderList().get(cpuMove[0] - 1).getPosition();
        Pair pos = c.getPosition();
        return (pos.getFirst() - p.getFirst()) + "@@@" + (pos.getSecond() - p.getSecond()) + "@@@" + cpuMove[0];
    }

    private String parseBuild() {
        Pair p = getPair(false);
        Cell c = this.getPlayer().getBuilderList().get(cpuMove[0] - 1).getPosition();
        Pair pos = c.getPosition();
        return (pos.getFirst() - p.getFirst()) + "@@@" + (pos.getSecond() - p.getSecond()) + "@@@" + cpuMove[0];
    }
}
