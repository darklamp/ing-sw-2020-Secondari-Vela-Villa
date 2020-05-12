package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Network.SocketClientConnection;

import java.util.ArrayList;

/**
 * News object used to move info in observer pattern
 */
public class News {

    public News() {
        this.height = null;
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

    public void setRecipients(ArrayList<SocketClientConnection> list){
        this.recipients = list;
    }

    public void setRecipients(Player p){
        if (p != null){
            ArrayList<SocketClientConnection> list = new ArrayList<>();
            list.add(p.getConnection());
            this.recipients = list;
        }
    }

    private BuildingType height;

    private final Cell cell;

    public Pair getCoords() {
        return coords;
    }

    public void setCoords(int x, int y, int builderIndex){
        this.coords = new Pair(x,y);
        this.builderIndex = builderIndex;
        this.height = null;
    }

    public void setCoords(int x, int y, int builderIndex, int height){
        this.coords = new Pair(x,y);
        this.builderIndex = builderIndex;
        this.height = BuildingType.parse(height);
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

    public Cell getCell(GameTable gameTable) {
        try {
            return gameTable.getCell(this.coords.getFirst(),this.getCoords().getSecond());
        } catch (InvalidCoordinateException ignored) {
        }
        return null;
    }

    public Builder getBuilder(GameTable gameTable) {
        return gameTable.getCurrentPlayer().getBuilderList().get(builderIndex);
    }

    public BuildingType getHeight() {
        return height;
    }

}
