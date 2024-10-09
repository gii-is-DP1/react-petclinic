package org.springframework.samples.petclinic.disease;

import java.util.Set;

import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.pet.PetType;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Disease extends NamedEntity {
    String description;
    @Min(1)
    @Max(5)
    @NotNull
    Integer severity;
    @ManyToMany
    Set<PetType> susceptiblePetTypes;
}
