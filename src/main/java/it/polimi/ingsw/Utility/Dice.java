package it.polimi.ingsw.Utility;

public class Dice {
    private staticfinalString[] DICE_FACES = {"\u26BE","\u2680", "\u2681", "\u2682","\u2683", "\u2684", "\u2685"};

    private Stringface;
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

    public Color GetColor(){
        returncolor;}

    public void SetColor(Color color){
        this.color= color;}

    @Override public StringtoString(){
        returnthis.color+ face + Color.RESET;}

    void Stamp(){System.out.print(this+"  ");}
}
