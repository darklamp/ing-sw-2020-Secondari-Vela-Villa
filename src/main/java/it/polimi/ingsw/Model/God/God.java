package it.polimi.ingsw.Model.God;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Exceptions.ApolloException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.MinotaurException;

public interface God {
    void isValidBuild(BuildingType oldheight, BuildingType newheight) throws Exception;
    void isValidMove(Cell finalPoint) throws InvalidMoveException, ApolloException, MinotaurException;
}
