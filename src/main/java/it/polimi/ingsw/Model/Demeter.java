package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Demeter extends Builder {
    private Cell previous;

    Demeter(Cell position, Player player) {
        super(position, player);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void isValidBuild(Cell cell, BuildingType newheight) throws AtlasException, DemeterException, HephaestusException, InvalidBuildException, PrometheusException {
        if (this.getPlayer().isFirstTime()) {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            this.getPlayer().setFirstTime(false);
            previous = cell;
            throw new DemeterException();
        } else {
            if (cell.equals(previous)) {
                throw new InvalidBuildException();
            } else {
                super.isValidBuild(cell, newheight);
                verifyBuild(cell, newheight);
            }
        }
    }


    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }
}