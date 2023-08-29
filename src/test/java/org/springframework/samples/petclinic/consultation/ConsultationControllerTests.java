package org.springframework.samples.petclinic.consultation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.exceptions.ResourceNotOwnedException;
import org.springframework.samples.petclinic.exceptions.UpperPlanFeatureException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.Pet;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for {@link ConsultationController}
 *
 * 
 */
@WebMvcTest(controllers = ConsultationController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class ConsultationControllerTests {
	private static final Integer TEST_OWNER_ID = 1;
	private static final int TEST_USER_ID = 1;
	private static final int TEST_CLINIC_ID = 1;
	private static final int TEST_CLINIC_OWNER_ID = 1;
	private static final int TEST_CLINIC_OWNER_USER_ID = 1;
	private static final int TEST_CONSULTATION_ID = 1;
	private static final int TEST_TICKET_ID = 1;
	private static final String BASE_URL = "/api/v1/consultations";
	private static final String TICKET_URL = "/api/v1/consultations/" + TEST_CONSULTATION_ID + "/tickets";

	@SuppressWarnings("unused")
	@Autowired
	private ConsultationController consultationController;

	@MockBean
	private ConsultationService consultationService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Owner george;
	private Vet vet;
	private Pet simba;
	private User user;
	private User logged;
	private Consultation consultation;
	private Ticket ticket;
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

		PetType lion = new PetType();
		lion.setId(1);
		lion.setName("lion");

		simba = new Pet();
		simba.setId(1);
		simba.setName("Simba");
		simba.setOwner(george);
		simba.setType(lion);
		simba.setBirthDate(LocalDate.of(2000, 01, 01));

		vet = new Vet();
		vet.setId(1);
		vet.setFirstName("Super");
		vet.setLastName("Vet");
		vet.setClinic(clinic);

		consultation = new Consultation();
		consultation.setId(TEST_CONSULTATION_ID);
		consultation.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));
		consultation.setTitle("Checking Simba's teeth.");
		consultation.setPet(simba);
		consultation.setOwner(george);
		consultation.setStatus(ConsultationStatus.PENDING);
		consultation.setIsClinicComment(false);

		ticket = new Ticket();
		ticket.setConsultation(consultation);
		ticket.setDescription("Ticket description");
		ticket.setId(TEST_TICKET_ID);
		ticket.setUser(user);
		ticket.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));

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
	void adminOrVetShouldFindAll() throws Exception {
		Consultation stomach = new Consultation();
		stomach.setId(2);
		stomach.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setTitle("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setOwner(george);
		stomach.setStatus(ConsultationStatus.PENDING);

		Consultation leg = new Consultation();
		leg.setId(3);
		leg.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));
		leg.setTitle("Checking Simba's leg.");
		leg.setPet(simba);
		leg.setOwner(george);
		leg.setStatus(ConsultationStatus.PENDING);

		when(this.consultationService.findAll()).thenReturn(List.of(consultation, stomach, leg));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].title").value("Checking Simba's teeth."))
				.andExpect(jsonPath("$[?(@.id == 2)].title").value("Checking Simba's stomach."))
				.andExpect(jsonPath("$[?(@.id == 3)].title").value("Checking Simba's leg."));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindAllOwned() throws Exception {
		logged.setId(TEST_USER_ID);
		Consultation stomach = new Consultation();
		stomach.setId(2);
		stomach.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));
		stomach.setTitle("Checking Simba's stomach.");
		stomach.setPet(simba);
		stomach.setOwner(george);
		stomach.setStatus(ConsultationStatus.PENDING);

		Consultation leg = new Consultation();
		leg.setId(3);
		leg.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));
		leg.setTitle("Checking Simba's leg.");
		leg.setPet(simba);
		leg.setOwner(george);
		leg.setStatus(ConsultationStatus.PENDING);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.findAllConsultationsByOwner(TEST_OWNER_ID))
				.thenReturn(List.of(consultation, stomach, leg));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].title").value("Checking Simba's teeth."))
				.andExpect(jsonPath("$[?(@.id == 2)].title").value("Checking Simba's stomach."))
				.andExpect(jsonPath("$[?(@.id == 3)].title").value("Checking Simba's leg."));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminOrVetShouldFindConsultation() throws Exception {
		logged.setId(1);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_CONSULTATION_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_CONSULTATION_ID))
				.andExpect(jsonPath("$.title").value(consultation.getTitle()))
				.andExpect(jsonPath("$.status").value(consultation.getStatus().toString()))
				.andExpect(jsonPath("$.pet.name").value(simba.getName()))
				.andExpect(jsonPath("$.owner.firstName").value(george.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindOwnedConsultation() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_CONSULTATION_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_CONSULTATION_ID))
				.andExpect(jsonPath("$.title").value(consultation.getTitle()))
				.andExpect(jsonPath("$.status").value(consultation.getStatus().toString()))
				.andExpect(jsonPath("$.pet.name").value(simba.getName()))
				.andExpect(jsonPath("$.owner.firstName").value(george.getFirstName()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindConsultationFromOthers() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);

		mockMvc.perform(get(BASE_URL + "/{id}", TEST_CONSULTATION_ID)).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundConsultation() throws Exception {
		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_CONSULTATION_ID)).andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldCreateConsultation() throws Exception {
		logged.setId(TEST_USER_ID);
		Consultation aux = new Consultation();
		aux.setId(3);
		aux.setTitle("Checking Simba's leg.");
		aux.setPet(simba);
		aux.setOwner(george);
		aux.setStatus(ConsultationStatus.PENDING);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void shouldNotCreateInvalidConsultationPetNotOfOwner() throws Exception {
		logged.setId(TEST_USER_ID);
		Owner owner = new Owner();
		owner.setId(2);

		Consultation aux = new Consultation();
		aux.setId(2);
		aux.setTitle("Checking Simba's leg.");
		aux.setPet(simba);
		aux.setOwner(owner);
		aux.setStatus(ConsultationStatus.PENDING);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest()).andExpect(
						result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldCreateConsultation() throws Exception {
		logged.setId(TEST_USER_ID);
		clinic.setPlan(PricingPlan.PLATINUM);
		Consultation aux = new Consultation();
		aux.setId(2);
		aux.setTitle("Checking Simba's leg.");
		aux.setPet(simba);
		aux.setOwner(george);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotCreateConsultationIfNotPlatinum() throws Exception {
		logged.setId(TEST_USER_ID);
		Consultation aux = new Consultation();
		aux.setId(2);
		aux.setTitle("Checking Simba's leg.");
		aux.setPet(simba);
		aux.setOwner(george);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof UpperPlanFeatureException))
				.andExpect(result -> assertEquals(
						"You need to be subscribed to plan PLATINUM to access this feature and you have plan BASIC.",
						result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldUpdateConsultation() throws Exception {
		consultation.setTitle("UPDATED");

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.updateConsultation(any(Consultation.class), any(Integer.class)))
				.thenReturn(consultation);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(consultation))).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value(consultation.getTitle()))
				.andExpect(jsonPath("$.pet.name").value(consultation.getPet().getName()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldUpdateVisit() throws Exception {
		logged.setId(TEST_USER_ID);
		consultation.setTitle("UPDATED");
		clinic.setPlan(PricingPlan.PLATINUM);
		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.updateConsultation(any(Consultation.class), any(Integer.class)))
				.thenReturn(consultation);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(consultation))).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value(consultation.getTitle()))
				.andExpect(jsonPath("$.pet.name").value(consultation.getPet().getName()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotUpdateOthersConsultation() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);
		when(this.consultationService.updateConsultation(any(Consultation.class), any(Integer.class)))
				.thenReturn(consultation);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_CONSULTATION_ID).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(consultation)))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException))
				.andExpect(
						result -> assertEquals("Consultation not owned.", result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotUpdateIfNotPlatinum() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.updateConsultation(any(Consultation.class), any(Integer.class)))
				.thenReturn(consultation);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_CONSULTATION_ID).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(consultation)))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof UpperPlanFeatureException))
				.andExpect(result -> assertEquals(
						"You need to be subscribed to plan PLATINUM to access this feature and you have plan BASIC.",
						result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminShouldDeleteConsultation() throws Exception {
		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		
		doNothing().when(this.consultationService).deleteConsultation(TEST_CONSULTATION_ID);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_CONSULTATION_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Consultation deleted!"));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminOrVetShouldFindAllTicketsOfConsultation() throws Exception {
		Ticket t2 = new Ticket();
		t2.setId(2);
		t2.setConsultation(consultation);
		t2.setDescription("T2");
		t2.setUser(user);
		t2.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));

		Ticket t3 = new Ticket();
		t3.setId(3);
		t3.setConsultation(consultation);
		t3.setDescription("T3");
		t3.setUser(user);
		t3.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findAllTicketsByConsultation(TEST_CONSULTATION_ID))
				.thenReturn(List.of(ticket, t2, t3));

		mockMvc.perform(get(TICKET_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].description").value("Ticket description"))
				.andExpect(jsonPath("$[?(@.id == 2)].description").value("T2"))
				.andExpect(jsonPath("$[?(@.id == 3)].description").value("T3"));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindAllTicketsOwned() throws Exception {
		logged.setId(TEST_USER_ID);

		Ticket t2 = new Ticket();
		t2.setId(2);
		t2.setConsultation(consultation);
		t2.setDescription("T2");
		t2.setUser(user);
		t2.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));

		Ticket t3 = new Ticket();
		t3.setId(3);
		t3.setConsultation(consultation);
		t3.setDescription("T3");
		t3.setUser(user);
		t3.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findAllTicketsByConsultation(TEST_CONSULTATION_ID))
				.thenReturn(List.of(ticket, t2, t3));

		mockMvc.perform(get(TICKET_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].description").value("Ticket description"))
				.andExpect(jsonPath("$[?(@.id == 2)].description").value("T2"))
				.andExpect(jsonPath("$[?(@.id == 3)].description").value("T3"));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindTicketsNotOwned() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		Ticket t2 = new Ticket();
		t2.setId(2);
		t2.setConsultation(consultation);
		t2.setDescription("T2");
		t2.setUser(user);
		t2.setCreationDate(LocalDateTime.of(2010, 1, 1, 12, 0));

		when(this.userService.findOwnerByUser(2)).thenReturn(other);
		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);

		mockMvc.perform(get(TICKET_URL)).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException));
	}

	@Test
	@WithMockUser(value = "admin", authorities = { "ADMIN" })
	void adminOrVetShouldFindTicket() throws Exception {
		logged.setId(1);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);

		mockMvc.perform(get(TICKET_URL + "/{id}", TEST_TICKET_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_TICKET_ID))
				.andExpect(jsonPath("$.description").value(ticket.getDescription()))
				.andExpect(jsonPath("$.user.username").value(user.getUsername()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldFindTicketInOwnedConsultation() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);

		mockMvc.perform(get(TICKET_URL + "/{id}", TEST_TICKET_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_TICKET_ID))
				.andExpect(jsonPath("$.description").value(ticket.getDescription()))
				.andExpect(jsonPath("$.user.username").value(user.getUsername()));
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotFindTicketFromOthers() throws Exception {
		logged.setId(2);

		Owner other = new Owner();
		other.setId(2);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);

		mockMvc.perform(get(TICKET_URL + "/{id}", TEST_TICKET_ID)).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminOrVetShouldCreateTicket() throws Exception {
		Ticket aux = new Ticket();
		aux.setDescription("Checking Simba's leg.");

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);

		mockMvc.perform(post(TICKET_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldCreateTicket() throws Exception {
		logged.setId(TEST_USER_ID);
		Ticket aux = new Ticket();
		aux.setDescription("Checking Simba's leg.");
		clinic.setPlan(PricingPlan.PLATINUM);
		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(post(TICKET_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(value = "owner", authorities = { "OWNER" })
	void ownerShouldNotCreateTicketInOthersConsultation() throws Exception {
		logged.setId(2);
		Ticket aux = new Ticket();
		aux.setDescription("Checking Simba's leg.");

		Owner other = new Owner();
		other.setId(2);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(2)).thenReturn(other);

		mockMvc.perform(post(TICKET_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldNotCreateTicketIfNotPlatinum() throws Exception {
		logged.setId(TEST_USER_ID);
		Ticket aux = new Ticket();
		aux.setDescription("Checking Simba's leg.");

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		mockMvc.perform(post(TICKET_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof UpperPlanFeatureException))
				.andExpect(result -> assertEquals(
						"You need to be subscribed to plan PLATINUM to access this feature and you have plan BASIC.",
						result.getResolvedException().getMessage()));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminShouldUpdateTicket() throws Exception {
		ticket.setDescription("UPDATED");

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.updateTicket(any(Ticket.class), any(Integer.class))).thenReturn(ticket);

		mockMvc.perform(put(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticket))).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value(ticket.getDescription()))
				.andExpect(jsonPath("$.user.username").value(user.getUsername()));
	}

	@Test
	@WithMockUser(username = "vet", authorities = "VET")
	void vetShouldUpdateTicket() throws Exception {
		logged.setId(TEST_USER_ID);
		ticket.setDescription("UPDATED");

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);
		when(this.consultationService.updateTicket(any(Ticket.class), any(Integer.class))).thenReturn(ticket);

		mockMvc.perform(put(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticket))).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value(ticket.getDescription()))
				.andExpect(jsonPath("$.user.username").value(user.getUsername()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldUpdateTicket() throws Exception {
		logged.setId(TEST_USER_ID);
		ticket.setDescription("UPDATED");

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.updateOwnerTicket(any(Ticket.class), any(Integer.class), any(Owner.class)))
				.thenReturn(ticket);

		mockMvc.perform(put(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticket))).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value(ticket.getDescription()))
				.andExpect(jsonPath("$.user.username").value(user.getUsername()));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerOrVetShouldNotUpdateOthersTicket() throws Exception {
		logged.setId(2);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);

		mockMvc.perform(put(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ticket))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void adminShouldDeleteTicket() throws Exception {
		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);
		
		doNothing().when(this.consultationService).deleteAdminTicket(ticket, consultation);

		mockMvc.perform(delete(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Ticket deleted!"));
	}

	@Test
	@WithMockUser(username = "vet", authorities = "VET")
	void vetShouldDeleteTicket() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);

		doNothing().when(this.consultationService).deleteOwnerTicket(ticket, george);

		mockMvc.perform(delete(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Ticket deleted!"));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerShouldDeleteTicket() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);

		doNothing().when(this.consultationService).deleteTicket(TEST_TICKET_ID);

		mockMvc.perform(delete(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Ticket deleted!"));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void ownerOrVetShouldNotDeleteOthersTicket() throws Exception {
		logged.setId(2);

		when(this.consultationService.findConsultationById(TEST_CONSULTATION_ID)).thenReturn(consultation);
		when(this.consultationService.findTicketById(TEST_TICKET_ID)).thenReturn(ticket);

		doNothing().when(this.consultationService).deleteTicket(TEST_TICKET_ID);

		mockMvc.perform(delete(TICKET_URL + "/{id}", TEST_TICKET_ID).with(csrf())).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotOwnedException));
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void shouldReturnOwnerStats() throws Exception {
		logged.setId(TEST_USER_ID);
		clinic.setPlan(PricingPlan.PLATINUM);
		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.getOwnerConsultationsStats(george.getId())).thenReturn(new HashMap<>());

		mockMvc.perform(get(BASE_URL + "/stats")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "owner", authorities = "OWNER")
	void shouldNotReturnOwnerStatsNotPlatinum() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.userService.findOwnerByUser(TEST_USER_ID)).thenReturn(george);
		when(this.consultationService.getOwnerConsultationsStats(george.getId())).thenReturn(new HashMap<>());

		mockMvc.perform(get(BASE_URL + "/stats")).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	void shouldReturnAdminStats() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.consultationService.getAdminConsultationsStats()).thenReturn(new HashMap<>());

		mockMvc.perform(get(BASE_URL + "/stats")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "vet", authorities = "VET")
	void shouldNotReturnVetStats() throws Exception {
		logged.setId(TEST_USER_ID);

		mockMvc.perform(get(BASE_URL + "/stats")).andExpect(status().isForbidden())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

}
