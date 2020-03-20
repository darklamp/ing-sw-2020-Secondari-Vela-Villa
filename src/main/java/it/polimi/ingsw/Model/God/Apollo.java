package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public class Apollo extends GodDecorator {
    public Apollo(Builder builder) {
        super(builder);
    }


    /**
     * @param finalPoint indicates the point to which the builder is trying to move
     * @throws InvalidMoveException can be thrown by the super() method; see Builder.isValidMove()
     * @throws MoveOnOccupiedCellException gets thrown if the builder tries to move to a cell occupied by an enemy builder
     */
    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, MoveOnOccupiedCellException {
        try {
            super.isValidMove(finalPoint);
            if (finalPoint.getBuilder() != null) throw new MoveOnOccupiedCellException(); //TODO: verificare che l'altro builder sia nemico (creare attributo nel Builder che dice di chi è?)
        }
        catch(InvalidMoveException e){
            throw new InvalidMoveException();
        }
    }
}
