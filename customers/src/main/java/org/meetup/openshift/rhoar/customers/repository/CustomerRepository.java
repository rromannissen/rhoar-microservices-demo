package org.meetup.openshift.rhoar.customers.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.customers.model.Customer;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
	
	private static Logger logger = Logger.getLogger( CustomerRepository.class.getName() );
	
	@Inject
	Tracer tracer;
	
	public Customer findById(Long id) {
		Span childSpan = tracer.buildSpan("findById").start();
		childSpan.setTag("layer", "DAO");
		logger.debug("Entering CustomerDAO.findById()");
		Customer c = find("id", id).firstResult();
		childSpan.finish();
		return c;
	}

}
