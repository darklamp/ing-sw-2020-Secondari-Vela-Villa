package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.AtlasException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;

public class Demeter extends Builder{
    public Demeter(Cell position, Player player) {
        super(position,player);
    }

    @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws AtlasException, InvalidBuildException {
        super.isValidBuild(oldheight,newheight);

        else verifyBuild(oldheight,newheight);
    }


}
