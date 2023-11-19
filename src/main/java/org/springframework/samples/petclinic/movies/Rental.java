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
public class Rental extends BaseEntity{

    private int daysRented;

    @ManyToOne
    private Movie movie;

    @Transient
    private Double amount;

    double getCharge(){
        double result = 0;
        //determine amounts for each line
            if (movie.getPriceCode().equals(PriceCode.REGULAR)) {
                result += 2;
                if (getDaysRented() > 2)
                    result += (getDaysRented() - 2) * 1.5;             
            } else if (movie.getPriceCode().equals(PriceCode.NEW_RELEASE)) {
                result += getDaysRented() * 3; 
            } else if (movie.getPriceCode().equals(PriceCode.CHILDRENS)) {
                result += 1.5;
                if (getDaysRented() > 3)
                    result += (getDaysRented() - 3) * 1.5;             
            }
        return result;
    }

    int getFrequentRenterPoints(){
        if ((movie.getPriceCode() == PriceCode.NEW_RELEASE) && daysRented > 1) 
            return 2;
        else
            return 1;
    }
}
