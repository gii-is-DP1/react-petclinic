package org.springframework.samples.petclinic.disease.treatmentvalidator;

import org.springframework.samples.petclinic.disease.Disease;
import org.springframework.samples.petclinic.disease.Treatment;
import org.springframework.samples.petclinic.pet.Pet;

public interface TreatmentValidatorAlgorithm {
    void validateDose(Integer doseToApply, // measured in mg per kg of the pet
                             Pet pet,       
                             Disease disease,
                             Treatment treatment) throws IncorrectDoseException, ToxicDoseException;
}
