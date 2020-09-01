package org.meetup.openshift.rhoar.inventory.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.inventory.model.Product;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

	private static Logger logger = Logger.getLogger( ProductRepository.class.getName() );
	
	@Inject
	Tracer tracer;
	
	public Product findById(Long id) {
		Span childSpan = tracer.buildSpan("findById").start();
		childSpan.setTag("layer", "DAO");
		logger.debug("Entering CustomerDAO.findById()");
		Product p = find("id", id).firstResult();
		childSpan.finish();
		return p;
	}

}
