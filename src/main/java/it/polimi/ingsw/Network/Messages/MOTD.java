package it.polimi.ingsw.Network.Messages;

import java.io.Serializable;

public class MOTD implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final String content;

    public String getMOTD() {
        return content;
    }

    public MOTD(String string) {
        content = string;
    }

}
