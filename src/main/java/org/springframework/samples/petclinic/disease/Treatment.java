package org.springframework.samples.petclinic.disease;

import java.util.Set;

import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
@EqualsAndHashCode( of = {"id"})
public class Treatment extends NamedEntity {
    
    String description;
    @Min(1)
    @NotNull
    Integer baseDose;
    @Min(1)
    Integer shockDose;
    @Min(1)
    @NotNull
    Integer maxDose;

    @ManyToMany
    Set<Disease> recommendedFor;
}
