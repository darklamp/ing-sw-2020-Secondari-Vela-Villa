package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.Network.Messages.ErrorMessage;
import it.polimi.ingsw.Network.Messages.InitMessage;
import it.polimi.ingsw.Network.Messages.Message;
import it.polimi.ingsw.Network.Messages.ServerMessage;
import it.polimi.ingsw.Utility.Color;
import it.polimi.ingsw.View.CellView;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static it.polimi.ingsw.Client.Client.*;

public class CLI implements Ui {

    /**
     * @see Ui
     */
    @Override
    public String nextLine(Scanner in) {
        return in.nextLine();
    }

    /**
     * @see Ui
     */
    @Override
    synchronized public void process(Message input) {
        if (input instanceof InitMessage) {
            InitMessage m = (InitMessage) input;
            setPlayerIndex(m.getPlayerIndex());
            setPlayersNumber(m.getSize());
            Client.setGod(m.getGod(Client.getPlayerIndex()));
            Client.setGods(m.getGod(0), m.getGod(1), m.getGod(2));
            System.out.println("Your game's number is: " + m.getGameIndex() + ". Keep it in case server goes down.");

        } else if (input instanceof ErrorMessage) {
            System.err.println(((ErrorMessage) input).getContent());
            if (input.equals(ServerMessage.gameLost) || input.equals(ServerMessage.abortMessage) || input.equals(ServerMessage.serverDown))
                System.exit(0);
        }
    }

    /**
     * @see Ui
     */
    @Override
    public void process(String input) {
        String[] inputs;
        if (input != null) {
            inputs = input.split("@@@");
        } else return;
        if (input.contains("[CHOICE]")) {
            if (inputs[1].equals("GODS")) {
                setPlayersNumber((short) Integer.parseInt(inputs[2]));
                AtomicInteger counter = new AtomicInteger(1);
                Client.completeGodList.forEach(name -> System.out.println(counter.getAndIncrement() + ") " + name));
                System.out.println(ServerMessage.nextChoice);
            } else if (inputs[1].equals("POS")) {
            } else {
                System.out.println(ServerMessage.nextChoice);
                for (int i = 1; i < inputs.length; i++) {
                    int x = Integer.parseInt(inputs[i]);
                    System.out.println(/*Integer.parseInt(inputs[i])+1*/x + 1 + ") " + Client.completeGodList.get(/*Integer.parseInt(inputs[i])+1*/x));
                }
            }
        } else if (input.contains(ServerMessage.connClosed)) {
            if (inputs.length == 1) {
                System.err.println(Color.RESET + ServerMessage.connClosed);
                System.exit(0);
            } else {
                int pIndex = Integer.parseInt(inputs[1]);
                if (pIndex == Client.getPlayerIndex()) {
                    process(ServerMessage.gameLost);
                } else {
                    if (pIndex == 1) {
                        setGods(Client.getGods()[0], Client.getGods()[2], -1);
                        Client.setPlayerIndex(Client.getPlayerIndex() == 0 ? 0 : 1);
                    } else if (pIndex == 0) {
                        setGods(Client.getGods()[1], Client.getGods()[2], -1);
                        Client.setPlayerIndex(Client.getPlayerIndex() - 1);
                    } else {
                        setGods(Client.getGods()[0], Client.getGods()[1], -1);
                    }
                    process("Player " + pIndex + " has lost!");
                    Client.setPlayersNumber(Client.getPlayersNumber() - 1);
                }
            }
        } else if (input.contains(ServerMessage.lastGod)) {
            Client.setGod(Integer.parseInt(inputs[1]));
            process("You're left with " + Client.completeGodList.get(Client.getGod()));
        } else System.out.println(input);
    }

    /**
     * Flow: Get input ip -> verify it -> if valid, try to connect, else ask again
     */
    @Override
    public void waitForIP(Client client) {
        client.run();
    }

