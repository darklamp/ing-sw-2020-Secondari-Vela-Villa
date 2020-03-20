package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.ApolloException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.MinotaurException;

public class Minotaur extends Builder {
    public Minotaur(Cell position) {
        super(position);
    }

    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException {
        try{
            super.isValidMove(finalPoint);
            if (finalPoint.getBuilder() != null) { // there's a builder on the cell I'm trying to move to
                if (checkEmptyCellBehind(finalPoint)) throw new MinotaurException();
                else throw new InvalidMoveException();
            }
        }
        catch(InvalidMoveException e){
//TODO
        }
    }
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
