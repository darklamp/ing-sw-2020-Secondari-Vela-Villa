package it.polimi.ingsw.Network.Messages;

import it.polimi.ingsw.Client.ClientState;

import java.io.Serializable;

/**
 * Message sent to all clients everytime a state change happens.
 */
public class GameStateMessage implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final ClientState p1, p2, p3;

    private final String player1, player2, player3;

    private final int currentPlayer;

    public GameStateMessage(ClientState p1, ClientState p2, ClientState p3, String player1, String player2, String player3, int currentPlayer) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.currentPlayer = currentPlayer;
    }

    public ClientState get(int i) {
        return switch (i) {
            case 1 -> p2;
            case 2 -> p3;
            default -> p1;
        };
    }

    public String getName(int i) {
        return switch (i) {
            case 1 -> player2;
            case 2 -> player3;
            default -> player1;
        };
    }

    public String getCurrentPlayer() {
        return switch (currentPlayer) {
            case 1 -> player2;
            case 2 -> player3;
            default -> player1;
        };
    }
}
