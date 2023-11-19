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
        double totalAmount = 0;
        int frequentRenterPoints = 0;


        for (Rental rental : c.getRentals()) {
            double thisAmount = 0;
            thisAmount = rental.getCharge();

            // add frequent renter points with bonus
            frequentRenterPoints += rental.getFrequentRenterPoints();
            
            rental.setAmount(thisAmount);
            totalAmount += thisAmount;
        }

        StatementDTO statement = new StatementDTO(c, totalAmount, frequentRenterPoints);
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
