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
 * Marks a sociable test: the class under test runs with its real collaborators (other beans, database...).
 * <p>
 * Adds the Allure label "isolation=sociable" and the JUnit tag "sociable".
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@LabelAnnotation(name = "isolation", value = "sociable")
@Tag("sociable")
public @interface SociableTest {
}
