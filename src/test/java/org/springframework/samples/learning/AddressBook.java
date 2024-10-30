package org.springframework.samples.learning;

import java.util.ArrayList;
import java.util.List;

public class AddressBook {
    private List<Customer> book = new ArrayList<Customer>();

    public void addCustomer(Customer c) {
        if (c==null)
            throw new NullPointerException();
        else
            book.add(c);
    }

	public int getNumberOfCustomers() {
		return book.size();
	}

}
