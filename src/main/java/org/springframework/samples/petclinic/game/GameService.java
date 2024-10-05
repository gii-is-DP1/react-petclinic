package org.springframework.samples.petclinic.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    protected GameRepository gameRepository;

    @Autowired
    GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    
}
