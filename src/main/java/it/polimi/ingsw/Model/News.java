package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Network.SocketClientConnection;

import java.io.Serializable;

public class News implements Serializable {

    //TODO idea: creare una stringa random per ogni giocatore all'inizio del gioco
    // e aggiungerla alle news che inviano, per verificare identità

    //TODO: check news sia valida

    public News() {
        this.number = 0;
        this.height = BuildingType.NONE;
        this.builder = null;
        this.cell = null;
    }

    public News(String string) {
        this();
        this.string = string;
    }

    public News(String string, SocketClientConnection c) {
        this(string);
        this.c = c;
    }

    private final BuildingType height;

    private final Cell cell;

    private String string;

    public SocketClientConnection getClient() {
        return c;
    }

    private SocketClientConnection c;

    private final int number;

    private final Builder builder;

    public String getString(){ return string; }

    public int getNumber() {
        return number;
    }

    public Cell getCell() {
        return cell;
    }

    public Builder getBuilder() {
        return builder;
    }

    public BuildingType getHeight() {
        return height;
    }


    public News(Cell cell, Builder builder) {
        this.cell = cell;
        this.builder = builder;
        this.number = 0;
        this.height = BuildingType.NONE;
        this.c = null;
    }

    public News(Player player) {
        this.cell = null;
        this.builder = null;
        this.number = 0;
        this.height = BuildingType.NONE;
        this.c = null;
    }

    public News(int number) {
        this.number = number;
        this.height = BuildingType.NONE;
        this.builder = null;
        this.cell = null;
        this.c = null;
    }

}
