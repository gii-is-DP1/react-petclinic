package org.springframework.samples.petclinic.movies;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RentalInfo extends BaseEntity {

    @ManyToOne
    private Movie movie;

    @Transient
    private Double amount;
    
}
