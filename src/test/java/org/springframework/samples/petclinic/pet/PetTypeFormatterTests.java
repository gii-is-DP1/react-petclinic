package org.springframework.samples.petclinic.pet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.IsolatedTest;
import org.springframework.samples.petclinic.testing.Layer;

/**
 * Test class for {@link PetTypeFormatter}
 *
 * @author Colin But
 */

@Epic("Clinic Module")
@Feature("Pet management")
@io.qameta.allure.Owner("DP1-tutors")
@Layer("formatter")
@IsolatedTest
@ExtendWith(MockitoExtension.class)
class PetTypeFormatterTests {

	@Mock
	private PetService clinicService;

	private PetTypeFormatter petTypeFormatter;

	@BeforeEach
	void setup() {
		petTypeFormatter = new PetTypeFormatter(clinicService);
	}

	@Story("HU-06: Gestionar mascotas")
	@Description("Un tipo de mascota se muestra como texto con su nombre.")
	@Test
	void testPrint() {
		PetType petType = new PetType();
		petType.setName("Hamster");
		String petTypeName = petTypeFormatter.print(petType, Locale.ENGLISH);
		assertEquals("Hamster", petTypeName);
	}

	@Story("HU-06: Gestionar mascotas")
	@Description("El nombre de un tipo de mascota se convierte en el tipo correspondiente.")
	@Test
	void shouldParse() throws ParseException {
		Mockito.when(clinicService.findPetTypes()).thenReturn(makePetTypes());
		PetType petType = petTypeFormatter.parse("Bird", Locale.ENGLISH);
		assertEquals("Bird", petType.getName());
	}

	@Story("HU-06: Gestionar mascotas")
	@Description("Convertir un nombre de tipo de mascota inexistente lanza una excepción.")
	@Test
	void shouldThrowParseException() throws ParseException {
		Mockito.when(clinicService.findPetTypes()).thenReturn(makePetTypes());
		Assertions.assertThrows(ParseException.class, () -> {
			petTypeFormatter.parse("Fish", Locale.ENGLISH);
		});
	}

	/**
	 * Helper method to produce some sample pet types just for test purpose
	 * @return {@link Collection} of {@link PetType}
	 */
	private Collection<PetType> makePetTypes() {
		Collection<PetType> petTypes = new ArrayList<>();
		petTypes.add(new PetType() {
			{
				setName("Dog");
			}
		});
		petTypes.add(new PetType() {
			{
				setName("Bird");
			}
		});
		return petTypes;
	}

}
