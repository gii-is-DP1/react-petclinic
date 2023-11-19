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

    private Game createValidOngoingGame(int id) {
        Game g=new Game();
        g.setId(id);
        g.setName("Mario Party!");
        g.setStart(LocalDateTime.of(2023, 11, 11, 11,11, 11));        
        return g;
    }

    @Test
    public void saveWithoutOngoingGames(){
        Game g22=createValidOngoingGame(22);
        Game g23=createValidOngoingGame(23);
        Owner o=new Owner();
        g22.setPlayers(Set.of(o));
        when(gr.findOngoinGamesByPlayer(any(Owner.class))).thenReturn(List.of());
        try {
            gs.save(g22);
        } catch (ConcurrentGameException e) {
            fail("No exception should be thrown: "+e.getMessage());
        }
    }

    @Test
    public void saveWithOngoingGames(){
        Game g22=createValidOngoingGame(22);
        Game g23=createValidOngoingGame(23);
        Owner o=new Owner();
        g22.setPlayers(Set.of(o));
        when(gr.findOngoinGamesByPlayer(any(Owner.class))).thenReturn(List.of(g23));
        assertThrows(ConcurrentGameException.class, ()-> gs.save(g22));        
    }

}