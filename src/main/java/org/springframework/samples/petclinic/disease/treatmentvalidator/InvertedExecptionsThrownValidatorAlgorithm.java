package org.springframework.samples.petclinic.disease.treatmentvalidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.samples.petclinic.disease.Disease;
import org.springframework.samples.petclinic.disease.Treatment;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.visit.Visit;

public class InvertedExecptionsThrownValidatorAlgorithm implements TreatmentValidatorAlgorithm{

    

    @Override
    public void validateDose(Integer doseToApply, Pet pet, Disease disease, Treatment treatment)
            throws IncorrectDoseException, ToxicDoseException {
                double toxicDoseThreshold=pet.getWeight()*treatment.getMaxDose();
                if(doseToApply>toxicDoseThreshold)
                    throw new IncorrectDoseException();          
                double correctDose=pet.getWeight()*treatment.getBaseDose() + (disease.getSeverity()==5?treatment.getShockDose():0);                
                // if the correct dose is greater than the toxic dose threshold, the correct dose is the toxic dose threshold
                if(correctDose>toxicDoseThreshold)
                    correctDose=toxicDoseThreshold;                                
                else if(doseToApply!=correctDose)
                    throw new ToxicDoseException();                
    }
}
