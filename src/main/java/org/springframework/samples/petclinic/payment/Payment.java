package org.springframework.samples.petclinic.payment;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.DiscriminatorType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_method", discriminatorType = DiscriminatorType.STRING)
public abstract class Payment extends BaseEntity{
    
}
