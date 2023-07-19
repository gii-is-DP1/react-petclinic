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

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
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

	@Test
	void shouldFindVets() {
		Collection<Vet> vets = (Collection<Vet>) this.vetService.findAll();

		Vet vet = EntityUtils.getById(vets, Vet.class, 3);
		assertEquals("Douglas", vet.getLastName());
		assertEquals(2, vet.getSpecialties().size());
		assertEquals("surgery", vet.getSpecialties().get(0).getName());
		assertEquals("dentistry", vet.getSpecialties().get(1).getName());
	}

	@Test
	void shouldFindSingleVet() {
		Vet vet = this.vetService.findVetById(1);
		assertThat(vet.getLastName()).startsWith("Carter");
		assertEquals("Sevilla", vet.getCity());
		assertEquals(0, vet.getSpecialties().size());
	}

	@Test
	void shouldNotFindSingleVetWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.vetService.findVetById(100));
	}

	@Test
	void shouldFindVetByUser() {
		Vet vet = this.vetService.findVetByUser(14);
		assertThat(vet.getLastName()).startsWith("Carter");
	}

	@Test
	void shouldNotFindVetByIncorrectUser() {
		assertThrows(ResourceNotFoundException.class, () -> this.vetService.findVetByUser(34));
	}

	@Test
	void shouldFindOptVetByUser() {
		Optional<Vet> vet = this.vetService.optFindVetByUser(14);
		assertThat(vet.get().getLastName()).startsWith("Carter");
	}

	@Test
	void shouldNotFindOptVetByIncorrectUser() {
		assertThat(this.vetService.optFindVetByUser(25)).isEmpty();
	}

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

	@Test
	void shouldFindSpecialties() {
		Collection<Specialty> specialties = (Collection<Specialty>) this.vetService.findSpecialties();

		Specialty specialty = EntityUtils.getById(specialties, Specialty.class, 1);
		assertEquals("radiology", specialty.getName());
	}

	@Test
	void shouldFindSingleSpecialty() {
		Specialty specialty = this.vetService.findSpecialtyById(1);
		assertEquals("radiology", specialty.getName());
	}

	@Test
	void shouldNotFindSingleSpecialtyWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.vetService.findSpecialtyById(100));
	}

	@Test
	@Transactional
	void shouldUpdateSpecialty() {
		Specialty specialty = this.vetService.findSpecialtyById(1);
		specialty.setName("Change");
		vetService.updateSpecialty(specialty, 1);
		specialty = this.vetService.findSpecialtyById(1);
		assertEquals("Change", specialty.getName());
	}

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
