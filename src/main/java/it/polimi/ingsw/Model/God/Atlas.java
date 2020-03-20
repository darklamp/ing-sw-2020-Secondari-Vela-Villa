package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Exceptions.*;

public class Atlas extends GodDecorator{
    public Atlas(Builder builder){
        super(builder);
    }

    /**
     * @param oldheight see super
     * @param newheight see super
     * @throws BuildingOnDomeException see super
     * @throws AtlasException when I'm doing something only Atlas can do, alas building a dome on any level
     * @throws InvalidBuildException see super
     */
    @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws BuildingOnDomeException, AtlasException, InvalidBuildException {
        try {
            super.isValidBuild(oldheight,newheight);
            if (newheight.equals(BuildingType.DOME) && !oldheight.equals(BuildingType.DOME)) throw new AtlasException();
        }
        catch(InvalidBuildException e){
            throw new InvalidBuildException();
        }
    }

}