package org.springframework.samples.petclinic.game;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game,Integer> {
    List<Game> findAll();    
}
