package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Artemis extends Builder {
    Artemis(Cell position, Player player) {
        super(position, player);
    }

    /**
     * Cell on which Artemis came from, and to which she cannot move again until next turn.
     */
    private Cell previous;

    /**
     * {@inheritDoc}
     */
    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PanException {
        boolean firstTime = this.getPlayer().isFirstTime();
        if (firstTime) {
            super.isValidMove(finalPoint);
            verifyMove(finalPoint);
            this.getPlayer().setFirstTime(false);
            previous = this.getPosition();
            throw new ArtemisException();
        } else {
            if (finalPoint.equals(previous)) {
                throw new InvalidMoveException();
            }// cannot move on same cell as before
            else {
                super.isValidMove(finalPoint);
                verifyMove(finalPoint);
               // firstTime = true; //così quando verrà richiamato il metodo isValidMove entrerò nel ramo if
            }
        }
    }

}
