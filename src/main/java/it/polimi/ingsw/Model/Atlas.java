package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Atlas extends Builder{
    public Atlas(Cell position, Player player){
        super(position,player);
    }

    /**
     * @param oldheight see super
     * @param newheight see super
     * @throws AtlasException when I'm doing something only Atlas can do, aka building a dome on any level
     * @throws InvalidBuildException see super
     */
    @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws AtlasException, InvalidBuildException {
        super.isValidBuild(oldheight,newheight);
        if (newheight.equals(BuildingType.DOME) && !oldheight.equals(BuildingType.DOME)) throw new AtlasException();
        else verifyBuild(oldheight,newheight);
    }

    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }
}