package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Model.BuildingType;
import it.polimi.ingsw.View.CellView;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CLITest {
    private static final CLI classecli = new CLI();
    private static final CellView [][] Matrix = new CellView[5][5];
    @Test
    void showTable() {
        Matrix[0][0]= new CellView(BuildingType.NONE,0,true);
        Matrix[0][1]= new CellView(BuildingType.MIDDLE,1,true);
        Matrix[0][2]= new CellView(BuildingType.TOP,2,true);
        Matrix[0][3]= new CellView(BuildingType.TOP,-1,true);
        Matrix[0][4]= new CellView(BuildingType.DOME,-1,true);
        Matrix[1][0]= new CellView(BuildingType.DOME,-1, false);
        Matrix[1][1]= new CellView(BuildingType.TOP,-1,false);
        Matrix[1][2]= new CellView(BuildingType.TOP,-1,false);
        Matrix[1][3]= new CellView(BuildingType.TOP,-1,false);
        Matrix[1][4]= new CellView(BuildingType.TOP,-1,false);
        Matrix[2][0]= new CellView(BuildingType.NONE,-1,true);
        Matrix[2][1]= new CellView(BuildingType.NONE,-1,false);
        Matrix[2][2]= new CellView(BuildingType.NONE,-1,true);
        Matrix[2][3]= new CellView(BuildingType.NONE,-1,false);
        Matrix[2][4]= new CellView(BuildingType.MIDDLE,-1,false);
        Matrix[3][0]= new CellView(BuildingType.MIDDLE,-1,true);
        Matrix[3][1]= new CellView(BuildingType.MIDDLE,-1,false);
        Matrix[3][2]= new CellView(BuildingType.MIDDLE,-1,true);
        Matrix[3][3]= new CellView(BuildingType.MIDDLE,-1,false);
        Matrix[3][4]= new CellView(BuildingType.MIDDLE,-1,false);
        Matrix[4][0] = new CellView(BuildingType.MIDDLE, -1, false);
        Matrix[4][1] = new CellView(BuildingType.MIDDLE, -1, true);
        Matrix[4][2] = new CellView(BuildingType.MIDDLE, -1, false);
        Matrix[4][3] = new CellView(BuildingType.MIDDLE, -1, false);
        Matrix[4][4] = new CellView(BuildingType.MIDDLE, -1, false);
        classecli.showTable(Matrix);


    }

    @Test
    void process() throws Exception {
        Method a = CLI.class.getDeclaredMethod("process", String.class);
        a.setAccessible(true);
        CLI cli = new CLI();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        a.invoke(cli, "[asdasda//111\\\41q1q7INITs]");
        System.out.flush();
        System.setOut(old);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            assertEquals(baos.toString(), "[asdasda//111\\\41q1q7INITs]\r\n");
        } else assertEquals(baos.toString(), "[asdasda//111\\\41q1q7INITs]\n");

        /* test INIT string */
        Field b = Client.class.getDeclaredField("playerIndex");
        b.setAccessible(true);
        Field c = Client.class.getDeclaredField("playersNumber");
        c.setAccessible(true);
        System.setOut(ps);
        a.invoke(cli, "[INIT]@@@2@@@3");
        System.out.flush();
        System.setOut(old);
        assertEquals((short) 2, b.get(cli));
        assertEquals((short) 3, c.get(cli));

    }

    @Test
    void processTurnChange() throws Exception {
        Method m = CLI.class.getDeclaredMethod("processTurnChange", ClientState.class);
        ClientState c = ClientState.WAIT;
        CLI cli = new CLI();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        m.invoke(cli, ClientState.WAIT);
        System.out.flush();
        String endLineChar = "\n";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            endLineChar = "\r\n";
        }
        assertEquals(baos.toString(), "Waiting for turn..." + endLineChar);
        baos.reset();
        m.invoke(cli, ClientState.MOVE);
        System.out.flush();
        assertEquals(baos.toString(), "It's your turn! Please choose a cell to move to and which builder to use (x,y,b): " + endLineChar);
        baos.reset();
        m.invoke(cli, ClientState.MOVEORBUILD);
        System.out.flush();
        assertEquals(baos.toString(), "It's your turn, You can choose whether to move(m) or build(b). Please choose: " + endLineChar);
        baos.reset();
        m.invoke(cli, ClientState.BUILD);
        System.out.flush();
        assertEquals(baos.toString(), "Please choose a cell to build on and which builder to use (x,y,b): " + endLineChar);
        baos.reset();
        m.invoke(cli, ClientState.WIN);
        System.out.flush();
        assertEquals(baos.toString(), "Hurray! You won the game!" + endLineChar);
        baos.reset();
        m.invoke(cli, ClientState.LOSE);
        System.out.flush();
        assertEquals(baos.toString(), "Looks like you've lost the game." + endLineChar);
        baos.reset();
        m.invoke(cli, ClientState.BUILDORPASS);
        System.out.flush();
        assertEquals(baos.toString(), "You can choose whether to build(b) or pass(p). Please choose: " + endLineChar);

    }
}