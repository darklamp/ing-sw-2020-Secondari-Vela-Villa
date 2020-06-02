package it.polimi.ingsw.Model;

import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.Exceptions.*;

public class Prometheus extends Builder {
    Prometheus(Cell position, Player player) {
        super(position, player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell, newheight);
        if (this.getPlayer().getState() == ClientState.MOVEORBUILD) throw new PrometheusException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        if (this.getPlayer().getState() == ClientState.MOVE) {
            if (getPosition().getHeight().compareTo(finalPoint.getHeight()) < 0) throw new InvalidMoveException();
        }
    }

    @Override
    public ClientState getFirstState(){
        return ClientState.MOVEORBUILD;
    }

}


