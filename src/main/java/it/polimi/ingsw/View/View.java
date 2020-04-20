package it.polimi.ingsw.View;


import it.polimi.ingsw.Model.Player;

public abstract class View extends Observable<PlayerMove> implements Observer<MoveMessage> {

    private Player player;

    protected View(Player player){
        this.player = player;
    }

    protected Player getPlayer(){
        return player;
    }

    protected abstract void showMessage(Object message);

    void handleMove(int row, int column) {
        System.out.println(row + " " + column);
        notify(new PlayerMove(player, row, column, this));
    }

    public void reportError(String message){
        showMessage(message);
    }

}
