package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Exceptions.*;
import org.graalvm.compiler.lir.gen.VerifyingMoveFactory;

public class Artemis extends Builder {
    public Artemis(Cell position, Player player) {
        super(position,player);
    }

    @Override
    public void isValidBuild(BuildingType oldheight, BuildingType newheight) throws InvalidBuildException, AtlasException {
        super.isValidBuild(oldheight, newheight);
        verifyBuild(oldheight,newheight);
    }

    @Override
    public void isValidMove(Cell finalPoint) throws MinotaurException, ApolloException, InvalidMoveException, ArtemisException,PrometeusException {
        super.isValidMove(finalPoint);
        if (finalPoint.getBuilder() != null && finalPoint!=this.getPosition() && (finalPoint.getHeight().compareTo(getPosition().getHeight()) < 3)) throw new ArtemisException();
        //TODO ma non so come,aggiungere condizione che presa una qualsiasi cella X appartenente a position.getNear ,finalPoint appartiene a X.getnear.
        else verifyMove(finalPoint);
    }

    /*
      public void MoveTwoTimes(Cell midPoint,Cell finalPoint){ //TODO->va nel controller
        getPlayer().MoveTo(midPoint);
        getPlayer().MoveTo(finalPoint);
      }
      */
}
