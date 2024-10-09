package org.springframework.samples.petclinic.disease.treatmentvalidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.samples.petclinic.disease.Disease;
import org.springframework.samples.petclinic.disease.Treatment;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.visit.Visit;

public class AllwaysThrowsIncorrectDoseExceptionDetectionAlgorithm implements TreatmentValidatorAlgorithm{
  
    @Override
    public void validateDose(Integer doseToApply, Pet pet, Disease disease, Treatment treatment)
            throws IncorrectDoseException, ToxicDoseException {        
        throw new IncorrectDoseException();
    }
    
}
