package org.springframework.samples.petclinic.vet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for the {@link VetController}
 */
@WebMvcTest(controllers = VetRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class VetControllerTests {

	private static final int TEST_VET_ID = 1;
	private static final String BASE_URL = "/api/v1/vets";

	@SuppressWarnings("unused")
	@Autowired
	private VetRestController vetController;

	@MockBean
	private VetService vetService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Vet george;
	private User user;

	@BeforeEach
	void setup() {
		george = new Vet();
		george.setId(TEST_VET_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setCity("Sevilla");

		Authorities vetAuth = new Authorities();
		vetAuth.setId(1);
		vetAuth.setAuthority("VET");

		user = new User();
		user.setId(1);
		user.setUsername("user");
		user.setPassword("password");
		user.setAuthority(vetAuth);

//		adminJwt = getToken("admin", "ADMIN");
//		ownerJwt = getToken("owner", "OWNER");

	}

	@Test
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {
		Vet sara = new Vet();
		sara.setId(2);
		sara.setFirstName("Sara");

		Vet juan = new Vet();
		juan.setId(3);
		juan.setFirstName("Juan");

		when(this.vetService.findAll()).thenReturn(List.of(george, sara, juan));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].firstName").value("George"))
				.andExpect(jsonPath("$[?(@.id == 2)].firstName").value("Sara"))
				.andExpect(jsonPath("$[?(@.id == 3)].firstName").value("Juan"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnVet() throws Exception {
		when(this.vetService.findVetById(TEST_VET_ID)).thenReturn(george);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_VET_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_VET_ID))
				.andExpect(jsonPath("$.firstName").value(george.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(george.getLastName()))
				.andExpect(jsonPath("$.city").value(george.getCity()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundVet() throws Exception {
		when(this.vetService.findVetById(TEST_VET_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_VET_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldCreateVet() throws Exception {
		Vet vet = new Vet();
		vet.setFirstName("Prueba");
		vet.setLastName("Prueba");
		vet.setCity("Llerena");
		vet.setUser(user);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(vet))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateVet() throws Exception {
		george.setFirstName("UPDATED");
		george.setLastName("CHANGED");

		when(this.vetService.findVetById(TEST_VET_ID)).thenReturn(george);
		when(this.vetService.updateVet(any(Vet.class), any(Integer.class))).thenReturn(george);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_VET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(george))).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("UPDATED")).andExpect(jsonPath("$.lastName").value("CHANGED"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateVet() throws Exception {
		george.setFirstName("UPDATED");
		george.setLastName("UPDATED");

		when(this.vetService.findVetById(TEST_VET_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.vetService.updateVet(any(Vet.class), any(Integer.class))).thenReturn(george);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_VET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(george))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	  void shouldDeleteVet() throws Exception {
		when(this.vetService.findVetById(TEST_VET_ID)).thenReturn(george);
		
	    doNothing().when(this.vetService).deleteVet(TEST_VET_ID);
	    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_VET_ID).with(csrf()))
	         .andExpect(status().isOk());
	  }

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void shouldReturnStats() throws Exception {
		when(this.vetService.getVetsStats()).thenReturn(new HashMap<>());

		mockMvc.perform(get(BASE_URL + "/stats")).andExpect(status().isOk());
	}

}
