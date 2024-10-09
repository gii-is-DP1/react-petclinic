package org.springframework.samples.petclinic.payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class InvoiceService {
    private InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Invoice> getAll(){
        return invoiceRepository.findAll();
    }
    
    @Transactional
    public Invoice save(Invoice invoice){
        return invoiceRepository.save(invoice);
    }
}
