package org.springframework.samples.petclinic.game;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {
    
    GameRepository gr;

    @Autowired
    public GameService(GameRepository gr){
        this.gr=gr;
    }

    @Transactional(readOnly=true)
    public List<Game> getAllGames(){
        return gr.findAll();
    }
    
    @Transactional(readOnly=true)
    public List<Game> getGamesByName(String namepatterm){
        return gr.findAll();
    }

    @Transactional(readOnly=true)
    public List<Game> getRunningGames(){
        return gr.findAll();
    }

    @Transactional
    public Game save(Game g) {
        gr.save(g);
        return g;
    }

    public Optional<Game> getGameById(Integer id) {        
        return gr.findById(id);
    }

    public void delete(Integer id) {
        gr.deleteById(id);
    }

}
