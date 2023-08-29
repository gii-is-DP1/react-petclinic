package org.springframework.samples.petclinic.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerService;
import org.springframework.samples.petclinic.vet.VetService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTests {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthoritiesService authService;

	@Autowired
	private VetService vetService;

	@Autowired
	private OwnerService ownerService;

	@Test
	@WithMockUser(username = "owner1", password = "0wn3r")
	void shouldFindCurrentUser() {
		User user = this.userService.findCurrentUser();
		assertEquals("owner1", user.getUsername());
	}

	@Test
	@WithMockUser(username = "prueba")
	void shouldNotFindCorrectCurrentUser() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
	}

	@Test
	void shouldNotFindAuthenticated() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
	}

	@Test
	void shouldFindAllUsers() {
		List<User> users = (List<User>) this.userService.findAll();
		assertEquals(19, users.size());
	}

	@Test
	void shouldFindUsersByUsername() {
		User user = this.userService.findUser("owner1");
		assertEquals("owner1", user.getUsername());
	}

	@Test
	void shouldFindUsersByAuthority() {
		List<User> owners = (List<User>) this.userService.findAllByAuthority("OWNER");
		assertEquals(10, owners.size());

		List<User> admins = (List<User>) this.userService.findAllByAuthority("ADMIN");
		assertEquals(1, admins.size());

		List<User> vets = (List<User>) this.userService.findAllByAuthority("VET");
		assertEquals(6, vets.size());
	}

	@Test
	void shouldNotFindUserByIncorrectUsername() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser("usernotexists"));
	}

	@Test
	void shouldFindSingleOwnerByUsername() {
		Owner owner = this.userService.findOwnerByUser("owner1");
		assertEquals("owner1", owner.getUser().getUsername());
	}

	@Test
	void shouldNotFindSingleOwnerWithBadUsername() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findOwnerByUser("badusername"));
	}

	@Test
	void shouldFindSingleOwnerByUserId() {
		Owner owner = this.userService.findOwnerByUser(4);
		assertEquals("owner1", owner.getUser().getUsername());
	}

	@Test
	void shouldNotFindSingleUserOwnerWithBadUserId() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findOwnerByUser(100));
	}

	@Test
	void shouldFindSingleUser() {
		User user = this.userService.findUser(4);
		assertEquals("owner1", user.getUsername());
	}

	@Test
	void shouldNotFindSingleUserWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser(100));
	}

	@Test
	void shouldExistUser() {
		assertEquals(true, this.userService.existsUser("owner1"));
	}

	@Test
	void shouldNotExistUser() {
		assertEquals(false, this.userService.existsUser("owner10000"));
	}

	@Test
	@Transactional
	void shouldUpdateUser() {
		User user = this.userService.findUser(2);
		user.setUsername("Change");
		userService.updateUser(user, 2);
		user = this.userService.findUser(2);
		assertEquals("Change", user.getUsername());
	}

	@Test
	@Transactional
	void shouldInsertUser() {
		int count = ((Collection<User>) this.userService.findAll()).size();

		User user = new User();
		user.setUsername("Sam");
		user.setPassword("password");
		user.setAuthority(authService.findByAuthority("ADMIN"));

		this.userService.saveUser(user);
		assertNotEquals(0, user.getId().longValue());
		assertNotNull(user.getId());

		int finalCount = ((Collection<User>) this.userService.findAll()).size();
		assertEquals(count + 1, finalCount);
	}
	

	@Test
	@Transactional
	void shouldDeleteUserWithoutOwner() {
		Integer firstCount = ((Collection<User>) userService.findAll()).size();
		User user = new User();
		user.setUsername("Sam");
		user.setPassword("password");
		Authorities auth = authService.findByAuthority("OWNER");
		user.setAuthority(auth);
		this.userService.saveUser(user);

		Integer secondCount = ((Collection<User>) userService.findAll()).size();
		assertEquals(firstCount + 1, secondCount);
		userService.deleteUser(user.getId());
		Integer lastCount = ((Collection<User>) userService.findAll()).size();
		assertEquals(firstCount, lastCount);
	}

//	@Test
//	@Transactional
//	void shouldDeleteUserWithOwner() {
//		Integer firstCount = ((Collection<User>) userService.findAll()).size();
//		User user = new User();
//		user.setUsername("Sam");
//		user.setPassword("password");
//		Authorities auth = authService.findByAuthority("OWNER");
//		user.setAuthority(auth);
//		Owner owner = new Owner();
//		owner.setAddress("Test");
//		owner.setFirstName("Test");
//		owner.setLastName("Test");
//		owner.setPlan(PricingPlan.BASIC);
//		owner.setTelephone("999999999");
//		owner.setUser(user);
//		owner.setCity("Test");
//		this.ownerService.saveOwner(owner);
//
//		Integer secondCount = ((Collection<User>) userService.findAll()).size();
//		assertEquals(firstCount + 1, secondCount);
//		userService.deleteUser(user.getId());
//		Integer lastCount = ((Collection<User>) userService.findAll()).size();
//		assertEquals(firstCount, lastCount);
//	}

	@Test
	@Transactional
	void shouldDeleteUserWithoutVet() {
		Integer firstCount = ((Collection<User>) userService.findAll()).size();
		User user = new User();
		user.setUsername("Sam");
		user.setPassword("password");
		Authorities auth = authService.findByAuthority("VET");
		user.setAuthority(auth);
		this.userService.saveUser(user);

		Integer secondCount = ((Collection<User>) userService.findAll()).size();
		assertEquals(firstCount + 1, secondCount);
		userService.deleteUser(user.getId());
		Integer lastCount = ((Collection<User>) userService.findAll()).size();
		assertEquals(firstCount, lastCount);
	}

//	@Test
//	@Transactional
//	void shouldDeleteUserWithVet() {
//		Integer firstCount = ((Collection<User>) userService.findAll()).size();
//		User user = new User();
//		user.setUsername("Sam");
//		user.setPassword("password");
//		Authorities auth = authService.findByAuthority("VET");
//		user.setAuthority(auth);
//		userService.saveUser(user);
//		Vet vet = new Vet();
//		vet.setFirstName("Test");
//		vet.setLastName("Test");
//		vet.setUser(user);
//		vet.setCity("Test");
//		this.vetService.saveVet(vet);
//
//		Integer secondCount = ((Collection<User>) userService.findAll()).size();
//		assertEquals(firstCount + 1, secondCount);
//		userService.deleteUser(user.getId());
//		Integer lastCount = ((Collection<User>) userService.findAll()).size();
//		assertEquals(firstCount, lastCount);
//	}

}
