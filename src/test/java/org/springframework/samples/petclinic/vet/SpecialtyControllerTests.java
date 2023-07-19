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
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for the {@link VetController}
 */
@WebMvcTest(controllers = SpecialtyRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class SpecialtyControllerTests {

	private static final int TEST_SPECIALTY_ID = 1;
	private static final String BASE_URL = "/api/v1/vets/specialties";

	@SuppressWarnings("unused")
	@Autowired
	private SpecialtyRestController specialtyController;

	@MockBean
	private VetService vetService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Specialty surgery;

	@BeforeEach
	void setup() {
		surgery = new Specialty();
		surgery.setId(TEST_SPECIALTY_ID);
		surgery.setName("Surgery");
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {
		Specialty dogs = new Specialty();
		dogs.setId(2);
		dogs.setName("Dogs");

		Specialty vaccines = new Specialty();
		vaccines.setId(3);
		vaccines.setName("Vaccines");

		when(this.vetService.findSpecialties()).thenReturn(List.of(surgery, dogs, vaccines));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].name").value("Surgery"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("Dogs"))
				.andExpect(jsonPath("$[?(@.id == 3)].name").value("Vaccines"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnSpecialty() throws Exception {
		when(this.vetService.findSpecialtyById(TEST_SPECIALTY_ID)).thenReturn(surgery);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_SPECIALTY_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_SPECIALTY_ID))
				.andExpect(jsonPath("$.name").value(surgery.getName()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundSpecialty() throws Exception {
		when(this.vetService.findSpecialtyById(TEST_SPECIALTY_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_SPECIALTY_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldCreateSpecialty() throws Exception {
		Specialty sp = new Specialty();
		sp.setName("Prueba");

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sp))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateSpecialty() throws Exception {
		surgery.setName("UPDATED");

		when(this.vetService.findSpecialtyById(TEST_SPECIALTY_ID)).thenReturn(surgery);
		when(this.vetService.updateSpecialty(any(Specialty.class), any(Integer.class))).thenReturn(surgery);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_SPECIALTY_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(surgery))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("UPDATED"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateSpecialty() throws Exception {
		surgery.setName("UPDATED");

		when(this.vetService.findSpecialtyById(TEST_SPECIALTY_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.vetService.updateSpecialty(any(Specialty.class), any(Integer.class))).thenReturn(surgery);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_SPECIALTY_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(surgery))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	  void shouldDeleteSpecialty() throws Exception {
		when(this.vetService.findSpecialtyById(TEST_SPECIALTY_ID)).thenReturn(surgery);
		
	    doNothing().when(this.vetService).deleteVet(TEST_SPECIALTY_ID);
	    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_SPECIALTY_ID).with(csrf()))
	         .andExpect(status().isOk());
	  }

}
