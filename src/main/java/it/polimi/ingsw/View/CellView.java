package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;

import java.io.Serializable;

/**
 * Representation of a {@link it.polimi.ingsw.Model.Cell} sent by the server to clients.
 */
public class CellView implements Serializable {

    private static final long serialVersionUID = 17756L;
    int player;
    /**
     * True if the builder on the cell is the first.
     * {@link Builder#isFirst()}
     */
    boolean first;
    BuildingType height;

    public int getPlayer() {
        return player;
    }

    public boolean isFirst() {
        return first;
    }

    public BuildingType getHeight() {
        return height;
    }

    public CellView(BuildingType height, int player, boolean isFirst){
        this.height = height;
        this.player = player;
        this.first = isFirst;
    }

}
