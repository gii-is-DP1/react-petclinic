package org.springframework.samples.petclinic.disease.treatmentvalidator;

import java.util.Set;

import org.springframework.samples.petclinic.disease.Disease;
import org.springframework.samples.petclinic.disease.Treatment;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.visit.Visit;

public class DummyTreatmentValidationAlgorithm implements TreatmentValidatorAlgorithm {

    @Override
    public void validateDose(Integer doseToApply, Pet pet, Disease disease, Treatment treatment)
            throws IncorrectDoseException, ToxicDoseException {        
    }

    
    
}
