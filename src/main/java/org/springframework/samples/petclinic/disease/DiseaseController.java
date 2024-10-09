package org.springframework.samples.petclinic.disease;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiseaseController {

    @Autowired
    DiseaseService service;

    @GetMapping("/api/v1/diseases")
    public List<Disease> getDiseases(){
        return service.findDiseases();
    }

    @GetMapping("/api/v1/diseases/{id}")
    public Disease getDiseases(@PathVariable("id")Integer id){
        Disease d=service.findDiseaseById(id);
        if(d==null)
            throw new ResourceNotFoundException("");
        return d;
    }
}
