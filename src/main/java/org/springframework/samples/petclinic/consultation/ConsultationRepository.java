package org.springframework.samples.petclinic.consultation;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ConsultationRepository extends CrudRepository<Consultation, Integer> {

	@Query("SELECT c FROM Consultation c WHERE c.owner.id = :ownerId ORDER BY c.creationDate DESC")
	public List<Consultation> findConsultationsByOwner(@Param("ownerId") int ownerId);

//	@Query("SELECT c FROM Consultation c JOIN Ticket t WHERE t.consultation.id = c.id AND t.user.id = :userId ORDER BY c.creationDate DESC")
//	public List<Consultation> findConsultationsByUser(@Param("userId") int userId);

	public Iterable<Consultation> findAllByOrderByCreationDateDesc();

//	@Query("SELECT c FROM Consultation c JOIN Vet v WHERE c.id = v.consul ORDER BY c.creationDate DESC")
//	public List<Consultation> findConsultationsByVet(@Param("vetId") int vetId);

	// STATS
	// ADMIN
	@Query("SELECT COUNT(c) FROM Consultation c")
	public Integer countAll();

	@Query("SELECT COUNT(o) FROM Owner o WHERE o.clinic.plan = 'PLATINUM'")
	public Integer countAllPlatinums();

	@Query("SELECT COUNT(o) FROM Owner o")
	public Integer countAllOwners();

	// OWNER
	@Query("SELECT COUNT(c) FROM Consultation c WHERE c.owner.id = :ownerId")
	public Integer countAllByOwner(int ownerId);

	@Query("SELECT COUNT(p) FROM Pet p WHERE p.owner.id = :ownerId")
	public Integer countAllPetsOfOwner(int ownerId);

	@Query("SELECT NEW MAP(c.pet.name as pet, cast(COUNT(c) as string) as consultations)"
			+ " FROM  Consultation c WHERE c.owner.id = :ownerId GROUP BY c.pet")
	public List<Map<String, String>> countConsultationsGroupedByPet(int ownerId);

	@Query("SELECT MIN(YEAR(c.creationDate)) FROM Consultation c WHERE c.owner.id = :ownerId")
	public Integer getYearOfFirstConsultation(int ownerId);

	@Query("SELECT NEW MAP(YEAR(c.creationDate) as year, cast(COUNT(c) as integer) as consultations)"
			+ " FROM  Consultation c WHERE c.owner.id = :ownerId GROUP BY YEAR(c.creationDate)")
	public List<Map<String, Integer>> countConsultationsGroupedByYear(int ownerId);

	@Query("SELECT c FROM Consultation c WHERE c.owner.clinic.clinicOwner.user.id = :userId")
	public List<Consultation> findAllByClinicOwnerUserId(int userId);

	@Query("SELECT c FROM Consultation c WHERE c.owner.clinic.id = :clinicId")
	public List<Consultation> findAllByClinicId(int clinicId);

}
