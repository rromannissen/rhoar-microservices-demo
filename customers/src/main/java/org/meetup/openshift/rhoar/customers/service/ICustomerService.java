package org.meetup.openshift.rhoar.customers.service;

import org.meetup.openshift.rhoar.customers.model.Customer;

public interface ICustomerService {
	public Customer findById(Long id);
}
