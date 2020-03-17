package it.polimi.ingsw.Model;

import java.util.Objects;

public class Cell {

    // NOTA: questo metodo va quasi bene ma va inserito in Builder, non qua, poichè può essere necessario override con Dei (in particolare Atlas)
    /*private void isValidBuild(BuildingType height) throws BuildingOnDomeException,BuildingTwoLevelsException,InvalidBuildException {

        if ((this.height).equals(BuildingType.DOME)) throw new BuildingOnDomeExcepion(); // verify that there is no dome on the cell
        else if ((this.height).compareTo(height) <= 0)  throw new InvalidBuildException(); // verify that I'm not building on the same level as already present or on a lower level
        else if ((this.height).equals(BuildingType.TOP) && !(height.equals(BuildingType.DOME))) throw new InvalidBuildException(); // if a top level is present, only a dome can be placed
        else if ((this.height).compareTo(height) == 2) throw new BuildingTwoLevelsException(); // building two levels
        else
    }*/

    private Builder builder;

    private BuildingType height = BuildingType.NONE;

    public BuildingType getHeight() {
        return height;
    }

    public void setHeight(BuildingType height) {
        try{
            builder.isValidBuild(height);
            this.height = height; //OK

        }
        catch(NullPointerException e) {

        }
        catch (BuildingOnDomeException e) {

        }
        catch (SameLevelBuildingException e) {

        }
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

}
