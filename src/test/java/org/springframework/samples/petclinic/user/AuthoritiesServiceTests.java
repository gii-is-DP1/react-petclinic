package org.springframework.samples.petclinic.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.ServiceTest;
import org.springframework.samples.petclinic.testing.SociableTest;

@Epic("Users & Admin Module")
@Feature("Authorization")
@Owner("DP1-tutors")
@ServiceTest
@SociableTest
@SpringBootTest
@AutoConfigureTestDatabase
class AuthoritiesServiceTests {

//	@Autowired
//	private UserService userService;

	@Autowired
	private AuthoritiesService authService;

	@Story("HU-03: Gestionar usuarios")
	@Description("Se obtienen todos los roles (authorities) existentes.")
	@Test
	void shouldFindAllAuthorities() {
		List<Authorities> auths = (List<Authorities>) this.authService.findAll();
		assertEquals(4, auths.size());
	}

	@Story("HU-03: Gestionar usuarios")
	@Description("Se encuentra un rol por su nombre.")
	@Test
	void shouldFindAuthoritiesByAuthority() {
		Authorities auth = this.authService.findByAuthority("VET");
		assertEquals("VET", auth.getAuthority());
	}

	@Story("HU-03: Gestionar usuarios")
	@Description("Buscar un rol inexistente lanza una excepción.")
	@Test
	void shouldNotFindAuthoritiesByIncorrectAuthority() {
		assertThrows(ResourceNotFoundException.class, () -> this.authService.findByAuthority("authnotexists"));
	}

	@Story("HU-03: Gestionar usuarios")
	@Description("Se puede dar de alta un rol nuevo.")
	@Test
	@Transactional
	void shouldInsertAuthorities() {
		int count = ((Collection<Authorities>) this.authService.findAll()).size();

		Authorities auth = new Authorities();
		auth.setAuthority("CLIENT");

		this.authService.saveAuthorities(auth);
		assertNotEquals(0, auth.getId().longValue());
		assertNotNull(auth.getId());

		int finalCount = ((Collection<Authorities>) this.authService.findAll()).size();
		assertEquals(count + 1, finalCount);
	}

//	@Test
//	@Transactional
//	void shouldAddAuthoritiesToUser() {
//		User user = userService.findUser("owner1");
//		assertEquals("OWNER" ,user.getAuthority().getAuthority());
//
//		this.authService.saveAuthorities("owner1", "TEST");
//		assertEquals("TEST" ,user.getAuthority().getAuthority());
//
//	}

}
