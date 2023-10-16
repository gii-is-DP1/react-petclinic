package org.springframework.samples.petclinic.game;

import java.time.LocalDateTime;

import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.Entity;
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
}
