package org.springframework.samples.petclinic.disease;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TreatmentRepository extends CrudRepository<Treatment, Integer> {

    Optional<Treatment> findById(Integer i);

    List<Treatment> findAll();

    Treatment save(Treatment any);
    
}
