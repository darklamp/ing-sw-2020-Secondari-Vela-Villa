package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;

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

    private final BuildingType height;

    private final Cell cell;

    private final int number;

    private final Builder builder;

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
    }

    public News(Player player) {
        this.cell = null;
        this.builder = null;
        this.number = 0;
        this.height = BuildingType.NONE;
    }

    public News(int number) {
        this.number = number;
        this.height = BuildingType.NONE;
        this.builder = null;
        this.cell = null;
    }

}
