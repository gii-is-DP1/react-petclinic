package org.springframework.samples.petclinic.owner;

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
import org.springframework.samples.petclinic.clinic.Clinic;
import org.springframework.samples.petclinic.clinic.PricingPlan;
import org.springframework.samples.petclinic.clinicowner.ClinicOwner;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for {@link OwnerRestController}
 *
 */

@WebMvcTest(value = { OwnerRestController.class,
		OwnerPlanController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
class OwnerRestControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final String BASE_URL = "/api/v1/owners";

	@SuppressWarnings("unused")
	@Autowired
	private OwnerRestController ownerController;

	@SuppressWarnings("unused")
	@Autowired
	private OwnerPlanController ownerPlanController;

	@MockBean
	private OwnerService ownerService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Owner george;
	private Owner sara;
	private Owner juan;
	private User user;
	private User userClinicOwner;
	private ClinicOwner clinicOwner;
	private Clinic clinic;

	@BeforeEach
	void setup() {
		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Sevilla");
		george.setTelephone("608555102");

		sara = new Owner();
		sara.setId(2);
		sara.setFirstName("Sara");
		sara.setLastName("Franklin");
		sara.setAddress("110 W. Liberty St.");
		sara.setCity("Sevilla");
		sara.setTelephone("608555102");

		juan = new Owner();
		juan.setId(3);
		juan.setFirstName("Juan");
		juan.setLastName("Franklin");
		juan.setAddress("110 W. Liberty St.");
		juan.setCity("Sevilla");
		juan.setTelephone("608555102");

		Authorities ownerAuth = new Authorities();
		ownerAuth.setId(1);
		ownerAuth.setAuthority("OWNER");

		Authorities clinicOwnerAuth = new Authorities();
		ownerAuth.setId(2);
		ownerAuth.setAuthority("CLINIC_OWNER");

		user = new User();
		user.setId(1);
		user.setUsername("user");
		user.setPassword("password");
		user.setAuthority(ownerAuth);

		userClinicOwner = new User();
		userClinicOwner.setId(1);
		userClinicOwner.setUsername("clinicOwner");
		userClinicOwner.setPassword("clinic_owner");
		userClinicOwner.setAuthority(clinicOwnerAuth);

		clinicOwner = new ClinicOwner();
		clinicOwner.setId(1);
		clinicOwner.setFirstName("John");
		clinicOwner.setLastName("Doe");
		clinicOwner.setUser(userClinicOwner);

		clinic = new Clinic();
		clinic.setId(1);
		clinic.setName("Clinic");
		clinic.setAddress("Address");
		clinic.setTelephone("123456789");
		clinic.setPlan(PricingPlan.BASIC);
		clinic.setClinicOwner(clinicOwner);

		george.setClinic(clinic);
		sara.setClinic(clinic);
		juan.setClinic(clinic);
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {
		when(this.ownerService.findAll()).thenReturn(List.of(george, sara, juan));
		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].firstName").value("George"))
				.andExpect(jsonPath("$[?(@.id == 2)].firstName").value("Sara"))
				.andExpect(jsonPath("$[?(@.id == 3)].firstName").value("Juan"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnOwner() throws Exception {
		when(this.ownerService.findOwnerById(TEST_OWNER_ID)).thenReturn(george);
		mockMvc.perform(get(BASE_URL +"/{id}", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_OWNER_ID))
				.andExpect(jsonPath("$.firstName").value(george.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(george.getLastName()))
				.andExpect(jsonPath("$.clinic.plan").value(george.getClinic().getPlan().toString()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundOwner() throws Exception {
		when(this.ownerService.findOwnerById(TEST_OWNER_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_OWNER_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldCreateOwner() throws Exception {
		Owner owner = new Owner();
		owner.setFirstName("Prueba");
		owner.setLastName("Prueba");
		owner.setCity("Llerena");
		owner.setAddress("Calle Reina,3");
		//owner.setPlan(PricingPlan.BASIC);
		owner.setTelephone("999999999");
		owner.setUser(user);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(owner))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateOwner() throws Exception {
		george.setFirstName("UPDATED");
		george.setLastName("UPDATED");

		when(this.ownerService.findOwnerById(TEST_OWNER_ID)).thenReturn(george);
		when(this.ownerService.updateOwner(any(Owner.class), any(Integer.class))).thenReturn(george);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_OWNER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(george))).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(george.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(george.getLastName()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateOwner() throws Exception {
		george.setFirstName("UPDATED");
		george.setLastName("UPDATED");

		when(this.ownerService.findOwnerById(TEST_OWNER_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.ownerService.updateOwner(any(Owner.class), any(Integer.class))).thenReturn(george);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_OWNER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(george))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	  void shouldDeleteOwner() throws Exception {
		when(this.ownerService.findOwnerById(TEST_OWNER_ID)).thenReturn(george);
		
	    doNothing().when(this.ownerService).deleteOwner(TEST_OWNER_ID);
	    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_OWNER_ID).with(csrf()))
	         .andExpect(status().isOk());
	  }

	@Test
	@WithMockUser("owner")
	void shouldReturnPlan() throws Exception {
		when(this.userService.findCurrentUser()).thenReturn(user);
		when(this.userService.findOwnerByUser(any(Integer.class))).thenReturn(george);
		
		mockMvc.perform(get("/api/v1/plan")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_OWNER_ID))
				.andExpect(jsonPath("$.firstName").value(george.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(george.getLastName()))
				.andExpect(jsonPath("$.clinic.plan").value(george.getClinic().getPlan().toString()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void shouldReturnStats() throws Exception {
		when(this.ownerService.getOwnersStats()).thenReturn(new HashMap<>());

		mockMvc.perform(get(BASE_URL + "/stats")).andExpect(status().isOk());
	}

}
