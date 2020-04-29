package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.BuildingType;

import java.io.Serializable;

public class CellView implements Serializable {
    public int getPlayer() {
        return player;
    }

    int player;

    public BuildingType getHeight() {
        return height;
    }

    BuildingType height;
    public CellView(BuildingType height, int player){
        this.height = height;
        this.player = player;
    }

}
