package org.springframework.samples.petclinic.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
@Entity
@DiscriminatorValue("CreditCard")
public class CreditCartdPayment extends Payment{
    String cardNumber;
    String cardHolder;
    String expirationDate;
    String cvv;
    CardType cardType;    
}
