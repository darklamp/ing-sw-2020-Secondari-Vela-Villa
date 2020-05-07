package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;

public class Hephaestus extends Builder{
    private boolean firsttime=true; //mi dice se è la prima o la seconda costruzione che fa
    private Cell previous;  //ci salvo la cella dove costruisco la prima volta
    Hephaestus(Cell position, Player player) {
        super(position,player);
    }



    @Override
    protected void isValidBuild(Cell cell, BuildingType newheight) throws AtlasException, HephaestusException, InvalidBuildException, DemeterException, PrometheusException {
        if(firsttime) {
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            firsttime=false;
            previous=cell; //mi salvo il valore della cella su cui voglio costruire
            throw new HephaestusException();  //lancio l'eccezione che dice al controller di far costruire di nuovo
        }
        else{
            super.isValidBuild(cell, newheight);
            verifyBuild(cell, newheight);
            if(newheight.equals(BuildingType.DOME)){throw new InvalidBuildException();} //non posso costruire una cupola come seconda costruzione ci va il new o no?
            else if(!cell.equals(previous)){throw new InvalidBuildException();}// non posso costruire su una cella diversa da quella precedente
            else {
                firsttime = true;  //così quando verrà richiamato il metodo isvalidbuild entrerò nel ramo if
            }
        }
    }
    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException, PrometheusException, PanException {
        super.isValidMove(finalPoint);
        verifyMove(finalPoint);
    }



}
