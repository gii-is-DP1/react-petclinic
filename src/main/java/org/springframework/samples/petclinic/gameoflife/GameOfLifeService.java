package org.springframework.samples.petclinic.gameoflife;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class GameOfLifeService {
    @Value("${gameoflife.rows:20}")
    private int rows;
    @Value("${gameoflife.columns:20}")
    private int columns;
    @Value("${gameoflife.maxInitialLife:40}")
    private int maxInitialLife;

    private Map<String, GameOfLife> planets;


    public GameOfLifeService(){
        planets=new HashMap<>();
    }

    public GameOfLife getPlanet(String name){
        GameOfLife planet=null;
        if(planets.containsKey(name)){
            planet=planets.get(name);
            planet.evolve();            // WE EVOLVE THE STATE OF THE PLANET!
        }else {
            planet=new GameOfLife(rows, columns, (int)(Math.random()*maxInitialLife)+1);
            planets.put(name,planet);
        }
        return planet;
    }
}
