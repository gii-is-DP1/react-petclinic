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
    public List<Game> getWaitingGames(){
        return gr.findByStart(null);
    }

    @Transactional
    public Game save(Game g) {
        gr.save(g);
        return g;
    }
    @Transactional(readOnly=true)
    public Optional<Game> getGameById(Integer id) {        
        return gr.findById(id);
    }
    @Transactional(readOnly=true)
    public Game getGameByCode(String code){
        List<Game> games=gr.findByCode(code);
        return games.isEmpty()?null:games.get(0);
    }

    
   

    @Transactional(readOnly=true)
    public List<Game> getGamesLike(String pattern){
        return gr.findByNameContains(pattern);
    }

    @Transactional()
    public void delete(Integer id) {
        gr.deleteById(id);
    }
    @Transactional(readOnly=true)
    public List<Game> getFinishedGames() {
        return gr.findByFinishIsNotNull();
    }
    @Transactional(readOnly=true)
    public List<Game> getOngoingGames() {
        return gr.findByFinishIsNullAndStartIsNotNull();
    }
}
