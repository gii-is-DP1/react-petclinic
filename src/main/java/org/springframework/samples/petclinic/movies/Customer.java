package org.springframework.samples.petclinic.movies;

import java.util.List;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends BaseEntity {

    @OneToMany
    private List<Rental> rentals;

    private String name;
    
}
