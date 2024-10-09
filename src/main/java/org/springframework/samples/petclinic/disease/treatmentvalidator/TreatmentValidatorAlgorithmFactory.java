package org.springframework.samples.petclinic.disease.treatmentvalidator;

import java.util.List;

public class TreatmentValidatorAlgorithmFactory {
    public static TreatmentValidatorAlgorithm create(String name){
        TreatmentValidatorAlgorithm result=null;
        List<TreatmentValidatorAlgorithm> incorrectAlgorithms = List.of(
            new AllwaysThrowsIncorrectDoseExceptionDetectionAlgorithm(),
            new DummyTreatmentValidationAlgorithm(),
            new AllwaysThrowsToxicDoseExceptionDetectionAlgorithm()
        );
        switch(name){
            case "ValidAlgorithm":
                result=new ValidTreatmentValidatorAlgorithm();
                break;
            case "IncorrectAlgorithm":
                int randomIndex=(int)(Math.random()*incorrectAlgorithms.size());
                result=incorrectAlgorithms.get(randomIndex);
                break;           
            default:
                result=new ValidTreatmentValidatorAlgorithm();
        }
        return result;
    }
}
