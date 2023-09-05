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

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.LimitReachedException;
import org.springframework.samples.petclinic.exceptions.ResourceNotOwnedException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/pets")
@Tag(name = "Pets", description = "The Pet management API")
@SecurityRequirement(name = "bearerAuth")
public class PetRestController {

	private final PetService petService;
	private final UserService userService;
	private static final String OWNER_AUTH = "OWNER";
	private static final String ADMIN_AUTH = "ADMIN";
	private static final String VET_AUTH = "VET";
	private static final String CLINIC_OWNER_AUTH = "CLINIC_OWNER";

	@Autowired
	public PetRestController(PetService petService, UserService userService) {
		this.petService = petService;
		this.userService = userService;
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}

	@GetMapping
	public ResponseEntity<List<Pet>> findAll(@RequestParam(required = false) Integer userId) {
		User user = userService.findCurrentUser();
		if (userId != null) {
			if (user.getId().equals(userId) || user.hasAnyAuthority(VET_AUTH, ADMIN_AUTH, CLINIC_OWNER_AUTH).equals(true))
				return new ResponseEntity<>(petService.findAllPetsByUserId(userId), HttpStatus.OK);
		} else {
			if (user.hasAnyAuthority(VET_AUTH, ADMIN_AUTH, CLINIC_OWNER_AUTH).equals(true))
				return new ResponseEntity<>((List<Pet>) this.petService.findAll(), HttpStatus.OK);
		}
		throw new AccessDeniedException();
	}

	@GetMapping("types")
	public ResponseEntity<List<PetType>> findAllTypes() {
		List<PetType> res = (List<PetType>) petService.findPetTypes();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Pet> create(@RequestBody @Valid Pet pet)
			throws DataAccessException, DuplicatedPetNameException {
		User user = userService.findCurrentUser();
		Pet newPet = new Pet();
		Pet savedPet;
		BeanUtils.copyProperties(pet, newPet, "id");
		if (user.hasAuthority(OWNER_AUTH).equals(true)) {
			Owner owner = userService.findOwnerByUser(user.getId());
			if (this.petService.underLimit(owner)) {
				newPet.setOwner(owner);
				savedPet = this.petService.savePet(newPet);
			} else
				throw new LimitReachedException("Pets", owner.getClinic().getPlan());
		} else {
			savedPet = this.petService.savePet(newPet);
		}

		return new ResponseEntity<>(savedPet, HttpStatus.CREATED);
	}

	@PutMapping("{petId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Pet> update(@PathVariable("petId") int petId, @RequestBody @Valid Pet pet) {
		Pet aux = RestPreconditions.checkNotNull(petService.findPetById(petId), "Pet", "ID", petId);
		User user = userService.findCurrentUser();
		if (user.hasAuthority(OWNER_AUTH).equals(true)) {
			Owner loggedOwner = userService.findOwnerByUser(user.getId());
			Owner petOwner = aux.getOwner();
			if (loggedOwner.getId().equals(petOwner.getId())) {
				Pet res = this.petService.updatePet(pet, petId);
				return new ResponseEntity<>(res, HttpStatus.OK);
			} else
				throw new ResourceNotOwnedException(aux);
		} else {
			Pet res = this.petService.updatePet(pet, petId);
			return new ResponseEntity<>(res, HttpStatus.OK);
		}

	}

	@GetMapping("{petId}")
	public ResponseEntity<Pet> findById(@PathVariable("petId") int petId) {
		Pet pet = RestPreconditions.checkNotNull(petService.findPetById(petId), "Pet", "ID", petId);
		User user = userService.findCurrentUser();
		if (user.hasAuthority(OWNER_AUTH).equals(true)) {
			Owner loggedOwner = userService.findOwnerByUser(user.getId());
			Owner petOwner = pet.getOwner();
			if (loggedOwner.getId().equals(petOwner.getId()))
				return new ResponseEntity<>(this.petService.findPetById(petId), HttpStatus.OK);
			else
				throw new ResourceNotOwnedException(pet);
		} else {
			return new ResponseEntity<>(this.petService.findPetById(petId), HttpStatus.OK);
		}
	}

	@DeleteMapping("{petId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("petId") int petId) {
		Pet pet = RestPreconditions.checkNotNull(petService.findPetById(petId), "Pet", "ID", petId);
		User user = userService.findCurrentUser();
		if (user.hasAuthority(OWNER_AUTH).equals(true)) {
			Owner loggedOwner = userService.findOwnerByUser(user.getId());
			Owner petOwner = pet.getOwner();
			if (loggedOwner.getId().equals(petOwner.getId())) {
				petService.deletePet(petId);
				return new ResponseEntity<>(new MessageResponse("Pet deleted!"), HttpStatus.OK);
			} else
				throw new ResourceNotOwnedException(pet);
		} else {
			petService.deletePet(petId);
			return new ResponseEntity<>(new MessageResponse("Pet deleted!"), HttpStatus.OK);
		}
	}

	@GetMapping(value = "stats")
	public ResponseEntity<Map<String, Object>> getStats() {
		return new ResponseEntity<>(this.petService.getPetsStats(), HttpStatus.OK);
	}

}
