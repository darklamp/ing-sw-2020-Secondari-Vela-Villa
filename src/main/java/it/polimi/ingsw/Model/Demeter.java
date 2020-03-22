package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Demeter extends Builder {
    private boolean firsttime = true;
    private Cell previous;

    public Demeter(Cell position, Player player) {
        super(position, player);
    }


  //  @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight, Cell cell) throws AtlasException, DemeterException, HephaestusException, InvalidBuildException {
        if (firsttime) {
            super.isValidBuild(oldheight, newheight);
            verifyBuild(oldheight, newheight);
            firsttime = false;
            previous = cell;
            throw new DemeterException();
        } else {
            super.isValidBuild(oldheight, newheight);
            verifyBuild(oldheight, newheight);
            if (cell.equals(previous)) {
                throw new InvalidBuildException()
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