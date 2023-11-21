package org.springframework.samples.petclinic.gameoflife;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class GameOfLifeTest {
    @Test
    public void showGameOfLife(){
        int initialPopulation=10;
        GameOfLife g=new GameOfLife(10, 10, initialPopulation);
        System.out.print(g.toString());
        assertEquals(initialPopulation, g.getPopulation());
    }


    @Test
    public void evolveGameOfLife(){
        
        int initialPopulation=5;
        GameOfLife g=new GameOfLife(10, 10, initialPopulation);
        System.out.println("----------- Starting Evolution (population:"+g.getPopulation()+") -------------");
        System.out.print(g.toString());        
        g.evolve();
        System.out.println("-------------------- population:"+ g.getPopulation()+"-------------------------");
        System.out.print(g.toString());        
        g.evolve();
        System.out.println("-------------------- population:"+ g.getPopulation()+"-------------------------");
        System.out.print(g.toString());        
        assertTrue(g.getPopulation()<=initialPopulation);
    }
}
