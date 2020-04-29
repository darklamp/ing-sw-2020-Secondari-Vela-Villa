package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.Ui;
import it.polimi.ingsw.Utility.Color;
import it.polimi.ingsw.Utility.Utils;
import it.polimi.ingsw.View.CellView;

import java.io.IOException;
import java.util.Scanner;

import static it.polimi.ingsw.Model.BuildingType.*;

public class CLI implements Ui {
    @Override
    public void process(String input) {
        System.out.println(input);
    }

    @Override
    public void waitForIP(Client client) throws IOException {
        /*Scanner stdin  = new Scanner(System.in);
        System.out.println("Insert IP address: ");
        String s = stdin.nextLine();
        while (!Utils.isValidIP(s)){
            System.out.println("Insert IP address: ");
            s = stdin.nextLine();
        }*/
        String s = "127.0.0.1";
        client.run(s);
    }

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
                    System.out.print("    A | B | C | D | E \n");
                    System.out.print("  ┌───┬───┬───┬───┬───┐\n0 │ ");
                }
                else if(c==0)
                {
                    System.out.print("\n─ ├───┼───┼───┼───┼───┤\n" + r + " │ ");
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
                    case -1 -> color = Color.ANSI_YELLOW;
                    case 0 -> color = Color.ANSI_BLUE;
                    case 1 -> color = Color.ANSI_RED;
                    case 2 -> color = Color.ANSI_GREEN;
                    default -> color = Color.ANSI_PURPLE;
                }
                dice.setColor(color);
                dice.Stamp();

                if (c == 4){
                    if (r == 0) System.out.print(Color.ANSI_BLUE + "  ██  PLAYER ONE    ██" + Color.RESET);
                    else if (r == 1) System.out.print(Color.ANSI_RED + "  ██  PLAYER TWO    ██" + Color.RESET);
                    else if (r == 2) System.out.print(Color.ANSI_GREEN + "  ██  PLAYER THREE  ██" + Color.RESET);
                    else if (r == 3) System.out.print(Color.ANSI_YELLOW+ "  ██  NO PLAYER     ██" + Color.RESET);
                }

            }
        }
        System.out.print("\n  └───┴───┴───┴───┴───┘\n");
    }

}

class Dice {
    private static final String[] DICE_FACES = {"#","\u2680", "\u2681", "\u2682","\u2683", "\u2684", "\u2685"};

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

    void Stamp(){System.out.print(this+" │ ");}
}
