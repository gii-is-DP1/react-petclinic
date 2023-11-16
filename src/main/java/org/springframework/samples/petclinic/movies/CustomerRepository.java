package org.springframework.samples.petclinic.movies;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer,Integer>{
    Customer findByName(String nombre);
}
