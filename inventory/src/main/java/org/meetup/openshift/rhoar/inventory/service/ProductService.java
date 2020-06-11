package org.meetup.openshift.rhoar.inventory.service;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.inventory.model.Product;
import org.meetup.openshift.rhoar.inventory.repository.ProductRepository;

import io.opentracing.Span;
import io.opentracing.Tracer;

@Transactional
@ApplicationScoped
public class ProductService implements IProductService {
	
	private static Logger logger = Logger.getLogger( ProductService.class.getName() );
	
	@Inject
	ProductRepository repository;
	
	@Inject
	Tracer tracer;
	
	/**
	 * Finds a {@link Product} using its {@code id} as search criteria
	 * @param id The {@link Product} {@code id}
	 * @return The {@link Product} with the supplied {@code id}, {@literal null} if no {@link Product} is found. 
	 */
	public Product findById(Long id) {
		Span childSpan = tracer.buildSpan("findById").start();
		childSpan.setTag("layer", "Service");
		logger.debug("Entering ProductService.findById()");
		Product p = repository.findById(id);
		childSpan.finish();
		return p;
	}
	
	
}
