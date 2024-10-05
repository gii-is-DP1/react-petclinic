package org.springframework.samples.petclinic.card;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
    
    protected CardRepository cardRepository;

    @Autowired
    CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> findAllCards() {
        return cardRepository.findAll();
    }

    public Card findCardById(int cardId) {
        return cardRepository.findById(cardId);
    }

    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    public List<Card> createAllCards(){

        List<Card> cartas= new ArrayList<Card>();
        
        for(int i=0; i<36; i++){
            Card carta = new Card();
            int id = i+1;
            //ARREGLAME AWELO
            carta.setDesign("../../carta"+id+".png");
            cartas.add(carta);
        }

        return cartas;
    }
}
