package org.springframework.samples.petclinic.game;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SocialGameControllerTest  {

     @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    GameService gs;

    static final String BASE_URL="/api/v1/game";
    

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    private Game creatValidGame() {
        Game g=new Game();
        g.setName("Crazy smash bros session");
        return g;
    }

    @Test
    @Transactional
    @WithMockUser(username = "Shigeru", authorities = {"ADMIN"})
    public void feasibleGameCreationTest() throws JsonProcessingException, Exception{
        Game g=creatValidGame(); // This game is invalid since it has no name:        
        ObjectMapper objectMapper=new ObjectMapper();

        mockMvc.perform(post(BASE_URL)
                        .with(csrf()).
                        contentType(MediaType.APPLICATION_JSON)
				        .content(objectMapper.writeValueAsString(g)))
                    .andExpect(status().isCreated());
        // Comprobamos que se ha grabado efectivamente el juego en la bd:
        //assertNotNull(gs.getGamesByName(g.getName()));
        assertThat(gs.getGamesByName(g.getName())).isNotEmpty();
    }

    /* 
    private void assertNotNull(List<Game> gamesByName) {
    }
    */
}
