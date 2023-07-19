package org.springframework.samples.petclinic.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.vet.Vet;

public interface UserRepository extends  CrudRepository<User, String>{
	
//	@Modifying
//	@Query("DELETE FROM Owner o WHERE o.user.username = :username")
//	void deleteOwnerOfUser(String username);
//	
//	@Modifying
//	@Query("DELETE FROM Pet p WHERE p.owner.id = :id")
//	public void deletePetsOfOwner(@Param("id") int id);
	
	@Query("SELECT o FROM Owner o WHERE o.user.username = :username")
	Optional<Owner> findOwnerByUser(String username);
	
	@Query("SELECT o FROM Owner o WHERE o.user.id = :id")
	Optional<Owner> findOwnerByUser(int id);

	@Query("SELECT v FROM Vet v WHERE v.user.id = :userId")
	Optional<Vet> findVetByUser(int userId);

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findById(Integer id);
	
	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllByAuthority(String auth);
	
	@Query("DELETE FROM Owner o WHERE o.user.id = :userId")
	@Modifying
	void deleteOwnerRelation(int userId);
	
	@Query("DELETE FROM Vet v WHERE v.user.id = :userId")
	@Modifying
	void deleteVetRelation(int userId);
	
}
