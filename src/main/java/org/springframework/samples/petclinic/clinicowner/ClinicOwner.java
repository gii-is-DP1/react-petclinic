package org.springframework.samples.petclinic.clinicowner;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.clinic.Clinic;
import org.springframework.samples.petclinic.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "clinic_owners")
public class ClinicOwner extends Person{

    @OneToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST })
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

    @OneToMany(mappedBy = "clinicOwner", orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Set<Clinic> clinics;
}
