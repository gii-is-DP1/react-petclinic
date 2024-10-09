package org.springframework.samples.petclinic.disease.treatmentvalidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.samples.petclinic.disease.Disease;
import org.springframework.samples.petclinic.disease.Treatment;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.visit.Visit;

public class InvertedIncorrectTreatmentDetectionRuleValidatorAlgorithm implements TreatmentValidatorAlgorithm{

    

    @Override
    public void validateDose(Integer doseToApply, Pet pet, Disease disease, Treatment treatment)
            throws IncorrectDoseException, ToxicDoseException {
                double toxicDose=pet.getWeight()*treatment.getMaxDose();
                double correctDose=pet.getWeight()*treatment.getBaseDose() + (disease.getSeverity()==5?treatment.getShockDose():0);
                if(doseToApply<toxicDose)
                    throw new ToxicDoseException();
                else if(doseToApply==correctDose)
                    throw new IncorrectDoseException();                
    }
}
