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
 * Marks a test of the repository (persistence) layer.
 * <p>
 * Adds the Allure label "layer=repository" and the JUnit tag "repository".
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@LabelAnnotation(name = "layer", value = "repository")
@Tag("repository")
public @interface RepositoryTest {
}
