package org.springframework.samples.petclinic.game;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;

@ExtendWith(MockitoExtension.class)
public class IsolatedGameServiceTest {

    GameService gs;
    @Mock
    GameRepository gr;
    @Mock
    OwnerRepository or;

    @BeforeEach
    public void setup(){
        gs=new GameService(gr);
    }

    @Test
    public void saveWithoutOngoingGames(){
        Game g=createValidOngoingGame();
        Game g2=createValidOngoingGame();
        g2.setId(g2.getId()+1);
        Owner o=new Owner();
        g.setPlayers(Set.of(o));
        when(gr.findOngoinGamesByPlayer(any(Owner.class))).thenReturn(List.of());
        try {
            gs.save(g);
        } catch (ConcurrentGameException e) {
            fail("No exception should be thrown: "+e.getMessage());
        }
    }


    @Test
    public void saveWithOngoingGames(){
        Game g=createValidOngoingGame();
        Game g2=createValidOngoingGame();
        g2.setId(g2.getId()+1);
        Owner o=new Owner();
        g.setPlayers(Set.of(o));
        when(gr.findOngoinGamesByPlayer(any(Owner.class))).thenReturn(List.of(g2));
        assertThrows(ConcurrentGameException.class, ()-> gs.save(g));        
    }



    private Game createValidOngoingGame() {
        Game g=new Game();
        g.setId(22);
        g.setName("Mario Party!");
        g.setStart(LocalDateTime.of(2023, 11, 11, 11,11, 11));        
        return g;
    }
}
