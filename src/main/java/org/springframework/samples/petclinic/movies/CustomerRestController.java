package org.springframework.samples.petclinic.movies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/statement")
public class CustomerRestController {

    private CustomerService service;

    @Autowired
    public CustomerRestController(CustomerService service) {
        this.service = service;
    }
    
    
    @GetMapping
    public String showStatement(@PathVariable("customerId") Integer id) {

        Customer c = this.service.findById(id);

        for (Rental rental : c.getRentals()) {
            rental.setAmount(rental.getCharge());
        }

        StatementDTO statement = new StatementDTO(c, c.getTotalCharge(), c.getTotalFrequentRenterPoints());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(statement);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }    
}
