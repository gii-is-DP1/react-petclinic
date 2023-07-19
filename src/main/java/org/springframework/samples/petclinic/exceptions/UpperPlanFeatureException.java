package org.springframework.samples.petclinic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.clinic.PricingPlan;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class UpperPlanFeatureException extends RuntimeException {

	private static final long serialVersionUID = -3906338266891937036L;

	private PricingPlan expected, current;

	public UpperPlanFeatureException(PricingPlan expected, PricingPlan current) {
		super(String.format("You need to be subscribed to plan %s to access this feature and you have plan %s.",
				expected, current));
		this.expected = expected;
		this.current = current;
	}

}
