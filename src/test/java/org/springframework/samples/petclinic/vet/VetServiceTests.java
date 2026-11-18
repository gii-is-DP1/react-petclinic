/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.pet.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.transaction.annotation.Transactional;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.ServiceTest;
import org.springframework.samples.petclinic.testing.SociableTest;

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@Epic("Clinic Module")
@Feature("Vet management")
@io.qameta.allure.Owner("DP1-tutors")
@ServiceTest
@SociableTest
@SpringBootTest
@AutoConfigureTestDatabase
class VetServiceTests {

	private VetService vetService;
	private AuthoritiesService authService;
	
	@Autowired
	public VetServiceTests(VetService vetService, AuthoritiesService authService) {
		this.vetService = vetService;
		this.authService = authService;
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Se obtienen todos los veterinarios.")
	@Test
	void shouldFindVets() {
		Collection<Vet> vets = (Collection<Vet>) this.vetService.findAll();

		Vet vet = EntityUtils.getById(vets, Vet.class, 3);
		assertEquals("Douglas", vet.getLastName());
		assertEquals(2, vet.getSpecialties().size());
		assertEquals("surgery", vet.getSpecialties().get(0).getName());
		assertEquals("dentistry", vet.getSpecialties().get(1).getName());
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Se encuentra un veterinario por su id.")
	@Test
	void shouldFindSingleVet() {
		Vet vet = this.vetService.findVetById(1);
		assertThat(vet.getLastName()).startsWith("Carter");
		assertEquals("Sevilla", vet.getCity());
		assertEquals(0, vet.getSpecialties().size());
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Buscar un id de veterinario inexistente lanza una excepción.")
	@Test
	void shouldNotFindSingleVetWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.vetService.findVetById(100));
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Se encuentra el veterinario asociado a un usuario.")
	@Test
	void shouldFindVetByUser() {
		Vet vet = this.vetService.findVetByUser(14);
		assertThat(vet.getLastName()).startsWith("Carter");
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Buscar el veterinario de un usuario que no lo es lanza una excepción.")
	@Test
	void shouldNotFindVetByIncorrectUser() {
		assertThrows(ResourceNotFoundException.class, () -> this.vetService.findVetByUser(34));
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Se encuentra, como opcional, el veterinario asociado a un usuario.")
	@Test
	void shouldFindOptVetByUser() {
		Optional<Vet> vet = this.vetService.optFindVetByUser(14);
		assertThat(vet.get().getLastName()).startsWith("Carter");
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Un usuario que no es veterinario no tiene veterinario asociado.")
	@Test
	void shouldNotFindOptVetByIncorrectUser() {
		assertThat(this.vetService.optFindVetByUser(25)).isEmpty();
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Se pueden modificar los datos de un veterinario.")
	@Test
	@Transactional
	void shouldUpdateVet() {
		Vet vet = this.vetService.findVetById(1);
		vet.setCity("Change");
		vet.setLastName("Update");
		vetService.updateVet(vet, 1);
		vet = this.vetService.findVetById(1);
		assertEquals("Change", vet.getCity());
		assertEquals("Update", vet.getLastName());
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Se puede dar de alta un veterinario y se le asigna un id.")
	@Test
	@Transactional
	void shouldInsertVet() {
		Collection<Vet> vets = (Collection<Vet>) this.vetService.findAll();
		int found = vets.size();

		Vet vet = new Vet();
		vet.setFirstName("Sam");
		vet.setLastName("Schultz");
		vet.setCity("Wollongong");
		User user = new User();
		user.setUsername("Sam");
		user.setPassword("supersecretpassword");
		user.setAuthority(authService.findByAuthority("VET"));
		vet.setUser(user);
		

		this.vetService.saveVet(vet);
		assertNotEquals(0, vet.getId().longValue());

		vets = (Collection<Vet>) this.vetService.findAll();
		assertEquals(found + 1, vets.size());
	}

	@Story("HU-08: Gestionar veterinarios")
	@Description("Se puede borrar un veterinario.")
	@Test
	@Transactional
	void shouldDeleteVet() throws DataAccessException, DuplicatedPetNameException {
		Integer firstCount = ((Collection<Vet>) this.vetService.findAll()).size();
		Vet vet = new Vet();
		vet.setFirstName("Sam");
		vet.setLastName("Schultz");
		vet.setCity("Wollongong");
		User user = new User();
		user.setUsername("Sam");
		user.setPassword("supersecretpassword");
		user.setAuthority(authService.findByAuthority("VET"));
		vet.setUser(user);
		this.vetService.saveVet(vet);

		Integer secondCount = ((Collection<Vet>) this.vetService.findAll()).size();
		assertEquals(firstCount + 1, secondCount);
		vetService.deleteVet(vet.getId());
		Integer lastCount = ((Collection<Vet>) this.vetService.findAll()).size();
		assertEquals(firstCount, lastCount);
	}

	// Specialties Tests

	@Story("HU-09: Gestionar especialidades")
	@Description("Se obtienen todas las especialidades.")
	@Test
	void shouldFindSpecialties() {
		Collection<Specialty> specialties = (Collection<Specialty>) this.vetService.findSpecialties();

		Specialty specialty = EntityUtils.getById(specialties, Specialty.class, 1);
		assertEquals("radiology", specialty.getName());
	}

	@Story("HU-09: Gestionar especialidades")
	@Description("Se encuentra una especialidad por su id.")
	@Test
	void shouldFindSingleSpecialty() {
		Specialty specialty = this.vetService.findSpecialtyById(1);
		assertEquals("radiology", specialty.getName());
	}

	@Story("HU-09: Gestionar especialidades")
	@Description("Buscar un id de especialidad inexistente lanza una excepción.")
	@Test
	void shouldNotFindSingleSpecialtyWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.vetService.findSpecialtyById(100));
	}

	@Story("HU-09: Gestionar especialidades")
	@Description("Se pueden modificar los datos de una especialidad.")
	@Test
	@Transactional
	void shouldUpdateSpecialty() {
		Specialty specialty = this.vetService.findSpecialtyById(1);
		specialty.setName("Change");
		vetService.updateSpecialty(specialty, 1);
		specialty = this.vetService.findSpecialtyById(1);
		assertEquals("Change", specialty.getName());
	}

	@Story("HU-09: Gestionar especialidades")
	@Description("Se puede dar de alta una especialidad y se le asigna un id.")
	@Test
	@Transactional
	void shouldInsertSpecialty() {
		Collection<Specialty> specialties = (Collection<Specialty>) this.vetService.findSpecialties();
		int found = specialties.size();

		Specialty specialty = new Specialty();
		specialty.setName("Vaccination");
		this.vetService.saveSpecialty(specialty);
		assertNotEquals(0, specialty.getId().longValue());

		specialties = (Collection<Specialty>) this.vetService.findSpecialties();
		assertEquals(found + 1, specialties.size());
	}

	@Story("HU-09: Gestionar especialidades")
	@Description("Se puede borrar una especialidad.")
	@Test
	@Transactional
	void shouldDeleteSpecialty() {
		Integer firstCount = ((Collection<Specialty>) this.vetService.findSpecialties()).size();
		Specialty specialty = new Specialty();
		specialty.setName("Vaccination");
		this.vetService.saveSpecialty(specialty);

		Integer secondCount = ((Collection<Specialty>) this.vetService.findSpecialties()).size();
		assertEquals(firstCount + 1, secondCount);
		vetService.deleteSpecialty(specialty.getId());
		Integer lastCount = ((Collection<Specialty>) this.vetService.findSpecialties()).size();
		assertEquals(firstCount, lastCount);
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Se calculan las estadísticas de veterinarios para el administrador.")
	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	void shouldReturnStatsForAdmin() {
		Map<String, Object> stats = this.vetService.getVetsStats();
		assertTrue(stats.containsKey("totalVets"));
		assertEquals(6, stats.get("totalVets"));
		assertTrue(stats.containsKey("vetsBySpecialty"));
		assertEquals(2, ((Map<String, Integer>) stats.get("vetsBySpecialty")).get("surgery"));
		assertTrue(stats.containsKey("vetsByCity"));
		assertEquals(3, ((Map<String, Integer>) stats.get("vetsByCity")).get("Sevilla"));
		assertTrue(stats.containsKey("visitsByVet"));
		assertEquals(3, ((Map<String, Integer>) stats.get("visitsByVet")).get("James Carter"));
	}

}
