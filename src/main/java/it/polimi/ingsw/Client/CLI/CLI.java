package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ClientState;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.Network.ServerMessage;
import it.polimi.ingsw.Utility.Color;
import it.polimi.ingsw.View.CellView;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class CLI implements Ui {

    private int playerIndex, playersNumber;

    @Override
    public String nextLine(Scanner in) {
        return in.nextLine();
    }

    @Override
    public void process(String input) {
        if (input.contains("[INIT]")) { /* if the string contains this prefix, it's an initialization string and it must be treated as such */
            String[] inputs = input.split("@@@");
            playerIndex = Integer.parseInt(inputs[1]);
            playersNumber = Integer.parseInt(inputs[2]);
        } else if (input.contains("[CHOICE]")) {
            String[] inputs = input.split("@@@");
            if (inputs[1].equals("GODS")) {
                playersNumber = Integer.parseInt(inputs[2]);
                AtomicInteger counter = new AtomicInteger(0);
                Client.completeGodList.forEach(name -> System.out.println(counter.getAndIncrement() + ") " + name));
                System.out.println(ServerMessage.nextChoice);
            } else if (inputs[1].equals("POS")) {
            } else {
                System.out.println(ServerMessage.nextChoice);
                for (int i = 1; i < inputs.length; i++) {
                    System.out.println(inputs[i] + ") " + Client.completeGodList.get(Integer.parseInt(inputs[i])));
                }
            }
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

                if (c == 4){
                    if (r == 0) System.out.print(Color.RESET + Color.BOLD + (playerIndex == 0 ? "  YOU" : (playersNumber == 3 ? "  P3 " : "  P2 ")) + Color.ANSI_BLUE + "    █ 1 █ " + Color.ANSI_YELLOW +"█ 2 █" + Color.RESET);
                    else if (r == 1) System.out.print(Color.RESET + Color.BOLD + (playerIndex == 1 ? "  YOU" : "  P1 ") + Color.ANSI_RED + "    █ 1 █ " + Color.ANSI_CYAN +"█ 2 █" + Color.RESET);
                    else if (r == 2 && playersNumber == 3) System.out.print(Color.RESET + Color.BOLD + (playerIndex == 2 ? "  YOU" : "  P2 ") + Color.ANSI_GREEN + "  █ 1 █ " + Color.ANSI_PURPLE +"█ 2 █" + Color.RESET);
                    else if (r == 3) System.out.print(Color.RESET + Color.BOLD + Color.ANSI_WHITE+ "  ███  NO PLAYER     ███" + Color.RESET);
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
        switch (newState){
            case WAIT -> s = "Waiting for turn...";
            case MOVEORBUILD -> s = "It's your turn, You can choose whether to move(m) or build(b). Please choose: ";
            case MOVE -> s = "It's your turn! Please choose a cell to move to and which builder to use (x,y,b): ";
            case BUILD -> s = "Please choose a cell to build on and which builder to use (x,y,b): ";
            case WIN -> s = "Hurray! You won the game!";
            case LOSE -> s = "Looks like you've lost the game.";
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

    public Color getColor(){
        return color;
    }

    public void setColor(Color color){
        this.color= color;
    }

    @Override public String toString(){
        return this.color + face + Color.RESET;
    }

    void Stamp(){System.out.print(this.toString() + Color.ANSI_CYAN + " │ ");}
}
