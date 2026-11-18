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
package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetService;
import org.springframework.samples.petclinic.pet.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.transaction.annotation.Transactional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.ServiceTest;
import org.springframework.samples.petclinic.testing.SociableTest;


@Epic("Clinic Module")
@Feature("Owner management")
@io.qameta.allure.Owner("DP1-tutors")
//@DataJpaTest(includeFilters = {@ComponentScan.Filter(Service.class),@ComponentScan.Filter(PasswordEncoder.class)})
@ServiceTest
@SociableTest
@SpringBootTest
@AutoConfigureTestDatabase
public class OwnerServiceTests {

	private OwnerService ownerService;
	private PetService petService;
	private AuthoritiesService authService;


	@Autowired
	public OwnerServiceTests(OwnerService ownerService, PetService petService, AuthoritiesService authService) {
		this.ownerService = ownerService;
		this.petService = petService;
		this.authService = authService;
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Se obtienen todos los propietarios.")
	@Test
	void shouldFindAllOwners() {
		List<Owner> owners = (List<Owner>) this.ownerService.findAll();
		assertEquals(10, owners.size());
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Se encuentran los propietarios por su apellido.")
	@Test
	void shouldFindOwnersByLastName() {
		Collection<Owner> owners = this.ownerService.findOwnerByLastName("Davis");
		assertEquals(2, owners.size());

		owners = this.ownerService.findOwnerByLastName("Daviss");
		assertThat(owners.isEmpty()).isTrue();
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Se encuentra un propietario por su id, con sus datos completos.")
	@Test
	void shouldFindSingleOwnerWithPet() {
		Owner owner = this.ownerService.findOwnerById(1);
		List<Pet> pets = petService.findAllPetsByOwnerId(owner.getId());
		assertThat(owner.getLastName()).startsWith("Franklin");
		assertEquals(1, pets.size());
		assertNotNull(pets.get(0).getType());
		assertEquals("cat", pets.get(0).getType().getName());
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Buscar un id de propietario inexistente lanza una excepción.")
	@Test
	void shouldNotFindSingleOwnerWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.ownerService.findOwnerById(100));
	}

//	@Test
//	void shouldFindOwnerByUser() {
//		Owner owner = this.ownerService.findOwnerByUser(2);
//		assertEquals("Franklin", owner.getLastName());
//	}
//
//	@Test
//	void shouldNotFindOwnerByIncorrectUser() {
//		assertThrows(ResourceNotFoundException.class, () -> this.ownerService.findOwnerByUser(34));
//	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Se encuentra el propietario asociado a un usuario.")
	@Test
	void shouldFindOptOwnerByUser() {
		Optional<Owner> owner = this.ownerService.optFindOwnerByUser(4);
		assertEquals("Franklin", owner.get().getLastName());
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Un usuario que no es propietario no tiene propietario asociado.")
	@Test
	void shouldNotFindOptOwnerByIncorrectUser() {
		assertThat(this.ownerService.optFindOwnerByUser(25)).isEmpty();
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Se pueden modificar los datos de un propietario.")
	@Test
	@Transactional
	void shouldUpdateOwner() {
		Owner owner = this.ownerService.findOwnerById(1);
		owner.setAddress("Change");
		owner.setLastName("Update");
		ownerService.updateOwner(owner, 1);
		owner = this.ownerService.findOwnerById(1);
		assertEquals("Change", owner.getAddress());
		assertEquals("Update", owner.getLastName());
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Se puede dar de alta un propietario y se le asigna un id.")
	@Test
	@Transactional
	void shouldInsertOwner() {
		int initialCount = ((Collection<Owner>) this.ownerService.findAll()).size();

		Owner owner = createOwnerUser();
		assertNotEquals(0, owner.getId().longValue());

		int finalCount = ((Collection<Owner>) this.ownerService.findAll()).size();
		assertEquals(initialCount + 1, finalCount);
	}

	private Owner createOwnerUser() {
		Owner owner = new Owner();
		owner.setFirstName("Sam");
		owner.setLastName("Apellido");
		owner.setAddress("4, Evans Street");
		owner.setCity("Wollongong");
		owner.setTelephone("444444444");
		User user = new User();
		user.setUsername("Sam");
		user.setPassword("supersecretpassword");
		user.setAuthority(authService.findByAuthority("OWNER"));
		owner.setUser(user);
		return this.ownerService.saveOwner(owner);
	}

	@Story("HU-12: Consultar estadísticas")
	@Description("Se calculan las estadísticas de propietarios para el administrador.")
	@Test
	@Transactional
	void shouldReturnStatsForAdmin() {
		Map<String, Object> stats = this.ownerService.getOwnersStats();
		assertTrue(stats.containsKey("totalOwners"));
		assertEquals(((Collection<Owner>) ownerService.findAll()).size(), stats.get("totalOwners"));
		assertTrue(stats.containsKey("basicOwners"));
		assertEquals(3, stats.get("basicOwners"));
		assertTrue(stats.containsKey("goldOwners"));
		assertEquals(3, stats.get("goldOwners"));
		assertTrue(stats.containsKey("platinumOwners"));
		assertEquals(4, stats.get("platinumOwners"));
		assertTrue(stats.containsKey("ownersVisits"));
	}

}
