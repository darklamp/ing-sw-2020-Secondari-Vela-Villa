package it.polimi.ingsw.Model;

import it.polimi.ingsw.Network.SocketClientConnection;

import java.util.ArrayList;

/**
 * News object used to move info in observer pattern
 */
public class News {

    private News() {
        this.height = BuildingType.NONE;
        this.builder = null;
        this.cell = null;
    }

    private News(String string) {
        this();
        this.string = string;
    }

    public News(String string, SocketClientConnection sender) {
        this(string);
        this.sender = sender;
        this.recipients = new ArrayList<>();
        this.recipients.add(sender);
    }

    private final BuildingType height;

    private final Cell cell;

    public Pair getCoords() {
        return coords;
    }

    public void setCoords(int x, int y, int builderIndex){
        this.coords = new Pair(x,y);
        this.builderIndex = builderIndex;
    }

    private int builderIndex;

    private Pair coords;

    private String string;

    public SocketClientConnection getSender() {
        return sender;
    }

    private SocketClientConnection sender;

    /**
     *  This arraylist contains the news' recipients, aka the people to whom the news is to be sent to
     *  a null value means the news needs to be sent to everyone
     **/
    private ArrayList<SocketClientConnection> recipients = null;

    public ArrayList<SocketClientConnection> getRecipients(){
        return this.recipients;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setInvalid() {
        isValid = false;
    }

    private boolean isValid = true;

    private final Builder builder;

    public String getString(){ return string; }

    public Cell getCell() {
        return cell;
    }

    public Builder getBuilder() {
        return builder;
    }

    public BuildingType getHeight() {
        return height;
    }

}
