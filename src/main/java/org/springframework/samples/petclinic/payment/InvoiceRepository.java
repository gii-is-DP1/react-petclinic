package org.springframework.samples.petclinic.payment;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {
    List<Invoice> findAll();
    
}
