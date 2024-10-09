package org.springframework.samples.petclinic.disease;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class SymptomService {
    SymptomRepository repo;
    @Autowired
    public SymptomService(SymptomRepository sr){
        this.repo=sr;
    }
    @Transactional
    public List<Symptom> getAll() {
        return repo.findAll();
    }

    @Transactional
    public Symptom save(Symptom s) {
        return repo.save(s);
    }

    @Transactional
    public void save(List<Symptom> s) {
        repo.saveAll(s);
    }
}
