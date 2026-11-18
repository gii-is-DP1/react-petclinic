package org.springframework.samples.petclinic.consultation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.exceptions.UpperPlanFeatureException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerService;
import org.springframework.samples.petclinic.pet.PetService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.samples.petclinic.vet.VetService;
import org.springframework.transaction.annotation.Transactional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import jakarta.persistence.EntityManager;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.ServiceTest;
import org.springframework.samples.petclinic.testing.SociableTest;

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@Epic("Clinic Module")
@Feature("Consultation management")
@io.qameta.allure.Owner("DP1-tutors")
@ServiceTest
@SociableTest
@SpringBootTest
@AutoConfigureTestDatabase
class ConsultationServiceTests {

	@Autowired
	protected ConsultationService consultationService;
	@Autowired
	protected OwnerService ownerService;

	@Autowired
	protected VetService vetService;

	@Autowired
	protected PetService petService;

	@Story("HU-10: Gestionar consultas")
	@Description("Se obtienen todas las consultas.")
	@Test
	void shouldFindAllConsultations() {
		Collection<Consultation> consultations = (Collection<Consultation>) this.consultationService.findAll();

		Consultation c1 = EntityUtils.getById(consultations, Consultation.class, 1);
		assertEquals("owner1", c1.getOwner().getUser().getUsername());
		Consultation c3 = EntityUtils.getById(consultations, Consultation.class, 3);
		assertEquals("My cat does not eat", c3.getTitle());
	}

	@Story("HU-10: Gestionar consultas")
	@Description("Se obtienen las consultas de un propietario.")
	@Test
	void shouldFindConsultationsByOwnerId() {
		Collection<Consultation> consultations = (Collection<Consultation>) this.consultationService
				.findAllConsultationsByOwner(1);

		Consultation c1 = EntityUtils.getById(consultations, Consultation.class, 1);
		assertEquals("owner1", c1.getOwner().getUser().getUsername());
		Consultation c2 = EntityUtils.getById(consultations, Consultation.class, 2);
		assertEquals("My dog gets really nervous", c2.getTitle());
	}

	@Story("HU-10: Gestionar consultas")
	@Description("Se encuentra una consulta por su id.")
	@Test
	void shouldFindConsultationWithCorrectId() {
		Consultation consultation = this.consultationService.findConsultationById(1);
		assertEquals("owner1", consultation.getOwner().getUser().getUsername());
	}

	@Story("HU-10: Gestionar consultas")
	@Description("Buscar un id de consulta inexistente lanza una excepción.")
	@Test
	void shouldNotFindConsultationWithIncorrectId() {
		assertThrows(ResourceNotFoundException.class, () -> this.consultationService.findConsultationById(700));
	}

	@Story("HU-10: Gestionar consultas")
	@Description("Se puede dar de alta una consulta y se le asigna un id.")
	@Test
	@Transactional
	void shouldInsertConsultation() {
		int initialCount = ((Collection<Consultation>) this.consultationService.findAll()).size();

		Consultation cons = new Consultation();
		cons.setTitle("Consulta de prueba");
		cons.setStatus(ConsultationStatus.PENDING);
		cons.setOwner(this.ownerService.findOwnerById(2));
		cons.setPet(this.petService.findPetById(2));

		this.consultationService.saveConsultation(cons);
		Assertions.assertThat(cons.getId().longValue()).isNotZero();

		int finalCount = ((Collection<Consultation>) this.consultationService.findAll()).size();
		assertEquals(initialCount + 1, finalCount);
	}

