package org.springframework.samples.petclinic.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(value = { GameRestController.class}, 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
public class IsolatedControllerTest {

    @MockBean
    GameService gs;

    @Autowired
    MockMvc mvc;

    static final String BASE_URL="/api/v1/game";


    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void unfeasibleGameCreationTest() throws JsonProcessingException, Exception{

        
        Game g=new Game(); // This game is invalid since it has no name:
        
        ObjectMapper objectMapper=new ObjectMapper();
        
        reset(gs);

        mvc.perform(post(BASE_URL)
                        .with(csrf()).
                        contentType(MediaType.APPLICATION_JSON)
				        .content(objectMapper.writeValueAsString(g)))
                    .andExpect(status().isBadRequest());
        // Comprobamos que no se ha grabado el juego en la BD:
        verify(gs,never()).save(any(Game.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void feasibleGameCreationTest() throws JsonProcessingException, Exception{
        Game g=creatValidGame(); // This game is invalid since it has no name:
        ObjectMapper objectMapper=new ObjectMapper();
        reset(gs);
        when(gs.save(any(Game.class))).thenReturn(g);


        mvc.perform(post(BASE_URL)
                        .with(csrf()).
                        contentType(MediaType.APPLICATION_JSON)
				        .content(objectMapper.writeValueAsString(g)))
                    .andExpect(status().isCreated());
        // Comprobamos que se ha intentado grabar el juego en la bd:
        verify(gs,times(1)).save(any(Game.class));
    }

    private Game creatValidGame() {
        Game g=new Game();
        g.setName("Crazy smash bros session");
        return g;
    }

}
