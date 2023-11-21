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
    public double getCharge(int daysRented) {
        double result = 0;
        //determine amounts for each line
            if (priceCode.equals(PriceCode.REGULAR)) {
                result += 2;
                if (daysRented > 2)
                    result += (daysRented - 2) * 1.5;             
            } else if (priceCode.equals(PriceCode.NEW_RELEASE)) {
                result += daysRented * 3; 
            } else if (priceCode.equals(PriceCode.CHILDRENS)) {
                result += 1.5;
                if (daysRented > 3)
                    result += (daysRented - 3) * 1.5;             
            }
        return result;
    }

    public int getFrequentRenterPoints(int daysRented){
        if ((priceCode == PriceCode.NEW_RELEASE) && daysRented > 1) 
            return 2;
        else
            return 1;
    }
}
