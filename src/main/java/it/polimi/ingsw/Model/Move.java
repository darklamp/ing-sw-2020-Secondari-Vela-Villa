package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;

public class Move {

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

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    private Builder builder;
    public Move(Cell cell, Builder builder) {
        this.cell = cell;
        this.builder = builder;
    }
}
