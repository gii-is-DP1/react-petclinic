package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {
    
    List<Card> findAll();

    @Query("SELECT c FROM Card c WHERE c.id = :id")
    Card findById(int id);
}
