package org.springframework.samples.petclinic.pet;

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
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.LimitReachedException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.exceptions.ResourceNotOwnedException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRestController;
import org.springframework.samples.petclinic.pet.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for {@link OwnerRestController}
 *
 */

@WebMvcTest(value = PetRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
class PetRestControllerTests {

	private static final int TEST_PET_ID = 1;
	private static final int TEST_OWNER_ID = 1;
	private static final int TEST_CLINIC_ID = 1;
	private static final int TEST_CLINIC_OWNER_ID = 1;
	private static final int TEST_CLINIC_OWNER_USER_ID = 1;
	private static final int TEST_TYPE_ID = 1;
	private static final Integer TEST_USER_ID = 1;
	private static final String BASE_URL = "/api/v1/pets";

	@SuppressWarnings("unused")
	@Autowired
	private PetRestController petController;

	@MockBean
	private PetService petService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Owner george;
	private Pet simba;
	private User user, logged;
	private PetType lion;
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

		when(this.userService.findCurrentUser()).thenReturn(getUserFromDetails(
				(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));

//		adminJwt = getToken("admin", "ADMIN");
//		ownerJwt = getToken("owner", "OWNER");

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
	void adminShouldFindAll() throws Exception {
		Pet timon = new Pet();
		timon.setId(2);
		timon.setName("Timon");

		Pet pumba = new Pet();
		pumba.setName("Pumba");
		pumba.setId(3);

		when(this.petService.findAll()).thenReturn(List.of(simba, timon, pumba));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].name").value("Simba"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("Timon"))
				.andExpect(jsonPath("$[?(@.id == 3)].name").value("Pumba"));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindAll() throws Exception {
		Pet timon = new Pet();
		timon.setId(2);
		timon.setName("Timon");

		Pet pumba = new Pet();
		pumba.setName("Pumba");
		pumba.setId(3);

		when(this.petService.findAll()).thenReturn(List.of(simba, timon, pumba));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
				.andExpect(result -> assertEquals("Access denied!", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminShouldFindAllWithOwner() throws Exception {
		logged.setId(2);

		Pet timon = new Pet();
		timon.setId(2);
		timon.setName("Timon");
		timon.setOwner(george);

		Pet pumba = new Pet();
		pumba.setName("Pumba");
		pumba.setId(3);

		when(this.petService.findAllPetsByUserId(TEST_USER_ID)).thenReturn(List.of(simba, timon));

		mockMvc.perform(get(BASE_URL).param("userId", TEST_USER_ID.toString())).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[?(@.id == 1)].name").value("Simba"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("Timon"))
				.andExpect(jsonPath("$[?(@.id == 1)].owner.firstName").value(george.getFirstName()))
				.andExpect(jsonPath("$[?(@.id == 2)].owner.firstName").value(george.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindAllOwnedPets() throws Exception {
		logged.setId(TEST_USER_ID);

		Pet timon = new Pet();
		timon.setId(2);
		timon.setName("Timon");
		timon.setOwner(george);

		when(this.petService.findAllPetsByUserId(TEST_USER_ID)).thenReturn(List.of(simba, timon));

		mockMvc.perform(get(BASE_URL).param("userId", TEST_USER_ID.toString())).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[?(@.id == 1)].name").value("Simba"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("Timon"))
				.andExpect(jsonPath("$[?(@.id == 1)].owner.firstName").value(george.getFirstName()))
				.andExpect(jsonPath("$[?(@.id == 2)].owner.firstName").value(george.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindOthersPets() throws Exception {
		logged.setId(2);

		Pet timon = new Pet();
		timon.setId(2);
		timon.setName("Timon");
		timon.setOwner(george);

		when(this.petService.findAllPetsByUserId(TEST_USER_ID)).thenReturn(List.of(simba, timon));

		mockMvc.perform(get(BASE_URL).param("userId", TEST_USER_ID.toString())).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
				.andExpect(result -> assertEquals("Access denied!", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(value = "admin")
	void shouldFindAllTypes() throws Exception {
		PetType dog = new PetType();
		dog.setId(2);
		dog.setName("dog");

		PetType cat = new PetType();
		cat.setId(3);
		cat.setName("cat");

		when(this.petService.findPetTypes()).thenReturn(List.of(lion, dog, cat));

		mockMvc.perform(get(BASE_URL + "/types")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].name").value("lion"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("dog"))
				.andExpect(jsonPath("$[?(@.id == 3)].name").value("cat"));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminShouldFindPet() throws Exception {
		logged.setId(1);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_PET_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_PET_ID)).andExpect(jsonPath("$.name").value(simba.getName()))
				.andExpect(jsonPath("$.type.name").value(simba.getType().getName()))
				.andExpect(jsonPath("$.owner.firstName").value(george.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindOwnedPet() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_PET_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_PET_ID)).andExpect(jsonPath("$.name").value(simba.getName()))
				.andExpect(jsonPath("$.type.name").value(simba.getType().getName()))
				.andExpect(jsonPath("$.owner.firstName").value(george.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindOthersSinglePet() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.userService.findOwnerByUser(logged.getId())).thenReturn(other);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_PET_ID)).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundPet() throws Exception {
		when(this.petService.findPetById(TEST_PET_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_PET_ID)).andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldCreatePet() throws Exception {
		Pet pet = new Pet();
		pet.setName("Prueba");
		pet.setBirthDate(LocalDate.of(2010, 1, 1));
		pet.setOwner(george);
		pet.setType(lion);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pet))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void shouldNotCreatePetWithInvalidName() throws Exception {
		Pet pet = new Pet();
		pet.setName("");
		pet.setOwner(george);

		// blank name
		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pet))).andExpect(status().isBadRequest());

		// name too long
		pet.setName("A".repeat(51));

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pet))).andExpect(status().isBadRequest());

		// null name
		pet.setName(null);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pet))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldCreatePet() throws Exception {
		logged.setId(TEST_USER_ID);

		Pet pet = new Pet();
		pet.setName("Prueba");
		pet.setBirthDate(LocalDate.of(2010, 1, 1));
		pet.setOwner(george);
		pet.setType(lion);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.petService.underLimit(george)).thenReturn(true);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pet))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotCreatePetPassedLimit() throws Exception {
		logged.setId(TEST_USER_ID);

		Pet pet = new Pet();
		pet.setName("Prueba");
		pet.setBirthDate(LocalDate.of(2010, 1, 1));
		pet.setOwner(george);
		pet.setType(lion);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.petService.underLimit(george)).thenReturn(false);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pet))).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof LimitReachedException))
				.andExpect(result -> assertEquals(
						"You have reached the limit for Pets with the BASIC plan. Please, contact with the clinic owner to ask for a plan upgrade.",
						result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotCreatePetWithSameName() throws Exception {
		logged.setId(TEST_USER_ID);

		Pet pet = new Pet();
		pet.setName("Simba");
		pet.setBirthDate(LocalDate.of(2010, 1, 1));
		pet.setOwner(george);
		pet.setType(lion);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.petService.underLimit(george)).thenReturn(true);
		when(this.petService.savePet(any(Pet.class))).thenThrow(DuplicatedPetNameException.class);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pet))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicatedPetNameException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldUpdatePet() throws Exception {
		simba.setName("UPDATED");

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.petService.updatePet(any(Pet.class), any(Integer.class))).thenReturn(simba);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_PET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simba))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(simba.getName()))
				.andExpect(jsonPath("$.type.name").value(simba.getType().getName()));
	}

	@Test
	@WithMockUser(username = "admin")
	void shouldReturnNotFoundUpdatePet() throws Exception {
		simba.setName("UPDATED");

		when(this.petService.findPetById(TEST_PET_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.petService.updatePet(any(Pet.class), any(Integer.class))).thenReturn(simba);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_PET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simba))).andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldUpdatePet() throws Exception {
		logged.setId(TEST_USER_ID);
		simba.setName("UPDATED");

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.petService.updatePet(any(Pet.class), any(Integer.class))).thenReturn(simba);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_PET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simba))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(simba.getName()))
				.andExpect(jsonPath("$.type.name").value(simba.getType().getName()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotUpdateOthersPet() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		when(this.petService.updatePet(any(Pet.class), any(Integer.class))).thenReturn(simba);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_PET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simba))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	  void adminOrVetShouldDeletePet() throws Exception {
		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
	    doNothing().when(this.petService).deletePet(TEST_PET_ID);
	    
	    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PET_ID).with(csrf()))
	         .andExpect(status().isOk());
	  }

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldDeletePet() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		doNothing().when(this.petService).deletePet(TEST_PET_ID);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PET_ID).with(csrf())).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotDeleteOthersPet() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.petService.findPetById(TEST_PET_ID)).thenReturn(simba);
		doNothing().when(this.petService).deletePet(TEST_PET_ID);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PET_ID).with(csrf())).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(result -> assertEquals("Pet not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void shouldReturnStats() throws Exception {
		when(this.petService.getPetsStats()).thenReturn(new HashMap<>());

		mockMvc.perform(get(BASE_URL + "/stats")).andExpect(status().isOk());
	}

}
