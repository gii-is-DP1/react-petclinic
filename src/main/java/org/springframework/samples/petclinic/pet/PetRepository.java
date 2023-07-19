/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.pet;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends CrudRepository<Pet, Integer> {

	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	List<PetType> findPetTypes() throws DataAccessException;

	@Query("SELECT ptype FROM PetType ptype WHERE ptype.name LIKE :name")
	Optional<PetType> findPetTypeByName(String name) throws DataAccessException;

	@Query(("SELECT p FROM Pet p WHERE p.owner.id = :id"))
	List<Pet> findAllPetsByOwnerId(int id) throws DataAccessException;

	@Modifying
	@Query("DELETE FROM Visit v WHERE v.pet.id = :petId")
	public void deleteVisitsByPet(@Param("petId") int petId);

	@Query(("SELECT COUNT(p) FROM Pet p WHERE p.owner.id = :id"))
	public Integer countPetsByOwner(int id);

	@Query(("SELECT p FROM Pet p WHERE p.owner.user.id = :id"))
	List<Pet> findAllPetsByUserId(int id);

	// STATS
	// ADMIN
	@Query("SELECT COUNT(p) FROM Pet p")
	public Integer countAll();

	@Query("SELECT COUNT(o) FROM Owner o")
	public Integer countAllOwners();

	@Query("SELECT NEW MAP(p.type.name as type, cast(COUNT(p) as string) as pets) FROM Pet p GROUP BY p.type")
	public List<Map<String, String>> countPetsGroupedByType();

}
