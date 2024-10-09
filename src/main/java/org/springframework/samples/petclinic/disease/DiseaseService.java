package org.springframework.samples.petclinic.disease;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiseaseService {

    DiseaseRepository repo;
    
    @Autowired    
    public DiseaseService(DiseaseRepository repo){
        this.repo=repo;
    }
    
    @Transactional(readOnly = true)
    public Disease findDiseaseById(int i) {
        Optional<Disease> od=repo.findById(i);
        return od.isPresent()?od.get():null;
    }
    

    @Transactional(readOnly = true)
    public List<Disease> findDiseases() {
        return repo.findAll();
    }
}
