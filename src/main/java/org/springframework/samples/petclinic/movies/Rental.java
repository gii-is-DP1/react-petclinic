package org.springframework.samples.petclinic.movies;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Rental extends BaseEntity{
    
    @OneToOne
    private RentalInfo info; 

    private int daysRented; 
}
