package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.springframework.samples.petclinic.testing.IsolatedTest;
import org.springframework.samples.petclinic.testing.ModelTest;

/**
 * @author Michael Isvy Simple test to make sure that Bean Validation is working (useful
 * when upgrading to a new version of Hibernate Validator/ Bean Validation)
 */
@Epic("Miscelaneous tets")
@Feature("Validation")
@Owner("DP1-tutors")
@ModelTest
@IsolatedTest
class ValidatorTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Story("HU-04: Gestionar propietarios")
	@Description("Una persona sin nombre no supera la validación del modelo.")
	@Test
	void shouldNotValidateWhenFirstNameEmpty() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Person person = new Person();
		person.setFirstName("");
		person.setLastName("smith");

		Validator validator = createValidator();
		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

		assertThat(constraintViolations.size()).isEqualTo(1);
		ConstraintViolation<Person> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

}
