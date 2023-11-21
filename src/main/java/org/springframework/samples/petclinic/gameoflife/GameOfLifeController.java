package org.springframework.samples.petclinic.gameoflife;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;

@RestController
public class GameOfLifeController {
        
    @Autowired
    GameOfLifeService universe;

    @GetMapping("/api/v1/universe/planet/{name}")
    public GameOfLife getPlanet(@PathVariable("name") String name){
        return universe.getPlanet(name);
    }
}
