package org.springframework.samples.petclinic.auth;

import java.util.ArrayList;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.auth.payload.request.SignupRequest;
import org.springframework.samples.petclinic.clinic.ClinicService;
import org.springframework.samples.petclinic.clinicowner.ClinicOwner;
import org.springframework.samples.petclinic.clinicowner.ClinicOwnerService;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final PasswordEncoder encoder;
	private final AuthoritiesService authoritiesService;
	private final UserService userService;
	private final OwnerService ownerService;
	private final VetService vetService;
	private final ClinicOwnerService clinicOwnerService;
	private final ClinicService clinicService;

	@Autowired
	public AuthService(PasswordEncoder encoder, AuthoritiesService authoritiesService, UserService userService,
			OwnerService ownerService, VetService vetService, ClinicOwnerService clinicOwnerService, ClinicService clinicService) {
		this.encoder = encoder;
		this.authoritiesService = authoritiesService;
		this.userService = userService;
		this.ownerService = ownerService;
		this.vetService = vetService;
		this.clinicOwnerService = clinicOwnerService;
		this.clinicService = clinicService;
	}

	@Transactional
	public void createUser(@Valid SignupRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(encoder.encode(request.getPassword()));
		String strRoles = request.getAuthority();
		Authorities role;

		switch (strRoles.toLowerCase()) {
		case "admin":
			role = authoritiesService.findByAuthority("ADMIN");
			user.setAuthority(role);
			userService.saveUser(user);
			break;
		case "vet":
			role = authoritiesService.findByAuthority("VET");
			user.setAuthority(role);
			userService.saveUser(user);
			Vet vet = new Vet();
			vet.setFirstName(request.getFirstName());
			vet.setLastName(request.getLastName());
			vet.setCity(request.getCity());
			vet.setSpecialties(new ArrayList<Specialty>());
			vet.setClinic(clinicService.findClinicById(request.getClinic().getId()));
			vet.setUser(user);
			vetService.saveVet(vet);
			break;
		case "clinic owner":
			role = authoritiesService.findByAuthority("CLINIC_OWNER");
			user.setAuthority(role);
			userService.saveUser(user);
			ClinicOwner clinicOwner = new ClinicOwner();
			clinicOwner.setFirstName(request.getFirstName());
			clinicOwner.setLastName(request.getLastName());
			clinicOwner.setUser(user);
			clinicOwnerService.saveClinicOwner(clinicOwner);
			break;
		default:
			role = authoritiesService.findByAuthority("OWNER");
			user.setAuthority(role);
			userService.saveUser(user);
			Owner owner = new Owner();
			owner.setFirstName(request.getFirstName());
			owner.setLastName(request.getLastName());
			owner.setAddress(request.getAddress());
			owner.setCity(request.getCity());
			owner.setTelephone(request.getTelephone());
			owner.setClinic(clinicService.findClinicById(request.getClinic().getId()));
			owner.setUser(user);
			ownerService.saveOwner(owner);

		}
	}

}
