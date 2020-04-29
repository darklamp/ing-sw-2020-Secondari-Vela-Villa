package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.View.CellView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CLITest {

    private static final BuildingType NONE ;
    private static final BuildingType MIDDLE = ;
    private static final BuildingType TOP = ;
    private static final BuildingType DOME = ;

    @Test
    void showTable() {
        CellView[][] Matrix = new CellView[5][5];
        Matrix[0][0]= new CellView(NONE,0);
        Matrix[0][1]= new CellView(MIDDLE,1);
        Matrix[0][2]= new CellView(TOP,2);
        Matrix[0][3]= new CellView(DOME,-1);
        Matrix[0][4]= new CellView(DOME,-1);
        Matrix[1][0]= new CellView(DOME,-1);
        Matrix[1][1]= new CellView(DOME,-1);
        Matrix[1][2]= new CellView(DOME,-1);
        Matrix[1][3]= new CellView(DOME,-1);
        Matrix[1][4]= new CellView(DOME,-1);
        Matrix[2][0]= new CellView(DOME,-1);
        Matrix[2][1]= new CellView(DOME,-1);
        Matrix[2][2]= new CellView(DOME,-1);
        Matrix[2][3]= new CellView(DOME,-1);
        Matrix[2][4]= new CellView(DOME,-1);
        Matrix[3][0]= new CellView(DOME,-1);
        Matrix[3][1]= new CellView(DOME,-1);
        Matrix[3][2]= new CellView(DOME,-1);
        Matrix[3][3]= new CellView(DOME,-1);
        Matrix[3][4]= new CellView(DOME,-1);
        Matrix[4][0]= new CellView(DOME,-1);
        Matrix[4][1]= new CellView(DOME,-1);
        Matrix[4][2]= new CellView(DOME,-1);
        Matrix[4][3]= new CellView(DOME,-1);
        Matrix[4][4]= new CellView(DOME,-1);
        CLI classecli = new CLI();
        classecli.showTable(Matrix);




    }
}