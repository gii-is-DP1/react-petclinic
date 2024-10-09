package org.springframework.samples.petclinic.disease;

import java.util.Set;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class Symptom extends NamedEntity{
    @Pattern(regexp = "^(LOW)|(MEDIUM)|(HIGH)$")
    String virulence;
    
    @ManyToMany
    Set<Disease> includes;
    
    @ManyToMany
    Set<Disease> excludes;
}
