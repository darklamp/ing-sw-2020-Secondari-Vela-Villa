package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Player;

public class CellView {
    String player;
    BuildingType height;
    public CellView(BuildingType height, Player player){
        this.height = height;
        this.player = player == null ? null : player.getNickname();
    }
}
