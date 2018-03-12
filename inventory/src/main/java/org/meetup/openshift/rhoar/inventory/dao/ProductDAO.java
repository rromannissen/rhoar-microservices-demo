package org.meetup.openshift.rhoar.inventory.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.inventory.model.Product;

import io.opentracing.Span;
import io.opentracing.Tracer;

@ApplicationScoped
public class ProductDAO implements IProductDAO{
	
	private static Logger logger = Logger.getLogger( ProductDAO.class.getName() );
	
	@PersistenceContext(unitName = "productsPU")
    private EntityManager em;
	
	@Inject
	private Tracer tracer;
	
	public Product findById(Long id) {
		Span childSpan = tracer.buildSpan("findById").start();
		childSpan.setTag("layer", "DAO");
		logger.debug("Entering ProductDAO.findById()");
		Product p = em.find(Product.class, id);
		childSpan.finish();
		return p;
	}
}
