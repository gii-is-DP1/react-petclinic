package org.springframework.samples.petclinic.game;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.Owner;
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

    @Transactional(rollbackFor = {ConcurrentGameException.class})
    public Game save(Game g) throws ConcurrentGameException {
        List<Game> onGoingGames;
        if(g.isOngoing())
            for(Owner player:g.getPlayers()){
                onGoingGames=gr.findOngoinGamesByPlayer(player);
                if(!onGoingGames.isEmpty() && !g.getId().equals(onGoingGames.get(0).getId()))
                    throw new ConcurrentGameException(player,g,onGoingGames.get(0));
            }
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
