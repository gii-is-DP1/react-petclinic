package org.springframework.samples.petclinic.testing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

import io.qameta.allure.LabelAnnotation;

/**
 * Marks an isolated (solitary) test: collaborators of the class under test are replaced by mocks or stubs.
 * <p>
 * Adds the Allure label "isolation=isolated" and the JUnit tag "isolated".
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@LabelAnnotation(name = "isolation", value = "isolated")
@Tag("isolated")
public @interface IsolatedTest {
}
