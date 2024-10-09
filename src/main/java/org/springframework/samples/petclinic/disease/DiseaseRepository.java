package org.springframework.samples.petclinic.disease;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.pet.PetType;

public interface DiseaseRepository extends CrudRepository<Disease, Integer> {
    @Query("SELECT DISTINCT v.diagnose FROM Visit v WHERE v.diagnose!=null AND v.pet.type IN :petTypes and v.datetime>=:startDate and v.datetime<=:endDate GROUP BY v.diagnose HAVING count(v)>=:diagnoses")
    Set<Disease> findEpidemicDiseases(@Param("petTypes") Set<PetType> petTypes,
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate, 
                                        @Param("diagnoses")Integer diagnoses);


    List<Disease> findAll();
}
