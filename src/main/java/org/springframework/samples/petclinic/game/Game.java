package org.springframework.samples.petclinic.game;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.owner.Owner;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
public class Game extends NamedEntity{
    String code;
    LocalDateTime start;
    LocalDateTime finish;
    @ManyToOne 
    Owner creator;
    @ManyToMany
    Set<Owner> players;
}
