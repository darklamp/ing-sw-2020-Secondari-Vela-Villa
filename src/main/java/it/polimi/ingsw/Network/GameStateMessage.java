package it.polimi.ingsw.Network;

import it.polimi.ingsw.Client.ClientState;

import java.io.Serializable;

/**
 * Message sent to all clients everytime a state change happens.
 */
public class GameStateMessage implements Serializable {

    private static final long serialVersionUID = 17756L;

    private final ClientState p1, p2, p3;

    public GameStateMessage(ClientState p1, ClientState p2, ClientState p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public ClientState get(short i) {
        return switch (i) {
            case 1 -> p2;
            case 2 -> p3;
            default -> p1;
        };
    }
}
