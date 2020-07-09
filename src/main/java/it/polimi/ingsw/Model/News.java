/*
 * Santorini
 * Copyright (C)  2020  Alessandro Villa and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * E-mail contact addresses:
 * darklampz@gmail.com
 * alessandro17.villa@mail.polimi.it
 *
 */

package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Network.SocketClientConnection;
import it.polimi.ingsw.Utility.Pair;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * News object used to move info in observer pattern
 */
public class News implements Serializable {

    /**
     * @see Serializable
     */
    private static final long serialVersionUID = 17756L;

    /**
     * Height of the build to be performed, if any.
     */
    private BuildingType height;

    /**
     * If coords have been set, this object contains the index of the player who sent the move; else, it holds no value.
     */
    private int builderIndex;

    /**
     * Eventual string to be passed.
     */
    private String string;

    /**
     * Connection of the player who sent the news
     */
    private transient SocketClientConnection sender;

    /**
     * This arraylist contains the news' recipients, aka the people to whom the news is to be sent to.
     * NB: A null value (default) means the news needs to be sent to everyone
     **/
    private ArrayList<SocketClientConnection> recipients = null;

    /**
     * Coordinates of move/build to be performed, if any.
     */
    private Pair coords;

    /**
     * True if the news is valid, false otherwise.
     */
    private boolean isValid = true;

    public News() {
        this.height = null;
    }

    private News(String string) {
        this();
        this.string = string;
    }

    public News(String string, SocketClientConnection sender) {
        this(string);
        this.sender = sender;
        this.recipients = null;
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

    public SocketClientConnection getSender() {
        return sender;
    }

    public ArrayList<SocketClientConnection> getRecipients(){
        return this.recipients;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setInvalid() {
        isValid = false;
    }

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
