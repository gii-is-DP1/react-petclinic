package org.springframework.samples.petclinic.game;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConcurrentGameException extends Exception {
    Owner player;
    Game gameToSave;
    Game alreadyOngoinGame;
    
    public ConcurrentGameException(Owner player, Game g1, Game g2) {
        super("Player with id: "+player.getId()+ "has an already ongoin game (with id "+g2.getId()+" thus we cannot same game: "+g1.toString());
        this.player=player;
        this.gameToSave=g1;
        this.alreadyOngoinGame=g2;   
    }

}
