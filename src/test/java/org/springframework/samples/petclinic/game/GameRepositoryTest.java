package org.springframework.samples.petclinic.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    GameRepository gr;

    @Autowired
    OwnerRepository or;

    @Test
    public void findOngoingGamesByNullPlayer() {
        List<Game> actualResult = gr.findOngoinGamesByPlayer(null);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    public void findOngoingGamesByPlayerTestNoGames() {
        // There are 3 ways to get one empty list of results => Refactor as 3 different
        // tests!
        // This owner has games that have not started yet:
        Optional<Owner> o = or.findById(1);
        List<Game> actualResult = gr.findOngoinGamesByPlayer(o.get());
        assertTrue(actualResult.isEmpty());

        // This owner has no games:
        o = or.findById(5);
        actualResult = gr.findOngoinGamesByPlayer(o.get());
        assertTrue(actualResult.isEmpty());

        // This owner has games that are finished already:
        o = or.findById(3);
        actualResult = gr.findOngoinGamesByPlayer(o.get());
        assertTrue(actualResult.isEmpty());
    }

    // This owner has games that have not started yet (1)
    // This owner has no games (5)
    // This owner has games that are finished already (3)
    @ParameterizedTest
    @ValueSource(ints = { 1, 5, 3 })
    public void findOngoingGamesByPlayerTestNoGamesParameterized(int id) {

        Optional<Owner> o = or.findById(id);
        List<Game> actualResult = gr.findOngoinGamesByPlayer(o.get());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    public void findOngoingGamesByPlayerTestSingleGame(){
        Optional<Owner> o=or.findById(2);
        Optional<Game> g=gr.findById(2);
        List<Game> actualResult=gr.findOngoinGamesByPlayer(o.get());   
        assertEquals(actualResult.size(),1);
        assertTrue(actualResult.contains(g.get()));
    }

    @Test
    public void findOngoingGamesByPlayerMultipleGames(){
        Optional<Owner> o=or.findById(4);
        Optional<Game> g2=gr.findById(2);
        Optional<Game> g4=gr.findById(4);
        List<Game> actualResult=gr.findOngoinGamesByPlayer(o.get());   
        //assertEquals(1,actualResult.size());
        assertThat(actualResult.size()).isEqualTo(1);
        assertTrue(actualResult.contains(g2.get()));
        assertTrue(actualResult.contains(g4.get()));
    }

}
