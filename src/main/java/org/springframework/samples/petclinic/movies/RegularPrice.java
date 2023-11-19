package org.springframework.samples.petclinic.movies;

public class RegularPrice extends Price {

    @Override
    public PriceCode getPriceCode() {
        return PriceCode.REGULAR;
    }

    @Override
    public double getCharged(int daysRented) {
        double result = 2;
        if (daysRented > 2)
            result += (daysRented - 2) * 1.5;             

        return result;
    }   

    @Override
    public int getFrequentRenterPoints(int daysRented) {
        return 1;
    }
}