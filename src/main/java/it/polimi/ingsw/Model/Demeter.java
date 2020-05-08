package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Demeter extends Builder {
    private boolean firstTime;
    private Cell previous;

    Demeter(Cell position, Player player) {
        super(position, player);
    }


    /**
     * @param cell      represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @throws AtlasException n/a
     * @throws DemeterException when I just built on a valid cell, to let the controller know I can build again
     * @throws HephaestusException n/a
     * @throws InvalidBuildException when thrown by super method or when trying to build using Demeter's power on the same cell as before (see previous)
     */
    @Override
    protected void isValidBuild(Cell cell, BuildingType newheight) throws AtlasException, DemeterException, HephaestusException, InvalidBuildException, PrometheusException {
        firstTime = this.getPlayer().isFirstTime();
        if (firstTime) {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            this.getPlayer().setFirstTime(false);
            previous = cell;
            throw new DemeterException();
        } else {
            if (cell.equals(previous)) {
                throw new InvalidBuildException();
            }
            else {
                super.isValidBuild(cell, newheight);
                verifyBuild(cell, newheight);
             //   this.getPlayer().setFirstTime(true); //così quando verrà richiamato il metodo isvalidbuild entrerò nel ramo if
            }
        }
    }


    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PrometheusException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }
}