package org.springframework.samples.petclinic.movies;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Movie extends BaseEntity {

    private String title; 
    private PriceCode priceCode;
}
