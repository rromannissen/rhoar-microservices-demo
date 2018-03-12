package org.meetup.openshift.rhoar.customers.dao;

import org.meetup.openshift.rhoar.customers.model.Customer;

public interface ICustomerDAO {
	public Customer findById(Long id);
}
