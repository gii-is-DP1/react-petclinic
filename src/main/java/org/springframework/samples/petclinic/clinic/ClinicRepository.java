package org.springframework.samples.petclinic.clinic;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.Vet;

public interface ClinicRepository extends CrudRepository<Clinic, Integer> {

    @Query("SELECT o FROM Owner o WHERE o.clinic.clinicOwner.user.id = :userId")
    List<Owner> findOwnersOfUserClinics(int userId);

    @Query("SELECT v FROM Vet v WHERE v.clinic.clinicOwner.user.id = :userId")
    List<Vet> findVetsOfUserClinics(int userId);

    @Query("SELECT c FROM Clinic c WHERE c.clinicOwner.user.id = :userId")
    List<Clinic> findClinicsByUserId(int userId);
}
