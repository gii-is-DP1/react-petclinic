package org.springframework.samples.petclinic.clinicowner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ClinicOwnerRepository extends CrudRepository<ClinicOwner, Integer> {

    @Query("SELECT clinicOwner FROM ClinicOwner clinicOwner WHERE clinicOwner.user.id = :userId")
    Optional<ClinicOwner> findByUserId(int userId);

}