	@Story("HU-10: Gestionar consultas")
	@Description("Se pueden modificar los datos de una consulta.")
	@Test
	@Transactional
	void shouldUpdateConsultation() {
		Consultation cons = this.consultationService.findConsultationById(1);
		cons.setTitle("Change");
		cons.setStatus(ConsultationStatus.ANSWERED);
		consultationService.updateConsultation(cons, 1);
		cons = this.consultationService.findConsultationById(1);
		assertEquals("Change", cons.getTitle());
		assertEquals(ConsultationStatus.ANSWERED, cons.getStatus());
	}



	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se obtienen los tickets de una consulta.")
	@Test
	void shouldFindAllTicketsByConsultation() {
		Collection<Ticket> tickets = (Collection<Ticket>) this.consultationService.findAllTicketsByConsultation(1);

		Ticket t1 = EntityUtils.getById(tickets, Ticket.class, 1);
		assertEquals("What vaccine should my dog receive?", t1.getDescription());
		Ticket t2 = EntityUtils.getById(tickets, Ticket.class, 2);
		assertEquals("Rabies' one.", t2.getDescription());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se encuentra un ticket por su id.")
	@Test
	void shouldFindTicketWithCorrectId() {
		Ticket t = this.consultationService.findTicketById(1);
		assertEquals("What vaccine should my dog receive?", t.getDescription());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Buscar un id de ticket inexistente lanza una excepción.")
	@Test
	void shouldNotFindTicektWithIncorrectId() {
		assertThrows(ResourceNotFoundException.class, () -> this.consultationService.findTicketById(700));
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se puede añadir un ticket a una consulta y se le asigna un id.")
	@Test
	@Transactional
	void shouldInsertTicket() {
		int initialCount = ((Collection<Ticket>) this.consultationService.findAllTicketsByConsultation(1)).size();

		Ticket t = new Ticket();
		t.setDescription("Consulta de prueba");
		t.setConsultation(this.consultationService.findConsultationById(1));
		t.setUser(this.ownerService.findOwnerById(1).getUser());

		this.consultationService.saveTicket(t);
		Assertions.assertThat(t.getId().longValue()).isNotZero();

		int finalCount = ((Collection<Ticket>) this.consultationService.findAllTicketsByConsultation(1)).size();
		assertEquals(initialCount + 1, finalCount);
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se pueden modificar los datos de un ticket.")
	@Test
	@Transactional
	void shouldUpdateTicket() {
		Ticket t = this.consultationService.findTicketById(1);
		t.setDescription("Change");
		consultationService.updateTicket(t, 1);
		t = this.consultationService.findTicketById(1);
		assertEquals("Change", t.getDescription());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se puede borrar un ticket.")
	@Test
	@Transactional
	void shouldDeleteTicket() throws DataAccessException {
		int initialCount = ((Collection<Ticket>) this.consultationService.findAllTicketsByConsultation(1)).size();

		Ticket t = new Ticket();
		t.setDescription("Consulta de prueba");
		t.setConsultation(this.consultationService.findConsultationById(1));
		t.setUser(this.ownerService.findOwnerById(1).getUser());
		this.consultationService.saveTicket(t);

		Integer secondCount = ((Collection<Ticket>) this.consultationService.findAllTicketsByConsultation(1)).size();
		assertEquals(initialCount + 1, secondCount);
		consultationService.deleteTicket(t.getId());
		Integer lastCount = ((Collection<Ticket>) this.consultationService.findAllTicketsByConsultation(1)).size();
		assertEquals(initialCount, lastCount);
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se permite modificar el último ticket de una consulta abierta.")
	@Test
	void shouldDoNothingLastTicketNotClosed() {
		Consultation cons = this.consultationService.findConsultationById(1);
		Ticket t = this.consultationService.findTicketById(2);
		assertDoesNotThrow(() -> this.consultationService.checkLastTicketAndStatus(cons, t));
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Solo se puede modificar o borrar el último ticket de una consulta.")
	@Test
	void shouldThrowNotLastTicket() {
		Consultation cons = this.consultationService.findConsultationById(1);
		Ticket t = this.consultationService.findTicketById(1);
		AccessDeniedException e = assertThrows(AccessDeniedException.class,
				() -> this.consultationService.checkLastTicketAndStatus(cons, t));
		assertEquals("You can only update or delete the last ticket in a consultation!", e.getMessage());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("No se pueden modificar ni borrar tickets de una consulta cerrada.")
	@Test
	void shouldThrowClosedConsultation() {
		Consultation cons = this.consultationService.findConsultationById(4);
		Ticket t = this.consultationService.findTicketById(8);
		AccessDeniedException e = assertThrows(AccessDeniedException.class,
				() -> this.consultationService.checkLastTicketAndStatus(cons, t));
		assertEquals("This consultation is closed!", e.getMessage());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Un propietario con plan PLATINUM puede modificar su ticket.")
	@Test
	@Transactional
	void shouldUpdateOwnerTicket() {
		Ticket ticket = this.consultationService.findTicketById(4);
		ticket.setDescription("Updated");
		Owner o = this.ownerService.findOwnerById(1);
		Ticket response = this.consultationService.updateOwnerTicket(ticket, ticket.getId(), o);
		assertEquals("Updated", response.getDescription());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Un propietario sin plan PLATINUM no puede modificar tickets.")
	@Test
	@Transactional
	void shouldNotUpdateOwnerTicketNotPlatinum() {
		Ticket ticket = this.consultationService.findTicketById(4);
		int id = ticket.getId();
		ticket.setDescription("Updated");
		Owner o = this.ownerService.findOwnerById(4);
		UpperPlanFeatureException response = assertThrows(UpperPlanFeatureException.class,
				() -> this.consultationService.updateOwnerTicket(ticket, id, o));
		assertEquals("You need to be subscribed to plan PLATINUM to access this feature and you have plan GOLD.",
				response.getMessage());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se pueden añadir varios tickets seguidos a una consulta.")
	@Test
	@Transactional
	void shouldDeleteAdminTicket() {
		final int CONSULTATION_ID = 2;
		Integer initialCount = ((Collection<Ticket>) this.consultationService
				.findAllTicketsByConsultation(CONSULTATION_ID)).size();
		Consultation c = this.consultationService.findConsultationById(CONSULTATION_ID);
		Ticket t1 = new Ticket();
		t1.setDescription("Consulta de prueba");
		t1.setConsultation(c);
		t1.setUser(this.ownerService.findOwnerById(1).getUser());
		this.consultationService.saveTicket(t1);

		Ticket t2 = new Ticket();
		t2.setDescription("Consulta de prueba");
		t2.setConsultation(c);
		t2.setUser(this.ownerService.findOwnerById(1).getUser());
		this.consultationService.saveTicket(t2);

		Ticket t3 = new Ticket();
		t3.setDescription("Consulta de prueba");
		t3.setConsultation(c);
		t3.setUser(this.ownerService.findOwnerById(1).getUser());
		this.consultationService.saveTicket(t3);

		Integer secondCount = ((Collection<Ticket>) this.consultationService
				.findAllTicketsByConsultation(CONSULTATION_ID)).size();
		assertEquals(initialCount + 3, secondCount);
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Un propietario con plan PLATINUM puede borrar su ticket.")
	@Test
	@Transactional
	void shouldDeleteOwnerTicket() {
		final int CONSULTATION_ID = 2;
		Integer initialCount = ((Collection<Ticket>) this.consultationService
				.findAllTicketsByConsultation(CONSULTATION_ID)).size();
		Consultation c = this.consultationService.findConsultationById(CONSULTATION_ID);
		Owner o = this.ownerService.findOwnerById(1);
		Ticket t = new Ticket();
		t.setDescription("Consulta de prueba");
		t.setConsultation(c);
		t.setUser(this.ownerService.findOwnerById(1).getUser());
		this.consultationService.saveTicket(t);

		Integer secondCount = ((Collection<Ticket>) this.consultationService
				.findAllTicketsByConsultation(CONSULTATION_ID)).size();
		assertEquals(initialCount + 1, secondCount);

		this.consultationService.deleteOwnerTicket(t, o);
		Integer lastCount = ((Collection<Ticket>) this.consultationService
				.findAllTicketsByConsultation(CONSULTATION_ID)).size();
		assertEquals(initialCount, lastCount);
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Un propietario sin plan PLATINUM no puede borrar tickets.")
	@Test
	@Transactional
	void shouldNotDeleteOwnerTicketNotPlatinum() {
		Consultation c = this.consultationService.findConsultationById(1);
		Owner o = this.ownerService.findOwnerById(4);
		Ticket t = new Ticket();
		t.setDescription("Consulta de prueba");
		t.setConsultation(c);
		t.setUser(this.vetService.findVetById(1).getUser());
		this.consultationService.saveTicket(t);

		UpperPlanFeatureException response = assertThrows(UpperPlanFeatureException.class,
				() -> this.consultationService.deleteOwnerTicket(t, o));
		assertEquals("You need to be subscribed to plan PLATINUM to access this feature and you have plan GOLD.",
				response.getMessage());
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se acepta un ticket que pertenece a la consulta indicada.")
	@Test
	@Transactional
	void shouldDoNothingTicketInConsultation() {
		Consultation c = this.consultationService.findConsultationById(1);
		Ticket t = this.consultationService.findTicketById(1);

		assertDoesNotThrow(() -> this.consultationService.checkIfTicketInConsultation(c, t));
	}

	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Se rechaza un ticket que no pertenece a la consulta indicada.")
	@Test
	@Transactional
	void shouldThrowTicketNotInConsultation() {
		Consultation c = this.consultationService.findConsultationById(1);
		Ticket t = this.consultationService.findTicketById(6);

		AccessDeniedException e = assertThrows(AccessDeniedException.class,
				() -> this.consultationService.checkIfTicketInConsultation(c, t));
		assertEquals(String.format("The ticket %s doesn't belong to the consultation %s.", t.getId(), c.getId()),
				e.getMessage());
	}

	@Story("HU-10: Gestionar consultas")
	@Story("HU-11: Gestionar tickets de consulta")
	@Description("Al borrar todos los tickets de una consulta, esta vuelve a estado PENDING.")
	@Test
	@Transactional
	void shouldUpdateConsultationNoTickets() {
		List<Ticket> tickets = (List<Ticket>) this.consultationService.findAllTicketsByConsultation(1);
		for (Ticket t : tickets)
			this.consultationService.deleteTicket(t.getId());
		assertEquals(ConsultationStatus.PENDING, this.consultationService.findConsultationById(1).getStatus());

	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Se calculan las estadísticas de consultas para el administrador.")
	@Test
	@Transactional
	void shouldReturnStatsForAdmin() {
		Map<String, Object> stats = this.consultationService.getAdminConsultationsStats();
		assertTrue(stats.containsKey("totalConsultations"));
		assertEquals(5, stats.get("totalConsultations"));
		assertTrue(stats.containsKey("avgConsultationsByPlatinum"));
		assertNotEquals(0, stats.get("avgConsultationsByPlatinum"));
		assertTrue(stats.containsKey("avgConsultationsByOwners"));
		assertNotEquals(0, stats.get("avgConsultationsByOwners"));
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Se calculan las estadísticas de consultas de un propietario.")
	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	void shouldReturnStatsForOwner() {
		Map<String, Object> stats = this.consultationService
				.getOwnerConsultationsStats(ownerService.findOwnerById(1).getId());
		assertTrue(stats.containsKey("totalConsultations"));
		assertEquals(2, stats.get("totalConsultations"));
		assertTrue(stats.containsKey("consultationsByYear"));
		assertEquals(1, ((Map<String, Integer>) stats.get("consultationsByYear")).get("2023"));
		assertTrue(stats.containsKey("avgConsultationsByYear"));
		assertNotEquals(0, stats.get("avgConsultationsByYear"));
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Las estadísticas de un propietario sin consultas están vacías.")
	@Test
	@Transactional
	void shouldReturnStatsForOwnerWithoutConsultations() {
		Map<String, Object> stats = this.consultationService
				.getOwnerConsultationsStats(ownerService.findOwnerById(9).getId());
		assertTrue(stats.containsKey("totalConsultations"));
		assertEquals(0, stats.get("totalConsultations"));
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Las estadísticas de un propietario con varias mascotas agrupan sus consultas por mascota.")
	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	void shouldReturnStatsForOwnerWithMultiplePets() {
		Map<String, Object> stats = this.consultationService
				.getOwnerConsultationsStats(ownerService.findOwnerById(10).getId());
		assertTrue(stats.containsKey("totalConsultations"));
		assertEquals(1, stats.get("totalConsultations"));
		assertTrue(stats.containsKey("consultationsByPet"));
		assertEquals(1, ((Map<String, Integer>) stats.get("consultationsByPet")).get("Lucky"));
		assertTrue(stats.containsKey("avgConsultationsByPet"));
		assertNotEquals(0, stats.get("avgConsultationsByPet"));
	}

}
