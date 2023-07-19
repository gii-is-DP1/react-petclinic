package org.springframework.samples.petclinic.visit;

import java.time.LocalDateTime;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class VisitValidator implements Validator {

	private static final String REQUIRED = "required";

	@Override
	public void validate(Object obj, Errors errors) {
		Visit visit = (Visit) obj;
		LocalDateTime datetime = visit.getDatetime();
		// date validation
		if (datetime == null || datetime.getHour() < 9 || datetime.getHour() >= 20) {
			errors.rejectValue("datetime", REQUIRED + " and between 9:00 and 20:00",
					REQUIRED + " and between 9:00 and 20:00");
		}

//		// date validation
//				if (visit.isNew() && datetime.compareTo(LocalDateTime.now()) >= 1) {
//					errors.rejectValue("datetime", REQUIRED + " and you can only appoint a visit in the future",
//							REQUIRED + " and you can only appoint a visit in the future");
//				}

		// pet validation
		if (visit.getPet() == null) {
			errors.rejectValue("pet", REQUIRED, REQUIRED);
		}
	}

	/**
	 * This Validator validates *just* Visit instances
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Visit.class.isAssignableFrom(clazz);
	}

}
