package org.springframework.samples.learning;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.pet.PetRestController;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ApplicationContext.class )
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, classes={org.springframework.samples.petclinic.PetclinicApplication.class})
//@SpringBootTest(classes={com.lapots.game.monolith.web.GrandJourneyMonolithApplication.class})
@DirtiesContext
public class PetControllerSecurityTest {
    
	@Autowired
    private WebApplicationContext context;    
	
	private MockMvc mockMvc;

	private int TEST_OWNER_ID = 1;
    
	@BeforeEach
    public void setup() {
    mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(SecurityMockMvcConfigurers.springSecurity())
          .build();
    }
    
	@WithMockUser(username ="SomeRandomVet",authorities = {"vet"})
    @Test
    void unsuccessfullTestInitCreationForm() throws Exception {
    		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
	    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
	
	@WithMockUser(username ="SomeRandomOwner",authorities = {"owner"})
    @Test
    void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID)).andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm")).andExpect(model().attributeExists("pet"));
	}
}