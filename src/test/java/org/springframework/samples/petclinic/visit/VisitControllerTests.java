package org.springframework.samples.petclinic.visit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.LimitReachedException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.exceptions.ResourceNotOwnedException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerService;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetService;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for {@link VisitRestController}
 *
 * 
 */
@WebMvcTest(controllers = VisitRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class VisitControllerTests {
	private static final int TEST_PET_ID = 1;
	private static final Integer TEST_OWNER_ID = 1;
	private static final int TEST_TYPE_ID = 1;
	private static final int TEST_USER_ID = 1;
	private static final int TEST_VISIT_ID = 1;
	private static final int TEST_CLINIC_ID = 1;
	private static final int TEST_CLINIC_OWNER_ID = 1;
	private static final int TEST_CLINIC_OWNER_USER_ID = 1;
	private static final String BASE_URL = "/api/v1/pets/" + TEST_PET_ID + "/visits";
	private static final String VISITS_URL = "/api/v1/visits";

	@SuppressWarnings("unused")
	@Autowired
	private VisitRestController visitController;

	@MockBean
	private VisitService visitService;

	@MockBean
	private PetService petService;

	@MockBean
	private OwnerService ownerService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Owner george;
	private Vet vet;
	private Pet simba;
	private User user, logged;
	private PetType lion;
	private Visit visit;
	private Clinic clinic;
	private ClinicOwner clinicOwner;
	private User clinicOwnerUser;

	@BeforeEach
	void setup() {

		Authorities clinicOwnerAuth = new Authorities();
		clinicOwnerAuth.setId(1);
		clinicOwnerAuth.setAuthority("CLINIC_OWNER");

		clinicOwnerUser = new User();
		clinicOwnerUser.setId(TEST_CLINIC_OWNER_USER_ID);
		clinicOwnerUser.setUsername("clinicOwnerTest");
		clinicOwnerUser.setPassword("clinicOwnerTest");
		clinicOwnerUser.setAuthority(clinicOwnerAuth);
		clinicOwner = new ClinicOwner();
		clinic = new Clinic();
		clinicOwner.setId(TEST_CLINIC_OWNER_ID);
		clinicOwner.setFirstName("Test Name");
		clinicOwner.setLastName("Test Surname");
		clinicOwner.setUser(clinicOwnerUser);
		clinic.setId(TEST_CLINIC_ID);
		clinic.setName("Clinic Test");
		clinic.setAddress("Test Address");
		clinic.setPlan(PricingPlan.BASIC);
		clinic.setTelephone("123456789");
		clinic.setClinicOwner(clinicOwner);

		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Sevilla");
		george.setTelephone("608555102");
		george.setClinic(clinic);

		Authorities ownerAuth = new Authorities();
		ownerAuth.setId(2);
		ownerAuth.setAuthority("OWNER");

		user = new User();
		user.setId(TEST_USER_ID);
		user.setUsername("user");
		user.setPassword("password");
		user.setAuthority(ownerAuth);

		george.setUser(user);

		lion = new PetType();
		lion.setId(TEST_TYPE_ID);
		lion.setName("lion");

		simba = new Pet();
		simba.setId(TEST_PET_ID);
		simba.setName("Simba");
		simba.setOwner(george);
		simba.setType(lion);
		simba.setBirthDate(LocalDate.of(2000, 01, 01));

		vet = new Vet();
		vet.setId(1);
		vet.setFirstName("Super");
		vet.setLastName("Vet");

		visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		visit.setDescription("Checking Simba's teeth.");
		visit.setPet(simba);
		visit.setVet(vet);

		when(this.userService.findCurrentUser()).thenReturn(getUserFromDetails(
				(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));

	}

	private User getUserFromDetails(UserDetails details) {
		logged = new User();
		logged.setUsername(details.getUsername());
		logged.setPassword(details.getPassword());
		Authorities aux = new Authorities();
		for (GrantedAuthority auth : details.getAuthorities()) {
			aux.setAuthority(auth.getAuthority());
		}
		logged.setAuthority(aux);
		return logged;
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminOrVetShouldFindAllFromPet() throws Exception {
		Visit stomach = new Visit();
		stomach.setId(2);
		stomach.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setDescription("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setVet(vet);

		Visit leg = new Visit();
		leg.setId(3);
		leg.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		leg.setDescription("Checking Simba's leg.");
		leg.setPet(simba);
		leg.setVet(vet);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.visitService.findVisitsByPetId(TEST_PET_ID)).thenReturn(List.of(visit, stomach, leg));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].description").value("Checking Simba's teeth."))
				.andExpect(jsonPath("$[?(@.id == 2)].description").value("Checking Simba's stomach."))
				.andExpect(jsonPath("$[?(@.id == 3)].description").value("Checking Simba's leg."));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindAllFromOwnedPet() throws Exception {
		logged.setId(TEST_USER_ID);

		Visit stomach = new Visit();
		stomach.setId(2);
		stomach.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setDescription("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setVet(vet);

		Visit leg = new Visit();
		leg.setId(3);
		leg.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		leg.setDescription("Checking Simba's leg.");
		leg.setPet(simba);
		leg.setVet(vet);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.visitService.findVisitsByPetId(TEST_PET_ID)).thenReturn(List.of(visit, stomach, leg));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].description").value("Checking Simba's teeth."))
				.andExpect(jsonPath("$[?(@.id == 2)].description").value("Checking Simba's stomach."))
				.andExpect(jsonPath("$[?(@.id == 3)].description").value("Checking Simba's leg."));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindAllFromOthersPet() throws Exception {
		logged.setId(2);

		Visit stomach = new Visit();
		stomach.setId(2);
		stomach.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setDescription("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setVet(vet);

		Visit leg = new Visit();
		leg.setId(3);
		leg.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		leg.setDescription("Checking Simba's leg.");
		leg.setPet(simba);
		leg.setVet(vet);

		Owner other = new Owner();
		other.setId(2);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);
		when(this.visitService.findVisitsByPetId(TEST_PET_ID)).thenReturn(List.of(visit, stomach, leg));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminOrVetShouldFindVisit() throws Exception {
		logged.setId(1);

		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_VISIT_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_VISIT_ID))
				.andExpect(jsonPath("$.description").value(visit.getDescription()))
				.andExpect(jsonPath("$.pet.name").value(simba.getName()))
				.andExpect(jsonPath("$.vet.firstName").value(vet.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindVisitFromOwnedPet() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_PET_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_PET_ID)).andExpect(jsonPath("$.id").value(TEST_VISIT_ID))
				.andExpect(jsonPath("$.description").value(visit.getDescription()))
				.andExpect(jsonPath("$.pet.name").value(simba.getName()))
				.andExpect(jsonPath("$.vet.firstName").value(vet.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindVisitFromOthersPet() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_PET_ID)).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundVisit() throws Exception {
		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_VISIT_ID)).andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldCreateVisit() throws Exception {
		logged.setId(TEST_USER_ID);
		Visit aux = new Visit();
		aux.setDescription("Prueba");
		aux.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		aux.setVet(vet);
		aux.setPet(simba);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void shouldNotCreateInvalidVisitNullPet() throws Exception {
		logged.setId(TEST_USER_ID);
		Visit aux = new Visit();
		aux.setDescription("Prueba");
		aux.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		aux.setVet(vet);
		aux.setPet(null);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void shouldNotCreateInvalidVisitInvalidDatetime() throws Exception {
		logged.setId(TEST_USER_ID);
		Visit aux = new Visit();
		aux.setDescription("Prueba");
		aux.setDatetime(null);
		aux.setVet(vet);
		aux.setPet(simba);

		// null datetime
		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest());

		// datetime before 9AM
		aux.setDatetime(LocalDateTime.of(2010, 1, 1, 8, 0));

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest());

		// datetime after 8PM
		aux.setDatetime(LocalDateTime.of(2010, 1, 1, 21, 0));

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldCreateVisit() throws Exception {
		logged.setId(TEST_USER_ID);
		Visit aux = new Visit();
		aux.setDescription("Prueba");
		aux.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		aux.setVet(vet);
		aux.setPet(simba);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.visitService.underLimit(any(Visit.class))).thenReturn(true);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotCreateVisitForOthersPet() throws Exception {
		logged.setId(2);
		Visit aux = new Visit();
		aux.setDescription("Prueba");
		aux.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		aux.setVet(vet);
		aux.setPet(simba);

		Owner other = new Owner();
		other.setId(2);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);
		when(this.visitService.underLimit(any(Visit.class))).thenReturn(false);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotCreatePetPassedLimit() throws Exception {
		logged.setId(TEST_USER_ID);
		Visit aux = new Visit();
		aux.setDescription("Prueba");
		aux.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		aux.setVet(vet);
		aux.setPet(simba);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.visitService.underLimit(any(Visit.class))).thenReturn(false);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof LimitReachedException))
				.andExpect(result -> assertEquals(
						"You have reached the limit for Visits per month for your Pet Simba with the BASIC plan. Please, contact with the clinic owner to ask for a plan upgrade.",
						result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldUpdateVisit() throws Exception {
		visit.setDescription("UPDATED");

		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.visitService.updateVisit(any(Visit.class), any(Integer.class))).thenReturn(visit);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(visit))).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value(visit.getDescription()))
				.andExpect(jsonPath("$.pet.name").value(visit.getPet().getName()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldUpdateVisit() throws Exception {
		logged.setId(TEST_USER_ID);
		visit.setDescription("UPDATED");

		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.visitService.updateVisit(any(Visit.class), any(Integer.class))).thenReturn(visit);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(visit))).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value(visit.getDescription()))
				.andExpect(jsonPath("$.pet.name").value(visit.getPet().getName()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotUpdateOthersVisit() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);
		when(this.visitService.updateVisit(any(Visit.class), any(Integer.class))).thenReturn(visit);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_PET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(visit))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldDeleteVisit() throws Exception {
		logged.setId(TEST_USER_ID);
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		doNothing().when(this.visitService).deleteVisit(TEST_VISIT_ID);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_VISIT_ID).with(csrf())).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldDeleteVisit() throws Exception {
		logged.setId(TEST_USER_ID);
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		doNothing().when(this.visitService).deleteVisit(TEST_VISIT_ID);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_VISIT_ID).with(csrf())).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotDeleteOthersVisit() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.visitService.findVisitById(TEST_VISIT_ID)).thenReturn(visit);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);
		doNothing().when(this.visitService).deleteVisit(TEST_VISIT_ID);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_VISIT_ID).with(csrf())).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminOrVetShouldFindAllVisits() throws Exception {
		Visit stomach = new Visit();
		stomach.setId(2);
		stomach.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setDescription("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setVet(vet);

		Pet other = new Pet();
		other.setId(2);
		other.setName("other");

		Visit leg = new Visit();
		leg.setId(3);
		leg.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		leg.setDescription("Checking Simba's leg.");
		leg.setPet(other);
		leg.setVet(vet);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.visitService.findAll()).thenReturn(List.of(visit, stomach, leg));

		mockMvc.perform(get(VISITS_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].description").value("Checking Simba's teeth."))
				.andExpect(jsonPath("$[?(@.id == 2)].pet.name").value(simba.getName()))
				.andExpect(jsonPath("$[?(@.id == 3)].pet.name").value(other.getName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindAllVisitsFromOwnedPets() throws Exception {
		logged.setId(TEST_USER_ID);
		Visit stomach = new Visit();
		stomach.setId(2);
		stomach.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setDescription("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setVet(vet);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.visitService.findVisitsByOwnerId(TEST_OWNER_ID)).thenReturn(List.of(visit, stomach));

		mockMvc.perform(get(VISITS_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[?(@.id == 1)].description").value("Checking Simba's teeth."))
				.andExpect(jsonPath("$[?(@.id == 2)].pet.name").value(simba.getName()));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminShouldFindAllWithOwner() throws Exception {
		logged.setId(2);

		Visit stomach = new Visit();
		stomach.setId(2);
		stomach.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setDescription("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setVet(vet);

		Visit leg = new Visit();
		leg.setId(3);
		leg.setDatetime(LocalDateTime.of(2010, 1, 1, 12, 0));
		leg.setDescription("Checking Simba's leg.");
		leg.setVet(vet);

		when(this.ownerService.findOwnerById(TEST_OWNER_ID)).thenReturn(george);
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.visitService.findVisitsByOwnerId(TEST_OWNER_ID)).thenReturn(List.of(visit, stomach));

		mockMvc.perform(get(VISITS_URL).param("ownerId", TEST_OWNER_ID.toString())).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[?(@.id == 1)].pet.name").value("Simba"))
				.andExpect(jsonPath("$[?(@.id == 2)].pet.name").value(simba.getName()))
				.andExpect(jsonPath("$[?(@.id == 1)].description").value("Checking Simba's teeth."))
				.andExpect(jsonPath("$[?(@.id == 2)].pet.name").value(simba.getName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindOthersPets() throws Exception {
		logged.setId(TEST_USER_ID);
		when(this.ownerService.findOwnerById(TEST_OWNER_ID)).thenReturn(george);
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);

		mockMvc.perform(get(VISITS_URL).param("ownerId", TEST_OWNER_ID.toString())).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
				.andExpect(result -> assertEquals("Access denied!", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void shouldReturnOwnerStats() throws Exception {
		logged.setId(TEST_USER_ID);
		clinic.setPlan(PricingPlan.PLATINUM);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.visitService.getVisitsOwnerStats(george.getId())).thenReturn(new HashMap<>());

		mockMvc.perform(get(VISITS_URL + "/stats")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void shouldNotReturnOwnerStatsNotPlatinum() throws Exception {
		logged.setId(TEST_USER_ID);
		//george.setPlan(PricingPlan.BASIC);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.visitService.getVisitsOwnerStats(george.getId())).thenReturn(new HashMap<>());

		mockMvc.perform(get(VISITS_URL + "/stats")).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void shouldReturnAdminStats() throws Exception {
		logged.setId(TEST_USER_ID);
		when(this.visitService.getVisitsAdminStats()).thenReturn(new HashMap<>());

		mockMvc.perform(get(VISITS_URL + "/stats")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "vet", authorities = "VET")
	void shouldNotReturnVetStats() throws Exception {
		logged.setId(TEST_USER_ID);

		mockMvc.perform(get(VISITS_URL + "/stats")).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

}
