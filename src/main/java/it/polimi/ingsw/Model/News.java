package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;

public class News {

    public News() {

    }

    public BuildingType getHeight() {
        return height;
    }

    public void setHeight(BuildingType height) {
        this.height = height;
    }

    private BuildingType height;

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    private Cell cell;

    public int getNumber() {
        return number;
    }

    private int number = 0;

    private Player player;

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    private Builder builder;
    public News(Cell cell, Builder builder) {
        this.cell = cell;
        this.builder = builder;
        this.player = null;
    }
    public News(Player player) {
        this.cell = null;
        this.builder = null;
        this.player = player;
    }
    public News(int number) {
        this.number = number;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
