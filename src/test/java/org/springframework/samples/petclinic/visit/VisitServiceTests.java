package org.springframework.samples.petclinic.visit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.owner.OwnerService;
import org.springframework.samples.petclinic.pet.PetService;
import org.springframework.samples.petclinic.pet.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.samples.petclinic.vet.VetService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.ServiceTest;
import org.springframework.samples.petclinic.testing.SociableTest;

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@Epic("Clinic Module")
@Feature("Visit management")
@io.qameta.allure.Owner("DP1-tutors")
@ServiceTest
@SociableTest
@SpringBootTest
@AutoConfigureTestDatabase
public class VisitServiceTests {

	@Autowired
	protected PetService petService;

	@Autowired
	protected OwnerService ownerService;

	@Autowired
	protected VisitService visitService;

	@Autowired
	protected VetService vetService;

	@Story("HU-07: Gestionar visitas")
	@Description("Se obtienen todas las visitas.")
	@Test
	void shouldFindAllVisits() {
		Collection<Visit> visits = (Collection<Visit>) this.visitService.findAll();

		Visit v1 = EntityUtils.getById(visits, Visit.class, 1);
		assertEquals("Samantha", v1.getPet().getName());
		Visit v3 = EntityUtils.getById(visits, Visit.class, 3);
		assertEquals("Max", v3.getPet().getName());
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se obtienen las visitas de las mascotas de un propietario.")
	@Test
	void shouldFindVisitsByOwnerId() {
		Collection<Visit> visits = (Collection<Visit>) this.visitService.findVisitsByOwnerId(6);

		Visit v1 = EntityUtils.getById(visits, Visit.class, 1);
		assertEquals("Samantha", v1.getPet().getName());
		Visit v3 = EntityUtils.getById(visits, Visit.class, 3);
		assertEquals("Max", v3.getPet().getName());
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se obtienen las visitas de una mascota.")
	@Test
	void shouldFindVisitsByPetId() {
		Collection<Visit> visits = (Collection<Visit>) this.visitService.findVisitsByPetId(7);

		Visit v1 = EntityUtils.getById(visits, Visit.class, 1);
		assertEquals("Samantha", v1.getPet().getName());
		Visit v4 = EntityUtils.getById(visits, Visit.class, 4);
		assertEquals("Samantha", v4.getPet().getName());
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se encuentra una visita por su id.")
	@Test
	void shouldFindVisitWithCorrectId() {
		Visit visit = this.visitService.findVisitById(1);
		assertEquals("Samantha", visit.getPet().getName());
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Buscar un id de visita inexistente lanza una excepción.")
	@Test
	void shouldNotFindVisitWithIncorrectId() {
		assertThrows(ResourceNotFoundException.class, () -> this.visitService.findVisitById(700));
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se pueden modificar los datos de una visita.")
	@Test
	@Transactional
	void shouldUpdateVisit() {
		Visit visit = this.visitService.findVisitById(1);
		visit.setDescription("Change");
		visitService.updateVisit(visit, 1);
		visit = this.visitService.findVisitById(1);
		assertEquals("Change", visit.getDescription());
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se puede dar de alta una visita y se le asigna un id.")
	@Test
	@Transactional
	void shouldInsertVisit() {
		int initialCount = ((Collection<Visit>) this.visitService.findAll()).size();

		Visit visit = new Visit();
		visit.setDatetime(LocalDateTime.now());
		visit.setDescription("prueba");
		visit.setPet(this.petService.findPetById(1));
		visit.setVet(this.vetService.findVetById(1));

		this.visitService.saveVisit(visit);
		assertThat(visit.getId().longValue()).isNotEqualTo(0);

		int finalCount = ((Collection<Visit>) this.visitService.findAll()).size();
		assertEquals(initialCount + 1, finalCount);
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se puede borrar una visita.")
	@Test
	@Transactional
	void shouldDeleteVisit() throws DataAccessException, DuplicatedPetNameException {
		int initialCount = ((Collection<Visit>) this.visitService.findAll()).size();

		Visit visit = new Visit();
		visit.setDatetime(LocalDateTime.now());
		visit.setDescription("prueba");
		visit.setPet(this.petService.findPetById(1));
		visit.setVet(this.vetService.findVetById(1));
		this.visitService.saveVisit(visit);

		Integer secondCount = ((Collection<Visit>) this.visitService.findAll()).size();
		assertEquals(initialCount + 1, secondCount);
		visitService.deleteVisit(visit.getId());
		Integer lastCount = ((Collection<Visit>) this.visitService.findAll()).size();
		assertEquals(initialCount, lastCount);
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se respeta el límite de visitas mensuales del plan BASIC.")
	@Test
	@Transactional
	void shouldCheckLimitForBasic() {
		Visit v = createVisit(11); // pet of Owner4 BASIC
		assertEquals(true, this.visitService.underLimit(v));
		this.visitService.saveVisit(v);
		v = createVisit(11);
		assertEquals(false, this.visitService.underLimit(v));
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se respeta el límite de visitas mensuales del plan GOLD.")
	@Test
	@Transactional
	void shouldCheckLimitForGold() {
		Visit v = createVisit(7);
		assertEquals(true, this.visitService.underLimit(v));
		this.visitService.saveVisit(v);
		v = createVisit(7);
		assertEquals(true, this.visitService.underLimit(v));
		this.visitService.saveVisit(v);
		v = createVisit(7);
		assertEquals(true, this.visitService.underLimit(v));
		this.visitService.saveVisit(v);
		v = createVisit(7);
		assertEquals(false, this.visitService.underLimit(v));
	}

	@Story("HU-07: Gestionar visitas")
	@Description("Se respeta el límite de visitas mensuales del plan PLATINUM.")
	@Test
	@Transactional
	void shouldCheckLimitForPlatinum() {
		Visit v = createVisit(1);
		assertEquals(true, this.visitService.underLimit(v));
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		v = createVisit(1);
		this.visitService.saveVisit(v);
		assertEquals(false, this.visitService.underLimit(v));
	}

	private Visit createVisit(int pet) {
		Visit visit = new Visit();
		visit.setDatetime(LocalDateTime.now());
		visit.setDescription("prueba");
		visit.setPet(this.petService.findPetById(pet));
		visit.setVet(this.vetService.findVetById(1));
		return visit;
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Se calculan las estadísticas de visitas para el administrador.")
	@Test
	@Transactional
	void shouldReturnStatsForAdmin() {
		Map<String, Object> stats = this.visitService.getVisitsAdminStats();
		assertTrue(stats.containsKey("totalVisits"));
		assertEquals(9, stats.get("totalVisits"));
		assertTrue(stats.containsKey("avgVisitsByPet"));
		assertNotEquals(0, stats.get("avgVisitsByPet"));
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Se calculan las estadísticas de visitas de un propietario.")
	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	void shouldReturnStatsForOwner() {
		Map<String, Object> stats = this.visitService.getVisitsOwnerStats(ownerService.findOwnerById(1).getId());
		assertTrue(stats.containsKey("totalVisits"));
		assertEquals(3, stats.get("totalVisits"));
		assertTrue(stats.containsKey("visitsByYear"));
		assertEquals(2, ((Map<String, Integer>) stats.get("visitsByYear")).get("2020"));
		assertTrue(stats.containsKey("avgVisitsByYear"));
		assertNotEquals(0, stats.get("avgVisitsByYear"));
		assertTrue(stats.containsKey("visitsByPet"));
		assertEquals(3, ((Map<String, Integer>) stats.get("visitsByPet")).get("Leo"));
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Las estadísticas de un propietario sin visitas están vacías.")
	@Test
	@Transactional
	void shouldReturnStatsForOwnerWithoutVisits() {
		Map<String, Object> stats = this.visitService.getVisitsOwnerStats(ownerService.findOwnerById(9).getId());
		assertTrue(stats.containsKey("totalVisits"));
		assertEquals(0, stats.get("totalVisits"));
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Las estadísticas de un propietario sin mascotas están vacías.")
	@Test
	@Transactional
	void shouldReturnStatsForOwnerWithoutPets() {
		Map<String, Object> stats = this.visitService.getVisitsOwnerStats(ownerService.findOwnerById(9).getId());
		assertTrue(stats.containsKey("totalVisits"));
		assertEquals(0, stats.get("totalVisits"));
	}

}
