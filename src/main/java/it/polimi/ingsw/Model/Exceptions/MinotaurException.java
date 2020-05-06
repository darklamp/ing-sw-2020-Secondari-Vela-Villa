package it.polimi.ingsw.Model.Exceptions;

import it.polimi.ingsw.Model.Pair;

public class MinotaurException extends Exception {
    private Pair pair;
    private MinotaurException(){
        super();
    }
    public MinotaurException(Pair p){
        this();
        this.pair = p;
    }

    public Pair getPair() {
        return pair;
    }
}
