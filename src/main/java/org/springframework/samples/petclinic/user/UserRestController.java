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
package org.springframework.samples.petclinic.user;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
class UserRestController {

	private final UserService userService;
	private final AuthoritiesService authService;

	@Autowired
	public UserRestController(UserService userService, AuthoritiesService authService) {
		this.userService = userService;
		this.authService = authService;
	}

	@GetMapping
	public ResponseEntity<List<User>> findAll(@RequestParam(required = false) String auth) {
		List<User> res;
		if (auth != null) {
			res = (List<User>) userService.findAllByAuthority(auth);
		} else
			res = (List<User>) userService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("authorities")
	public ResponseEntity<List<Authorities>> findAllAuths() {
		List<Authorities> res = (List<Authorities>) authService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping(value = "{id}")
	public ResponseEntity<User> findById(@PathVariable("id") Integer id) {
		return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<User> create(@RequestBody @Valid User user) {
		User savedUser = userService.saveUser(user);
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}

	@PutMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> update(@PathVariable("userId") Integer id, @RequestBody @Valid User user) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		return new ResponseEntity<>(this.userService.updateUser(user, id), HttpStatus.OK);
	}

	@DeleteMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("userId") int id) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		if (userService.findCurrentUser().getId() != id) {
			userService.deleteUser(id);
			return new ResponseEntity<>(new MessageResponse("User deleted!"), HttpStatus.OK);
		} else
			throw new AccessDeniedException("You can't delete yourself!");
	}

}
