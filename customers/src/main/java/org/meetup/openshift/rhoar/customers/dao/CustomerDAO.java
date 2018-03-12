package org.meetup.openshift.rhoar.customers.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.customers.model.Customer;

import io.opentracing.Span;
import io.opentracing.Tracer;

@ApplicationScoped
public class CustomerDAO implements ICustomerDAO {
	
	private static Logger logger = Logger.getLogger( CustomerDAO.class.getName() );
	
	@PersistenceContext(unitName = "customersPU")
    private EntityManager em;
	
	@Inject
	private Tracer tracer;
	
	public Customer findById(Long id) {
		Span childSpan = tracer.buildSpan("findById").start();
		childSpan.setTag("layer", "DAO");
		logger.debug("Entering CustomerDAO.findById()");
		Customer p = em.find(Customer.class, id);
		childSpan.finish();
		return p;
	}
}
