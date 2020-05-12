package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BuilderTest {
    private static Cell c1,c2,c3,c4,c5;
    private static Builder b1,b2;
    private static Player p1,p2;
    /**
     * Initiates various support variables.
     */
    @BeforeAll
    static void init() throws Exception {
        GameTable g = new GameTable(2);

        c1 = g.getCell(4, 4);
        c2 = g.getCell(4, 3);
        c3 = g.getCell(3, 4);
        c4 = g.getCell(3, 3);
        c5 = g.getCell(1, 1);
        p1 = new Player("Giggino", g, "MINOTAUR");
        p2 = new Player("Giggino2", g, "ATLAS");
        b1 = new Minotaur(c1, p1);
        b2 = new Demeter(c2, p2);
    }
    @Test
    void isValidBuild() throws Exception {
            c3.mustSetHeight(BuildingType.DOME);
            c4.mustSetHeight(BuildingType.TOP);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.isValidBuild(c3,BuildingType.DOME); //dove sta una dome non ci si può mai costruire altro
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.isValidBuild(c3,BuildingType.TOP); //dove sta una dome non ci si può mai costruire altro
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.isValidBuild(c4,BuildingType.MIDDLE); //nella cella c4 c'è una TOP quindi ci si può mettere solo una dome sopra,sennò parte eccezione
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.isValidBuild(c4,BuildingType.TOP); //nella cella c4 c'è una TOP quindi ci si può mettere solo una dome sopra,sennò parte eccezione
        });
    }
     //manca il test out of bound
    @Test
    void verifyBuild() {
        c3.mustSetHeight(BuildingType.DOME);
        c4.mustSetHeight(BuildingType.MIDDLE);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.verifyBuild(c3,BuildingType.DOME); //non si può costruire qualcosa di uguale o livello inferiore a un certo tipo di costruizione
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.verifyBuild(c3,BuildingType.TOP); //non si può costruire qualcosa di uguale o livello inferiore a un certo tipo di costruizione
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.verifyBuild(c4,BuildingType.DOME); //non si può costruire qualcosa di 2 o più livelli superiore
        });
    }


    @Test
    void isValidMove() {
        c4.mustSetHeight(BuildingType.DOME);
        c3.mustSetHeight(BuildingType.TOP);
        c2.mustSetHeight(BuildingType.BASE);
        c1.mustSetHeight(BuildingType.TOP);
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b1.isValidMove(c4); //non si può muovere su una cella dove ci sta posizionata una DOME
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b2.isValidMove(c4); //non si può muovere su una cella dove ci sta posizionata una DOME
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b2.isValidMove(c3); //non ci si può sostare di 2 livelli soperiori
        });
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b2.isValidMove(c5); //non è adiacente
        });

    }

    @Test
    void verifyMove() {
        Assertions.assertThrows(InvalidMoveException.class, () -> {
            b2.verifyMove(c1); //ci sta già un player p1
        });
    }

    @Test
    void swapPosition() {
        Cell temp1=b1.getPosition(); //mi salvo la posizione
        Cell temp2=b2.getPosition(); //mi salvo la posizione
        b1.swapPosition(b1,b2); //scambio posizioni builders
        Assertions.assertEquals(b1.getPosition(),temp2); //controlla che la nuova posizione sia uguale a quella di dove si trovava l'altro giocatore prima
        Assertions.assertEquals(b2.getPosition(),temp1); //controlla che la nuova posizione sia uguale a quella di dove si trovava l'altro giocatore prima
    }
}