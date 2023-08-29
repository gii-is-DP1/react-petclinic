package org.springframework.samples.petclinic.consultation;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.user.User;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "consultation_tickets")
@Getter
@Setter
public class Ticket extends BaseEntity {

	@Column(name = "description")
	@NotEmpty
	private String description;

	@Column(name = "creation_date")
	@CreationTimestamp
	private LocalDateTime creationDate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@Valid
	@ManyToOne(optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	protected Consultation consultation;

}
