package it.polimi.ingsw.Network.Messages;

import it.polimi.ingsw.Model.GameTable;
import it.polimi.ingsw.ServerMain;

import java.io.Serializable;
import java.util.List;

/**
 * Holds the server's MOTD, the available gods as a list, and a flag to tell clients if persistence is enabled.
 */
public class MOTD implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final String content;

    private final List<String> gods = GameTable.completeGodList;

    private final boolean persistence = ServerMain.persistence();

    public boolean persistenceEnabled() {
        return persistence;
    }

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
