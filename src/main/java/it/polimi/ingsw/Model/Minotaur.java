package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import it.polimi.ingsw.Utility.Pair;

public class Minotaur extends Builder{
    Minotaur(Cell position, Player player) {
        super(position,player);
    }

    @Override
    public void isValidBuild(Cell cell, BuildingType newheight) throws InvalidBuildException, AtlasException, DemeterException, HephaestusException, PrometheusException {
        super.isValidBuild(cell, newheight);
        verifyBuild(cell,newheight);
    }

    /**
     * @param finalPoint represents the cell to which the builder wants to move
     * @throws MinotaurException    when the move is a minotaur-only move and it's valid
     * @throws ApolloException      see super
     * @throws InvalidMoveException see super
     */
    @Override
    void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PanException {
        super.isValidMove(finalPoint);
        if (finalPoint.getBuilder() != null) { // there's a builder on the cell I'm trying to move to
            try {
                if (checkEmptyCellBehind(finalPoint) && finalPoint.getBuilder().getPlayer() != this.getPlayer())
                    throw new MinotaurException(getCellBehind(finalPoint));
                else throw new InvalidMoveException();
            } catch (InvalidCoordinateException e) {
                throw new InvalidMoveException();
            }
        }
            else super.verifyMove(finalPoint);
    }

    /**
     * @param finalPoint cell on which builder wants to position
     * @return true if the cell behind the occupied one is empty and valid; else false
     */
    boolean checkEmptyCellBehind(Cell finalPoint) throws InvalidCoordinateException {
        /* Warn : X is the row, so it really is Y, and viceversa */
        int diffY = finalPoint.getRow() - this.getPosition().getRow();
        int diffX = finalPoint.getColumn() - this.getPosition().getColumn();
        if (diffY == 1) {
            if (diffX == 1) {
                return finalPoint.getRow() != 4 && finalPoint.getColumn() != 4 && finalPoint.movableCell(finalPoint.getRow() + 1, finalPoint.getColumn() + 1); //OK
            } else if (diffX == -1) {
                return finalPoint.getRow() != 4 && finalPoint.getColumn() != 0 && finalPoint.movableCell(finalPoint.getRow() + 1, finalPoint.getColumn() - 1); //OK
            }
                else {
                    return finalPoint.getRow() != 4 && finalPoint.movableCell(finalPoint.getRow() + 1, finalPoint.getColumn()); //OK
                }
            }
            else if (diffY == -1){
                if (diffX == 1) {
                    return finalPoint.getRow() != 0 && finalPoint.getColumn() != 4 && finalPoint.movableCell(finalPoint.getRow() - 1, finalPoint.getColumn() + 1); //OK
                }
                else if (diffX == -1) {
                    return finalPoint.getRow() != 0 && finalPoint.getColumn() != 0 && finalPoint.movableCell(finalPoint.getRow() - 1, finalPoint.getColumn() - 1); //OK
                }
                else {
                    return finalPoint.getRow() != 0 && finalPoint.movableCell(finalPoint.getRow() - 1, finalPoint.getColumn()); //OK
                }
            }
            else {
                if (diffX == 1) {
                    return finalPoint.getColumn() != 4 && finalPoint.movableCell(finalPoint.getRow(), finalPoint.getColumn() + 1); //OK
                }
                else if (diffX == -1) {
                    return finalPoint.getColumn() != 0 && finalPoint.movableCell(finalPoint.getRow(), finalPoint.getColumn() - 1); //OK
                }
                return false;
            }
    }

    /**
     * @param finalPoint is the cell where the Minotaur builder wants to move and it's already occupied.
     * @return the coordinates where the builder from finalPoint is forced to move.
     */
    Pair getCellBehind(Cell finalPoint) {
        int diffY = finalPoint.getRow() - this.getPosition().getRow();
        int diffX = finalPoint.getColumn() - this.getPosition().getColumn();
        if (diffY == 1) {
            if (diffX == 1) {
                return new Pair(finalPoint.getRow() + 1, finalPoint.getColumn() + 1);
            } else if (diffX == -1) {
                return new Pair(finalPoint.getRow() + 1, finalPoint.getColumn() - 1);
            } else {
                return new Pair(finalPoint.getRow() + 1, finalPoint.getColumn());
            }
        }
        else if (diffY == -1){
            if (diffX == 1) {
                return new Pair(finalPoint.getRow() - 1, finalPoint.getColumn() + 1);
            }
            else if (diffX == -1) {
                return new Pair(finalPoint.getRow() - 1, finalPoint.getColumn() - 1);
            }
            else {
                return new Pair(finalPoint.getRow() - 1, finalPoint.getColumn());
            }
        }
        else {
            if (diffX == 1) {
                return new Pair(finalPoint.getRow(), finalPoint.getColumn() + 1);
            }
            else if (diffX == -1) {
                return new Pair(finalPoint.getRow(), finalPoint.getColumn() - 1);
            }
        }
        return new Pair(0,0);
    }

}
