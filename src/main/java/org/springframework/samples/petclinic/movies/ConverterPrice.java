package org.springframework.samples.petclinic.movies;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;

@Convert
public class ConverterPrice implements AttributeConverter<Price, String> {

    @Override
    public String convertToDatabaseColumn(Price attribute) {
        return attribute.getPriceCode().toString();
    }

    @Override
    public Price convertToEntityAttribute(String dbData) {
        if (dbData.equals("CHILDRENS")) {
            return new ChildrensPrice();
        } else if (dbData.equals("NEW_RELEASE")) {
            return new NewReleasePrice();
        } else {
            return new RegularPrice();
        }
    }

}