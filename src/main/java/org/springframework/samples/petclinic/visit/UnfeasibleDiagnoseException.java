package org.springframework.samples.petclinic.visit;

import org.springframework.samples.petclinic.disease.Disease;

public class UnfeasibleDiagnoseException extends Exception {
    public UnfeasibleDiagnoseException(Disease diagnose, Visit visit) {
        super("The diagnose " + diagnose.getName() + " is not feasible for a " + visit.getPet().getType());
    }

}
