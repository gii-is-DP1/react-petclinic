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
package org.springframework.samples.petclinic.visit;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.disease.Disease;
import org.springframework.samples.petclinic.disease.Symptom;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.payment.Invoice;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.vet.Vet;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "visits")
@Getter
@Setter
public class Visit extends BaseEntity {

	@Column(name = "visit_date_time")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH/mm")
//	@NotNull
	private LocalDateTime datetime = LocalDateTime.now();

//	@NotEmpty
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Pet pet;

	@ManyToOne(optional = false)
	@JoinColumn(name = "vet_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Vet vet;

	@ManyToOne
	Disease diagnose;	

	@ManyToMany
	List<Symptom> symptoms;

	@OneToOne(optional = true)
	private Invoice invoice;

}