    /**
     * @see Ui
     */
    @Override
    public void showTable(CellView[][] table){
        int r,c;
        Dice dice = new Dice();
        for(r=0;r<5;r++)
        {
            for(c=0;c<5;c++)
            {
                if(r==0 && c==0)
                {
                    System.out.print(Color.ANSI_WHITE + "    0 | 1 | 2 | 3 | 4 \n");
                    System.out.print(Color.ANSI_CYAN +  "  ┌───┬───┬───┬───┬───┐\n" + Color.ANSI_WHITE + "0" + Color.ANSI_CYAN + " │ ");
                }
                else if(c==0)
                {
                    System.out.print(Color.ANSI_CYAN + "\n─ ├───┼───┼───┼───┼───┤\n" + Color.ANSI_WHITE + r + Color.ANSI_CYAN + " │ ");
                }
                switch (table[r][c].getHeight()){
                    case NONE -> dice.Zero();
                    case BASE -> dice.One();
                    case MIDDLE -> dice.Two();
                    case TOP -> dice.Three();
                    case DOME -> dice.Four();
                }
                Color color;
                switch (table[r][c].getPlayer()){
                    case 0 -> color = table[r][c].isFirst() ? Color.ANSI_BLUE : Color.ANSI_YELLOW;
                    case 1 -> color = table[r][c].isFirst() ? Color.ANSI_RED : Color.ANSI_CYAN;
                    case 2 -> color = table[r][c].isFirst() ? Color.ANSI_GREEN : Color.ANSI_PURPLE;
                    default -> color = Color.ANSI_WHITE;
                }
                dice.setColor(color);
                dice.Stamp();

                if (c == 4) {
                    if (r == 0)
                        System.out.print(Color.RESET + Color.BOLD + (Client.getPlayerIndex() == 0 ? "  YOU" : (Client.getPlayersNumber() == 3 ? "  P3 " : "  P2 ")) + Color.ANSI_BLUE + "    █ 1 █ " + Color.ANSI_YELLOW + "█ 2 █" + Color.RESET);
                    else if (r == 1)
                        System.out.print(Color.RESET + Color.BOLD + (Client.getPlayerIndex() == 1 ? "  YOU" : "  P1 ") + Color.ANSI_RED + "    █ 1 █ " + Color.ANSI_CYAN + "█ 2 █" + Color.RESET);
                    else if (r == 2 && Client.getPlayersNumber() == 3)
                        System.out.print(Color.RESET + Color.BOLD + (Client.getPlayerIndex() == 2 ? "  YOU  " : "  P2   ") + Color.ANSI_GREEN + "  █ 1 █ " + Color.ANSI_PURPLE + "█ 2 █" + Color.RESET);
                    else if (r == 3)
                        System.out.print(Color.RESET + Color.BOLD + Color.ANSI_WHITE + "  ███  NO PLAYER     ███" + Color.RESET);
                }

            }
        }
        System.out.print("\n  └───┴───┴───┴───┴───┘\n");
    }

    /**
     * @see Ui
     */
    public void processTurnChange(ClientState newState){
        String s = "";
        switch (newState) {
            case WAIT -> s = "Waiting for turn...";
            case MOVEORBUILD -> s = "It's your turn, You can choose whether to move(m) or build(b). Please choose: ";
            case MOVE -> s = "It's your turn! Please choose a cell to move to and which builder to use (x,y,b): ";
            case BUILD -> s = "Please choose a cell to build on and which builder to use (x,y,b): ";
            case WIN -> {
                s = "Hurray! You won the game!";
                System.exit(0);
            }
            case LOSE -> {
                s = "Looks like you've lost the game.";
                System.exit(0);
            }
            case BUILDORPASS -> s = "You can choose whether to build(b) or pass(p). Please choose: ";
        }
        System.out.println(s);
    }

}

/**
 * Basic class for printing constructions levels
 */
class Dice {
    private static final String[] DICE_FACES = {"#","1", "2", "3","4", "\u2684", "\u2685"};

    private String face;
    private Color color;

    public void Zero(){
        this.face= DICE_FACES[0];}

    public void One(){
        this.face= DICE_FACES[1];}

    public void Two(){
        this.face= DICE_FACES[2];}

    public void Three(){
        this.face= DICE_FACES[3];}

    public void Four(){
        this.face= DICE_FACES[4];}

    public void setColor(Color color){
        this.color= color;
    }

    @Override public String toString(){
        return this.color + face + Color.RESET;
    }

    void Stamp(){System.out.print(this.toString() + Color.ANSI_CYAN + " │ ");}
}
