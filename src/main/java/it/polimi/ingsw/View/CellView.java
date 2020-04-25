package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Player;

public class CellView {
    String player;
    int x,y;
    BuildingType height;
    public CellView(int x, int y, BuildingType height, Player player){
        this.x = x;
        this.y = y;
        this.height = height;
        this.player = player == null ? null : player.getNickname();
    }
}
