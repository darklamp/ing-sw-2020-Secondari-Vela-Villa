package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Model.Exceptions.InvalidCoordinateException;
import it.polimi.ingsw.Model.Exceptions.InvalidMoveException;
import it.polimi.ingsw.Model.Exceptions.WinnerException;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class BuilderTest {
    private static Cell c1,c2,c3,c4,c5;
    private static Builder b1,b2;
    private static Player p1, p2;
    private static GameTable g;
    /**
     * Initiates various support variables.
     */
    @BeforeAll
    static void init() throws Exception {
        g = new GameTable(2);

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
    void setPositionTest() throws Exception {
        GameTable g = new GameTable(2);
        Player p1 = new Player("Giggino", g, "APOLLO");
        Player p2 = new Player("Pippo", g, "APOLLO");
        Cell c1 = g.getCell(4, 3);
        Cell c2 = g.getCell(4, 4);
        Cell c3 = g.getCell(0, 3);
        Cell c4 = g.getCell(0, 4);
        Cell c5 = g.getCell(0, 3);
        Builder b1 = new Apollo(c1, p1);
        Builder b2 = new Apollo(c2, p2);
        Builder b3 = new Apollo(c3, p1);
        Builder b4 = new Apollo(c4, p2);
        Builder b5 = new Artemis(c5, p1);
        b1.setPosition(c2);
        Assertions.assertEquals(b1, c2.getBuilder());
        Assertions.assertEquals(b2, c1.getBuilder());
        c3.mustSetHeight(BuildingType.MIDDLE);
        c4.mustSetHeight(BuildingType.TOP);
        Assertions.assertThrows(WinnerException.class, () -> {
            b3.setPosition(c4);
        });
        c4.setBuilder(null);
        Assertions.assertThrows(WinnerException.class, () -> {
            b5.setPosition(c4);
        });
    }

    @Test
    void isValidBuild() throws Exception {
        c3.mustSetHeight(BuildingType.DOME);
        c4.mustSetHeight(BuildingType.TOP);
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.isValidBuild(c3, BuildingType.DOME); //dove sta una dome non ci si può mai costruire altro
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            b2.isValidBuild(c3, BuildingType.TOP); //dove sta una dome non ci si può mai costruire altro
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
    void isValidMove() throws IllegalAccessException, NoSuchFieldException, InvalidCoordinateException {
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
        Assertions.assertThrows(InvalidMoveException.class, () -> b2.isValidMove(null));
        c1.setBuilder(null);
        c1.mustSetHeight(BuildingType.MIDDLE);
        c2.setBuilder(null);
        c2.mustSetHeight(BuildingType.TOP);
        Builder b4 = new Artemis(c1, p1);
        Field f = GameTable.class.getDeclaredField("athenaMove");
        f.setAccessible(true);
        f.set(g, true);
        Assertions.assertThrows(InvalidMoveException.class, () -> b4.isValidMove(c2));
        Cell c5 = g.getCell(3, 3);
        Cell c6 = g.getCell(2, 3);

        c5.mustSetHeight(BuildingType.NONE);
        c6.mustSetHeight(BuildingType.BASE);
        c5.setBuilder(null);
        c6.setBuilder(null);


        Builder b5 = new Athena(c5, p1);
        Assertions.assertDoesNotThrow(() -> b5.isValidMove(c6));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(null));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(new Cell(-1, 4, g)));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(new Cell(4, -1, g)));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(new Cell(5, 0, g)));
        Assertions.assertThrows(InvalidMoveException.class, () -> b1.isValidMove(new Cell(0, 5, g)));


    }

    @Test
    void constructorTest1() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Apollo(g.getCell(1, 1), null);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Apollo(null, p1);
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
        Cell temp1 = b1.getPosition(); //mi salvo la posizione
        Cell temp2 = b2.getPosition(); //mi salvo la posizione
        b1.swapPosition(b1, b2); //scambio posizioni builders
        Assertions.assertEquals(b1.getPosition(), temp2); //controlla che la nuova posizione sia uguale a quella di dove si trovava l'altro giocatore prima
        Assertions.assertEquals(b2.getPosition(), temp1); //controlla che la nuova posizione sia uguale a quella di dove si trovava l'altro giocatore prima
    }

    @Test
    void isFirst() throws Exception {
        GameTable g = new GameTable(2);
        Builder b = new Prometheus(new Cell(2, 2, g), new Player("g", g, (SocketClientConnection) null));
        Field c = Builder.class.getDeclaredField("first");
        c.setAccessible(true);
        c.set(b, true);
        Assertions.assertTrue((Boolean) c.get(b));
    }

}