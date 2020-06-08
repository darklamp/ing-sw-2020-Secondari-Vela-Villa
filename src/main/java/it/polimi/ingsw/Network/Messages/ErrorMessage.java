package it.polimi.ingsw.Network.Messages;

import java.io.Serializable;
import java.util.Objects;

public class ErrorMessage implements Serializable, Message {

    private static final long serialVersionUID = 17756L;

    private final String content;

    public ErrorMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorMessage that = (ErrorMessage) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
