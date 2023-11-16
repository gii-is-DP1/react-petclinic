package org.springframework.samples.petclinic.movies;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatementDTO {
    
    @NotNull
    Customer customer;

    @NotNull
    @PositiveOrZero
    Double totalAmount;

    @NotNull
    @PositiveOrZero
    Integer frequentRenterPoints;

    public StatementDTO(){}

    public StatementDTO(Customer c, Double ta, Integer frp) {
        this.customer = c;
        this.totalAmount = ta;
        this.frequentRenterPoints = frp;
    }
}
