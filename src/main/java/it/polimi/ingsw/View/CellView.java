package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.BuildingType;

import java.io.Serializable;

public class CellView implements Serializable {

    private static final long serialVersionUID = 17756L;

    public int getPlayer() {
        return player;
    }

    int player;

    public boolean isFirst() {
        return first;
    }

    boolean first;

    public BuildingType getHeight() {
        return height;
    }

    BuildingType height;
    public CellView(BuildingType height, int player, boolean isFirst){
        this.height = height;
        this.player = player;
        this.first = isFirst;
    }

}
