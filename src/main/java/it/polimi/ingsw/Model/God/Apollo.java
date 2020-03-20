package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.Builder;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.*;

public class Apollo extends Builder {
    public Apollo(Cell position) {
        super(position);
    }


    /**
     * @param finalPoint indicates the point to which the builder is trying to move
     * @throws InvalidMoveException can be thrown by the super() method; see Builder.isValidMove()
     * @throws ApolloException gets thrown if the builder tries to move to a cell occupied by an enemy builder
     */
    @Override
    public void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException {
        try {
            super.isValidMove(finalPoint);
            if (finalPoint.getBuilder() != null) throw new ApolloException(); //TODO: verificare che l'altro builder sia nemico (creare attributo nel Builder che dice di chi è?)
        }
        catch(InvalidMoveException | MinotaurException e){
            throw new InvalidMoveException();
        }
    }
}
