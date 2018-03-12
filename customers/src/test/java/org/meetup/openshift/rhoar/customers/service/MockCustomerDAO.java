package org.meetup.openshift.rhoar.customers.service;

import javax.enterprise.context.ApplicationScoped;

import org.meetup.openshift.rhoar.customers.dao.ICustomerDAO;
import org.meetup.openshift.rhoar.customers.model.Customer;

@ApplicationScoped
public class MockCustomerDAO implements ICustomerDAO {

	@Override
	public Customer findById(Long id) {
		Customer c = null;
		if (id.equals(new Long(1))) {
			c = new Customer();
			c.setId(new Long(1));
			c.setUsername("mockusername");
			c.setName("Test User Mock");
			c.setSurname("Test Surname Mock");
			c.setAddress("Test Address Mock");
			c.setCity("Test City Mock");
			c.setCountry("Test Country Mock");
			c.setZipCode("MOCKZIP");
		}		
		return c;
	}

}
