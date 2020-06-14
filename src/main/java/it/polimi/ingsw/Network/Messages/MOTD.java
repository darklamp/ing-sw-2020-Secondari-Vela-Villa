package it.polimi.ingsw.Network.Messages;

import it.polimi.ingsw.Model.GameTable;

import java.io.Serializable;
import java.util.List;

/**
 * Holds the server's MOTD and the available gods as a list.
 */
public class MOTD implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final String content;

    private final List<String> gods = GameTable.completeGodList;

    public String getMOTD() {
        return content;
    }

    public List<String> getGods() {
        return gods;
    }

    public MOTD(String string) {
        content = string;
    }

}
