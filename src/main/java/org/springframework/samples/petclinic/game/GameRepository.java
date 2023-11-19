package org.springframework.samples.petclinic.game;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game,Integer> {
    List<Game> findAll();    

    List<Game> findByNameContains(String pattern);
    List<Game> findByStart(LocalDateTime start);
    List<Game> findByFinish(LocalDateTime finish);
    List<Game> findByCode(String code);

    List<Game> findByFinishIsNotNull();

    List<Game> findByFinishIsNullAndStartIsNotNull();

    @Query("SELECT g FROM Game g WHERE g.start<>null AND g.finish=null AND :player MEMBER OF g.players")
    List<Game> findOngoinGamesByPlayer(@Param("player") Owner player);
}
