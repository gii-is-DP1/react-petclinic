package org.springframework.samples.petclinic.movies;

public abstract class Price {
    public abstract PriceCode getPriceCode();
    public abstract double getCharged(int daysRented);    
    public int getFrequentRenterPoints(int daysRented) {
        return 1;
    }
}
