package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;

public enum BuildingType {
    NONE,BASE,MIDDLE,TOP,DOME;
    public BuildingType getNext() throws InvalidBuildException {
        switch (this){
            case NONE -> {
                return BASE;
            }
            case BASE -> {
                return MIDDLE;
            }
            case MIDDLE -> {
                return TOP;
            }
            case TOP -> {
                return DOME;
            }
            default -> throw new InvalidBuildException();
        }
    }
    public static BuildingType parse(int i)  {
        switch (i){
            case 1 -> {
                return MIDDLE;
            }
            case 2 -> {
                return TOP;
            }
            case 3 -> {
                return DOME;
            }
            default -> {
                return BASE;
            }
        }
    }
}
