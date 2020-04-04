package it.polimi.ingsw.Model;

import org.junit.jupiter.api.Test;

public class Prova {
    @Test
    void provaTest(){
        System.out.println(ProvaEnum.UNO.compareTo(ProvaEnum.DUE));
    }
}
enum ProvaEnum{
    UNO,DUE;
}
