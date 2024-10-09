package org.springframework.samples.petclinic.disease;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymptomRepository extends CrudRepository<Symptom, Integer> {

    Optional<Symptom> findById(Integer i);

    List<Symptom> findAll();

    Symptom save(Symptom any);
    
}
