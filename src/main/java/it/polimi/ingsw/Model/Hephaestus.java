package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Hephaestus extends Builder{
    private Cell previous;  //ci salvo la cella dove costruisco la prima volta
    Hephaestus(Cell position, Player player) {
        super(position,player);
    }



    @Override
    protected void isValidBuild(Cell cell, BuildingType newheight) throws AtlasException, HephaestusException, InvalidBuildException, DemeterException, PrometheusException {
        if (this.getPlayer().isFirstTime()) {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            if (!(newheight == BuildingType.DOME)) {
                this.getPlayer().setFirstTime(false);
                previous = cell; //mi salvo il valore della cella su cui voglio costruire
                throw new HephaestusException();  //lancio l'eccezione che dice al controller di far costruire di nuovo
            }
        } else {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            if (!cell.equals(previous)) {
                throw new InvalidBuildException();
            }
        }
    }

    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }



}
