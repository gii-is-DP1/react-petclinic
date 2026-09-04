package org.springframework.samples.petclinic.configuration.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.test.util.ReflectionTestUtils;

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

	@Test
	void shouldGenerateAndValidateToken() {
		Authorities authority = new Authorities();
		authority.setAuthority("ADMIN");

		String token = jwtUtils.generateTokenFromUsername("admin", authority);

		assertTrue(jwtUtils.validateJwtToken(token));
		assertEquals("admin", jwtUtils.getUserNameFromJwtToken(token));
	}

	@Test
	void shouldRejectMalformedToken() {
		assertFalse(jwtUtils.validateJwtToken("not-a-jwt"));
	}
}
