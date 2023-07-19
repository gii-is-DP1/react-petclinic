/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.samples.petclinic.pet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author japarejo
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicatedPetNameException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3330551940727004798L;
	
	public DuplicatedPetNameException() {
		super("You can't have two pets with the same name.");
	}
    
}
