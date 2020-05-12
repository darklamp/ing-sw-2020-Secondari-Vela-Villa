package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.InvalidBuildException;
import it.polimi.ingsw.Network.SocketClientConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class  PlayerTest {

    @Test
    void initBuilderList() throws Exception {
        GameTable g = GameTable.getDebugInstance(2); g.setDebugInstance();
        Player p1 = new Player("Giggino", g, "MINOTAUR");
        Player p2 = new Player("Giggino2", g, "ATLAS");
        Cell c1 = g.getCell(4,3);
        Cell c2 = g.getCell(4,4);
        Cell c3 = g.getCell(4,1);
        Cell c4 = g.getCell(4,2);
        Builder b1 = new Minotaur(c1,p1);
        p2.initBuilderList(c2);
        p2.initBuilderList(c3);

        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p1.initBuilderList(c2);  //parte l'eccezione perchè la cella c2 è occupata dal player p2
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p1.initBuilderList(c1);  //parte l'eccezione perchè la cella c1 è occupata dal suo primo builder b1
        });
        Assertions.assertThrows(InvalidBuildException.class, () -> {
            p2.initBuilderList(c4); // anche se è libera la cella parte l'eccezione perchè p2 ha già due builder posizionati
        });

    }

    @Test
    void isValidGodTest() throws Exception {
        Method m = Player.class.getDeclaredMethod("isValidGod", String.class, GameTable.class);
        m.setAccessible(true);
        GameTable g = new GameTable(2);
        SocketClientConnection c = new SocketClientConnection(new Socket(), null);
        Player p = new Player("gigi", g, c);
        Field f = GameTable.class.getDeclaredField("godChoices");
        f.setAccessible(true);
        ArrayList<String> gods = new ArrayList<>();
        gods.add("APOLLO");
        f.set(g, gods);
        assertFalse((Boolean) m.invoke(p, "OLEG", g));
        assertTrue((Boolean) m.invoke(p, "APOLLO", g));


    }

}