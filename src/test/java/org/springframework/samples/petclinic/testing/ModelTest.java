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
 * Marks a test of the domain model (entities, validation rules).
 * <p>
 * Adds the Allure label "layer=model" and the JUnit tag "model".
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@LabelAnnotation(name = "layer", value = "model")
@Tag("model")
public @interface ModelTest {
}
