package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {
    
    @Query("SELECT card FROM Card card")
    public List<Card> findAll();

    @Query("SELECT card FROM Card card WHERE card.id = :cardId")
    public Card findById(int cardId);
}
