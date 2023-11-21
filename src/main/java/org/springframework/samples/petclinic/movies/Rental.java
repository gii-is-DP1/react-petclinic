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

    public double getCharge() {
        double result = 0;
        //determine amounts for each line
            if (getInfo().getMovie().getPriceCode().equals(PriceCode.REGULAR)) {
                result += 2;
                if (getDaysRented() > 2)
                    result += (getDaysRented() - 2) * 1.5;             
            } else if (getInfo().getMovie().getPriceCode().equals(PriceCode.NEW_RELEASE)) {
                result += getDaysRented() * 3; 
            } else if (getInfo().getMovie().getPriceCode().equals(PriceCode.CHILDRENS)) {
                result += 1.5;
                if (getDaysRented() > 3)
                    result += (getDaysRented() - 3) * 1.5;             
            }
        return result;
    } 
}
