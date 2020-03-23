package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Demeter extends Builder {
    private boolean firsttime = true;
    private Cell previous;

    public Demeter(Cell position, Player player) {
        super(position, player);
    }


    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws AtlasException, DemeterException, HephaestusException, InvalidBuildException {
        if (firsttime) {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            firsttime = false;
            previous = cell;
            throw new DemeterException();
        } else {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            if (cell.equals(previous)) {
                throw new InvalidBuildException();
            }// non posso costruire su una cella uguale a quella precedente
            else {
                firsttime = true; //così quando verrà richiamato il metodo isvalidbuild entrerò nel ramo if
            }
        }
    }


    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PrometeusException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }
}