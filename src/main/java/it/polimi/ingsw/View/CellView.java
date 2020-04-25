package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Player;

public class CellView {
    int player;
    BuildingType height;
    public CellView(BuildingType height, int player){
        this.height = height;
        this.player = player;
    }
}
