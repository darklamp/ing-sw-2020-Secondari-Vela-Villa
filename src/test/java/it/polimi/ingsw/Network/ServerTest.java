package it.polimi.ingsw.Network;

import it.polimi.ingsw.Model.Exceptions.NickAlreadyTakenException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerTest {
    Server s;

    {
        try {
            s = new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks correct deregistration of SocketClientConnection
     */
    @Test
    void deregisterConnectionTest() throws Exception {
        Field b = Server.class.getDeclaredField("gameList");
        b.setAccessible(true);
        SocketClientConnection c1 = new SocketClientConnection(new Socket(), s);
        SocketClientConnection c2 = new SocketClientConnection(new Socket(), s);
        SocketClientConnection c3 = new SocketClientConnection(new Socket(), s);
        //noinspection unchecked
        Map<Integer, ArrayList<SocketClientConnection>> asd = (Map<Integer, ArrayList<SocketClientConnection>>) b.get(null);
        ArrayList<SocketClientConnection> list0 = new ArrayList<>();
        ArrayList<SocketClientConnection> list1 = new ArrayList<>();
        list0.add(c1);
        list1.add(c2);
        list1.add(c3);
        asd.put(0, list0);
        asd.put(1, list1);
        int prevSize = asd.size();
        int prevSizeList0 = 1, prevSizeList1 = 2;
        Server.deregisterConnection(c2);
        assertTrue(asd.size() == prevSize && asd.get(0).get(0) == c1 && asd.get(0).size() == prevSizeList0 && asd.get(1).get(0) == c3 && asd.get(1).size() == prevSizeList1 - 1);
    }

    /**
     * Checks that lobby method throws NickAlreadyTakenException as expected.
     */
    @Test
    void lobbyNoDoubleNickname() throws Exception {
        Field a = Server.class.getDeclaredField("waitingConnection");
        a.setAccessible(true);
        SocketClientConnection c1 = new SocketClientConnection(new Socket(), s);
        //noinspection unchecked
        Map<String, SocketClientConnection> asd = (Map<String, SocketClientConnection>) a.get(null);
        asd.put("giggi", c1);
       /* Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(a, a.getModifiers() & ~Modifier.FINAL);*/
        assertThrows(NickAlreadyTakenException.class, () -> s.lobby(c1, "giggi"));
    }
}