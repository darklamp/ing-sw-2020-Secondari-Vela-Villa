package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public class Minotaur extends Builder{
    public Minotaur(Cell position) {
        super(position);
    }

    @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws InvalidBuildException, AtlasException {
        super.isValidBuild(oldheight, newheight);
        verifyBuild(oldheight,newheight);
    }

    /**
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws MinotaurException when the move is a minotaur-only move and it's valid
     * @throws ApolloException see super
     * @throws InvalidMoveException see super
     */
    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException {
            super.isValidMove(finalPoint);
            if (finalPoint.getBuilder() != null) { // there's a builder on the cell I'm trying to move to
                if (checkEmptyCellBehind(finalPoint)) throw new MinotaurException();
                else throw new InvalidMoveException();
            }
            else super.verifyMove(finalPoint);
    }

    /**
     * @param finalPoint cell on which builder wants to position
     * @return true if the cell behind the occupied one is empty and valid; else false
     */
    private boolean checkEmptyCellBehind(Cell finalPoint) {
            int diffX = finalPoint.getX() - this.getPosition().getX();
            int diffY = finalPoint.getY() - this.getPosition().getY();
            if(diffX == 1) {
                if (diffY == 1) {
                    return finalPoint.getX() != 4 && finalPoint.getY() != 4 && finalPoint.emptyCell(finalPoint.getX() + 1, finalPoint.getY() + 1); //OK
                }
                else if (diffY == -1) {
                    return finalPoint.getX() != 4 && finalPoint.getY() != 0 && finalPoint.emptyCell(finalPoint.getX() + 1, finalPoint.getY() - 1); //OK
                }
                else {
                    return finalPoint.getX() != 4 && finalPoint.emptyCell(finalPoint.getX() + 1, finalPoint.getY()); //OK
                }
            }
            else if (diffX == -1){
                if (diffY == 1) {
                    return finalPoint.getX() != 0 && finalPoint.getY() != 4 && finalPoint.emptyCell(finalPoint.getX() - 1, finalPoint.getY() + 1); //OK
                }
                else if (diffY == -1) {
                    return finalPoint.getX() != 0 && finalPoint.getY() != 0 && finalPoint.emptyCell(finalPoint.getX() - 1, finalPoint.getY() - 1); //OK
                }
                else {
                    return finalPoint.getX() != 0 && finalPoint.emptyCell(finalPoint.getX() - 1, finalPoint.getY()); //OK
                }
            }
            else {
                if (diffY == 1) {
                    return finalPoint.getY() != 4 && finalPoint.emptyCell(finalPoint.getX(), finalPoint.getY() + 1); //OK
                }
                else if (diffY == -1) {
                    return finalPoint.getY() != 0 && finalPoint.emptyCell(finalPoint.getX(), finalPoint.getY() - 1); //OK
                }
                return false;
            }
    }
}
