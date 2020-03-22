package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.AtlasException;
import it.polimi.ingsw.Model.Exceptions.HephaestusException;
import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;

public class Hephaestus extends Builder{
    private boolean firsttime=true;
    private Cell previous;
    public Hephaestus(Cell position, Player player) {
        super(position,player);
    }


    @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws AtlasException, HephaestusException, InvalidBuildException {
        if(firsttime) {
            super.isValidBuild(oldheight, newheight);
            verifyBuild(oldheight, newheight);
            firsttime=false;
            previous=this.getPosition();
            throw new HephaestusException();
        }
        else{
            super.isValidBuild(oldheight, newheight);
            verifyBuild(oldheight, newheight);

        }
    }



}
