package org.springframework.samples.petclinic.clinic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.petclinic.clinicowner.ClinicOwner;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.owner.Owner;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clinics")
public class Clinic extends BaseEntity{

    @Column(name = "name")
	@NotBlank
	private String name;

    @Column(name = "address")
	@NotBlank
	private String address;

    @Column(name = "telephone")
	@NotEmpty
	@Digits(fraction = 0, integer = 10)
	private String telephone;

    @Enumerated(EnumType.STRING)
	@NotNull
	private PricingPlan plan;

    @ManyToOne
	@JoinColumn(name = "clinic_owner", referencedColumnName = "id")
	private ClinicOwner clinicOwner;

    @OneToMany(mappedBy = "clinic", orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Set<Owner> owners;
}
