package it.polimi.ingsw;

import it.polimi.ingsw.Controller.MainController;
import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.GameTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestListener {
    @Test
    public void Test1(){
        // tutta sta roba andrebbe nella classe che inizializza il server
        GameTable observable = new GameTable();
        MainController observer = new MainController();
        observable.addPropertyChangeListener(observer);
        observable.setNews("Progetto IngSW");
        Assertions.assertEquals(observer.getNews(), "Progetto IngSW");
        Cell cell = new Cell(1,1);
        observable.setNews(cell);
        Assertions.assertEquals(observer.getCellNews(), cell);
    }
}
