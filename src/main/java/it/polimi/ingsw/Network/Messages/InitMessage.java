package it.polimi.ingsw.Network.Messages;

import java.io.Serializable;

public class InitMessage implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final int pIndex, size, gameIndex, god1, god2, god3;

    public InitMessage(int pIndex, int size, int gameIndex, int god1, int god2, int god3) {
        this.pIndex = pIndex;
        this.size = size;
        this.gameIndex = gameIndex;
        this.god1 = god1;
        this.god2 = god2;
        this.god3 = god3;
    }


    public int getPlayerIndex() {
        return pIndex;
    }

    public int getSize() {
        return size;
    }

    public int getGameIndex() {
        return gameIndex;
    }

    public int getGod(int i) {
        return switch (i) {
            case 2 -> god3;
            case 1 -> god2;
            default -> god1;
        };
    }
}
