package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;

public class News {

    public News() {

    }

    private BuildingType height;

    private Cell cell;

    private int number = 0;

    private Player player;

    private Builder builder;

    public int getNumber() {
        return number;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

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

    public BuildingType getHeight() {
        return height;
    }

    public void setHeight(BuildingType height) {
        this.height = height;
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
