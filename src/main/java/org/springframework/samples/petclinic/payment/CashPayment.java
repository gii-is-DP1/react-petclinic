package org.springframework.samples.petclinic.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Cash")
public class CashPayment extends Payment{
    double amountTendered;
    double change;
}
