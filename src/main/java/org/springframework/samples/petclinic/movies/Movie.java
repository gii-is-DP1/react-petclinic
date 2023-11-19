package org.springframework.samples.petclinic.movies;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Movie extends BaseEntity {

    private String title; 
    
    @Convert(converter = ConverterPrice.class)
    private Price price;

    double getCharge(int daysRented){
        return price.getCharged(daysRented);
    }

    int getFrequentRenterPoints(int daysRented){
        return price.getFrequentRenterPoints(daysRented);
    }

    @Transient
    public PriceCode getPriceCode() {
        return getPrice().getPriceCode();
    }
}
