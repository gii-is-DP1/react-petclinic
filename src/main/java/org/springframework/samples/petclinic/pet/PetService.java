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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.clinic.PricingPlan;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.exceptions.DuplicatedPetNameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

	private final Integer BASIC_LIMIT = 2;
	private final Integer GOLD_LIMIT = 4;
	private final Integer PLATINUM_LIMIT = 7;

	private PetRepository petRepository;

	@Autowired
	public PetService(PetRepository petRepository) {
		this.petRepository = petRepository;
	}

	@Transactional(readOnly = true)
	public Collection<PetType> findPetTypes() throws DataAccessException {
		return petRepository.findPetTypes();
	}

	@Transactional(readOnly = true)
	public PetType findPetTypeByName(String name) throws DataAccessException {
		return petRepository.findPetTypeByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("PetType", "name", name));
	}

	@Transactional(readOnly = true)
	public Collection<Pet> findAll() {
		return (List<Pet>) petRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Pet findPetById(int id) throws DataAccessException {
		return petRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pet", "ID", id));
	}

	@Transactional(readOnly = true)
	public List<Pet> findAllPetsByOwnerId(int id) throws DataAccessException {
		return petRepository.findAllPetsByOwnerId(id);
	}

	@Transactional(readOnly = true)
	public List<Pet> findAllPetsByUserId(int id) throws DataAccessException {
		return petRepository.findAllPetsByUserId(id);
	}

	@Transactional(rollbackFor = DuplicatedPetNameException.class)
	public Pet savePet(Pet pet) throws DataAccessException, DuplicatedPetNameException {
		Pet otherPet = getPetWithNameAndIdDifferent(pet);
		if (otherPet != null && !otherPet.getId().equals(pet.getId())) {
			throw new DuplicatedPetNameException();
		} else
			petRepository.save(pet);

		return pet;
	}

	private Pet getPetWithNameAndIdDifferent(Pet pet) {
		String name = pet.getName().toLowerCase();
		for (Pet p : findAllPetsByOwnerId(pet.getOwner().getId())) {
			String compName = p.getName().toLowerCase();
			if (compName.equals(name) && !p.getId().equals(pet.getId())) {
				return p;
			}
		}
		return null;
	}

	@Transactional
	public Pet updatePet(Pet pet, int id) {
		Pet toUpdate = findPetById(id);
		BeanUtils.copyProperties(pet, toUpdate, "id");
		return savePet(toUpdate);
	}

	@Transactional
	public void deletePet(int id) throws DataAccessException {
		Pet toDelete = findPetById(id);
		petRepository.deleteVisitsByPet(toDelete.getId());
		petRepository.delete(toDelete);
	}

	public boolean underLimit(Owner owner) {
		Integer petCount = this.petRepository.countPetsByOwner(owner.getId());
		PricingPlan plan = owner.getClinic().getPlan();
		switch (plan) {
		case PLATINUM:
			if (petCount < PLATINUM_LIMIT)
				return true;
			break;
		case GOLD:
			if (petCount < GOLD_LIMIT)
				return true;
			break;
		default:
			if (petCount < BASIC_LIMIT)
				return true;
			break;
		}
		return false;
	}

	public Map<String, Object> getPetsStats() {
		Map<String, Object> res = new HashMap<>();
		Integer countAll = this.petRepository.countAll();
		int owners = this.petRepository.countAllOwners();
		Double avgPetsByOwner = (double) countAll / owners;
		Map<String, Integer> petsByType = getPetsByType();

		res.put("totalPets", countAll);
		res.put("avgPetsByOwner", avgPetsByOwner);
		res.put("petsByType", petsByType);

		return res;
	}

	private Map<String, Integer> getPetsByType() {
		Map<String, Integer> unsortedPetsByType = new HashMap<>();
		this.petRepository.countPetsGroupedByType().forEach(m -> {
			String key = m.get("type");
			Integer value = Integer.parseInt(m.get("pets"));
			unsortedPetsByType.put(key, value);
		});
		return unsortedPetsByType;
	}
}
