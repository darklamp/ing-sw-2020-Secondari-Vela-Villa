package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

import static it.polimi.ingsw.Model.Cell.movableCell;

public class Minotaur extends Builder{
    Minotaur(Cell position, Player player) {
        super(position,player);
    }

    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }

    /**
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws MinotaurException when the move is a minotaur-only move and it's valid
     * @throws ApolloException see super
     * @throws InvalidMoveException see super
     */
    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, PrometeusException, ArtemisException, PanException {
            super.isValidMove(finalPoint);
            if (finalPoint.getBuilder() != null) { // there's a builder on the cell I'm trying to move to
                try{
                    if (checkEmptyCellBehind(finalPoint) && finalPoint.getBuilder().getPlayer() != this.getPlayer()) throw new MinotaurException();
                    else throw new InvalidMoveException();
                }
                catch (InvalidCoordinateException e){
                    throw new InvalidMoveException();
                }
            }
            else super.verifyMove(finalPoint);
    }

    /**
     * @param finalPoint cell on which builder wants to position
     * @return true if the cell behind the occupied one is empty and valid; else false
     */
    private boolean checkEmptyCellBehind(Cell finalPoint) throws InvalidCoordinateException {
            int diffX = finalPoint.getX() - this.getPosition().getX();
            int diffY = finalPoint.getY() - this.getPosition().getY();
            if(diffX == 1) {
                if (diffY == 1) {
                    return finalPoint.getX() != 4 && finalPoint.getY() != 4 && movableCell(finalPoint.getX() + 1, finalPoint.getY() + 1); //OK
                }
                else if (diffY == -1) {
                    return finalPoint.getX() != 4 && finalPoint.getY() != 0 && movableCell(finalPoint.getX() + 1, finalPoint.getY() - 1); //OK
                }
                else {
                    return finalPoint.getX() != 4 && movableCell(finalPoint.getX() + 1, finalPoint.getY()); //OK
                }
            }
            else if (diffX == -1){
                if (diffY == 1) {
                    return finalPoint.getX() != 0 && finalPoint.getY() != 4 && movableCell(finalPoint.getX() - 1, finalPoint.getY() + 1); //OK
                }
                else if (diffY == -1) {
                    return finalPoint.getX() != 0 && finalPoint.getY() != 0 && movableCell(finalPoint.getX() - 1, finalPoint.getY() - 1); //OK
                }
                else {
                    return finalPoint.getX() != 0 && movableCell(finalPoint.getX() - 1, finalPoint.getY()); //OK
                }
            }
            else {
                if (diffY == 1) {
                    return finalPoint.getY() != 4 && movableCell(finalPoint.getX(), finalPoint.getY() + 1); //OK
                }
                else if (diffY == -1) {
                    return finalPoint.getY() != 0 && movableCell(finalPoint.getX(), finalPoint.getY() - 1); //OK
                }
                return false;
            }
    }

}
