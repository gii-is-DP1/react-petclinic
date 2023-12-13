package org.springframework.samples.petclinic.model;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.springframework.samples.petclinic.configuration.UserRevisionListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@RevisionEntity(UserRevisionListener.class)
@Entity
public class UserRevEntity extends DefaultRevisionEntity {

    private String username;

}
