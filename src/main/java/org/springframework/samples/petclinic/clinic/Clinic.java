package org.springframework.samples.petclinic.clinic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.clinic_owner.ClinicOwner;
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
