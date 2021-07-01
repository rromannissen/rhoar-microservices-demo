package org.meetup.openshift.rhoar.customers.service;


import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.customers.model.Customer;
import org.meetup.openshift.rhoar.customers.repository.CustomerRepository;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@Transactional
@ApplicationScoped
public class CustomerService implements ICustomerService{
	
	private static Logger logger = Logger.getLogger( CustomerService.class.getName() );
	
	@Inject
	CustomerRepository repository;
	
	@Inject
	Tracer tracer;
	
	/**
	 * Finds a {@link Customer} using its {@code id} as search criteria
	 * @param id The {@link Customer} {@code id}
	 * @return The {@link Customer} with the supplied {@code id}, {@literal null} if no {@link Customer} is found. 
	 */
	public Customer findById(Long id) {
		Span childSpan = tracer.buildSpan("findById").start();
		childSpan.setTag("layer", "Service");
		logger.debug("Entering CustomerService.findById()");
		Customer c = repository.findById(id);
		childSpan.finish();
		return c;
	}
	
	@Override
	public List<Customer> findAll(Page page, Sort sort) {
		Span childSpan = tracer.buildSpan("findAll").start();
		childSpan.setTag("layer", "Service");
		logger.debug("Entering CustomerService.findAll()");
		List<Customer> c = repository.findAll(page, sort);
		childSpan.finish();
		return c;
	}
	
	
}
