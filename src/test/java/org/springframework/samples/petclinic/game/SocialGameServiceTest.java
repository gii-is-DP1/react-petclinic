package org.springframework.samples.petclinic.game;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;

@DataJpaTest(includeFilters = @ComponentScan
.Filter(type = FilterType.ASSIGNABLE_TYPE,classes=GameService.class))
public class SocialGameServiceTest {

    @Autowired
    GameService gs;

    @Autowired
    OwnerRepository or;

    private Game createValidOngoingGame() {
        Game g=new Game();
        g.setId(22);
        g.setName("Mario Party!");
        g.setStart(LocalDateTime.of(2023, 11, 11, 11,11, 11));        
        return g;
    }

    @Test
    public void saveWithoutOngoingGames(){
        Game g=createValidOngoingGame();
        Optional<Owner> o=or.findById(1);
        g.setPlayers(Set.of(o.get()));
        try {
            gs.save(g);
        } catch (ConcurrentGameException e) {
            fail("No exception should be thrown: "+e.getMessage());
        }
    }

    @Test
    public void saveWithOngoingGames(){
        Game g=createValidOngoingGame();
        Optional<Owner> o=or.findById(2);
        g.setPlayers(Set.of(o.get()));
        assertThrows(ConcurrentGameException.class, ()-> gs.save(g));        
    }

    
}
