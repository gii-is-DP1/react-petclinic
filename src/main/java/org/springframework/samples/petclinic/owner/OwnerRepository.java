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
package org.springframework.samples.petclinic.owner;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.clinic.PricingPlan;
import org.springframework.samples.petclinic.pet.Pet;

/**
 * Spring Data JPA OwnerRepository interface
 *
 * @author Michael Isvy
 * @since 15.1.2013
 */
public interface OwnerRepository extends CrudRepository<Owner, Integer> {

	/**
	 * Retrieve <code>Owner</code>s from the data store by last name, returning all
	 * owners whose last name <i>starts</i> with the given name.
	 * 
	 * @param lastName Value to search for
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an
	 *         empty <code>Collection</code> if none found)
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner WHERE owner.lastName LIKE :lastName%")
	public Collection<Owner> findByLastName(@Param("lastName") String lastName);

	@Query("SELECT p FROM Pet p WHERE p.owner.id = :ownerId")
	public List<Pet> findPetsByOwner(@Param("ownerId") int ownerId);

	@Query("SELECT DISTINCT owner FROM Owner owner WHERE owner.user.id = :userId")
	public Optional<Owner> findByUser(int userId);

	// STATS

	@Query("SELECT COUNT(o) FROM Owner o WHERE o.clinic.plan = :plan")
	public Integer countByPlan(PricingPlan plan);

	@Query("SELECT COUNT(o) FROM Owner o")
	public Integer countAll();

	@Query("SELECT NEW MAP(v.pet.owner.id as userId, cast(COUNT(v) as integer) as visits) FROM  Visit v GROUP BY v.pet.owner")
	public List<Map<String, Integer>> getOwnersWithMostVisits();

}
