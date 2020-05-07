package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Artemis extends Builder {
    Artemis(Cell position, Player player) {
        super(position,player);
    }

    private Cell previous;

    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }

    /**
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws MinotaurException n/a
     * @throws ApolloException n/a
     * @throws InvalidMoveException when super method throws it or when I'm trying to move on the same cell as before
     * @throws ArtemisException when moving for the first time on a valid cell
     * @throws PrometheusException n/a
     */
    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PrometheusException, PanException {
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
