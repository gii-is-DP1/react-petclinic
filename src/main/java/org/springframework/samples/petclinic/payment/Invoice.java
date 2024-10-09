package org.springframework.samples.petclinic.payment;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.visit.Visit;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Invoice extends BaseEntity{        
        
        String concept;

        
        String paymentMethod;
        
        @DecimalMin("0.0")
        Double subtotal;
        
        @DecimalMin("0.0")
        Double tax;

        @Transient
        public Double getTotal(){
            return subtotal + tax;
        }   
}
