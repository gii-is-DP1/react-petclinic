package org.springframework.samples.petclinic.consultation;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ConsultationValidator implements Validator {

	@Override
	public void validate(Object obj, Errors errors) {
		Consultation consultation = (Consultation) obj;
		
		// pet validation
		Pet pet = consultation.getPet();
		Owner owner = consultation.getOwner();
		if (!pet.getOwner().getId().equals(owner.getId())) {
			errors.rejectValue("pet", "pet does not belong to the selected owner", "pet does not belong to the selected owner");
		}			
		
	}

	/**
	 * This Validator validates *just* Consultation instances
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Consultation.class.isAssignableFrom(clazz);
	}

}
