package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Pan extends Builder{

    Pan(Cell position, Player player) {
        super(position, player);
    }

    /**
     * @param cell represents the cell on which the builder wants to build
     * @param newheight represents "new height", meaning the height which the builder wants to build on
     * @throws InvalidBuildException   see super
     * @throws AtlasException          when builder wants to build a dome above any level
     * @throws DemeterException        when builder wants to build twice a turn in two distinct cells
     * @throws HephaestusException     when builder wants to build two levels on the same cell
     * @throws PrometheusException     when builder does't go up, he can build before and after moving
     */
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }


    /**
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws MinotaurException
     * @throws ApolloException      builder can move in an cell occupied by a builder from another player and switch their positions.
     * @throws InvalidMoveException see super
     * @throws ArtemisException     when builder wants to move twice and he can't come back to the starting position
     * @throws PanException         when builder goes down by two or more levels --> players wins game
     */
    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
        if (this.getPosition().getHeight().compareTo(finalPoint.getHeight()) >= 2) {
            throw new PanException();
        }
    }
}
