package org.meetup.openshift.ordermanagement.service;


import org.jboss.logging.Logger;
import org.meetup.openshift.ordermanagement.model.Customer;
import org.meetup.openshift.ordermanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {
	
	@Autowired
	private CustomerRepository repository;
	
	private static Logger logger = Logger.getLogger( CustomerService.class.getName() );
	
	public Customer findById(Long id) {
		logger.debug("Entering CustomerService.findById()");
		Customer c = repository.findById(id).orElse(null);
		logger.debug("Returning element: " + c);
		return c;
	}
	
	public Page<Customer>findAll(Pageable pageable) {
		logger.debug("Entering CustomerService.findAll()");
		Page<Customer> p = repository.findAll(pageable);
		logger.debug("Returning element: " + p);
		return p;
	}

}
