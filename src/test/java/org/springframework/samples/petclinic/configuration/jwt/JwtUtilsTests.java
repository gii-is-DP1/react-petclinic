package org.springframework.samples.petclinic.configuration.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.test.util.ReflectionTestUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.IsolatedTest;
import org.springframework.samples.petclinic.testing.Layer;

@Epic("Users & Admin Module")
@Feature("Authentication")
@io.qameta.allure.Owner("DP1-tutors")
@Layer("security")
@IsolatedTest
class JwtUtilsTests {

	private static final String TEST_SECRET =
			"cGV0Y2xpbmljLWRldmVsb3BtZW50LWtleS0yMDI2LTIwMjctY2hhbmdlLXRoaXMtaW4tcHJvZHVjdGlvbiEhISEhISE=";

	private JwtUtils jwtUtils;

	@BeforeEach
	void setUp() {
		jwtUtils = new JwtUtils();
		ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TEST_SECRET);
		ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60_000);
	}

	@Story("HU-01: Iniciar sesión")
	@Description("Un token generado para un usuario es válido y contiene su nombre de usuario.")
	@Test
	void shouldGenerateAndValidateToken() {
		Authorities authority = new Authorities();
		authority.setAuthority("ADMIN");

		String token = jwtUtils.generateTokenFromUsername("admin", authority);

		assertTrue(jwtUtils.validateJwtToken(token));
		assertEquals("admin", jwtUtils.getUserNameFromJwtToken(token));
	}

	@Story("HU-01: Iniciar sesión")
	@Description("Un token mal formado no es válido.")
	@Test
	void shouldRejectMalformedToken() {
		assertFalse(jwtUtils.validateJwtToken("not-a-jwt"));
	}
}
