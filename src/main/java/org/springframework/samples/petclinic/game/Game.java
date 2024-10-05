package org.springframework.samples.petclinic.game;

import java.util.List;

import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.scoreboard.Scoreboard;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Game extends BaseEntity {
    
    @NotNull
    @Max(4)
    private Integer numberOfPlayers;

    @NotNull
    private Integer numberOfRounds;

    @OneToMany(mappedBy = "game")
    private List<Scoreboard> scoreboards;

    @OneToMany
    private List<Card> cards;

    private Integer turn;
}