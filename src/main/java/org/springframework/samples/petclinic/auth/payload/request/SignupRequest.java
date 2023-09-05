package org.springframework.samples.petclinic.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

import org.springframework.samples.petclinic.clinic.Clinic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
	
	// User
	@NotBlank
	private String username;
	
	@NotBlank
	private String authority;

	@NotBlank
	private String password;
	
	//Both
	@NotBlank
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	@NotBlank
	private String city;
	//Owner

	private String address;
	private String telephone;
	private Clinic clinic;

}
