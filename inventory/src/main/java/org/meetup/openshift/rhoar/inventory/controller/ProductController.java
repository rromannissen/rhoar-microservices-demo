package org.meetup.openshift.rhoar.inventory.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.inventory.exception.ResourceNotFoundException;
import org.meetup.openshift.rhoar.inventory.model.Product;
import org.meetup.openshift.rhoar.inventory.service.IProductService;

import io.opentracing.Scope;
import io.opentracing.Tracer;


@Path("/products")
@ApplicationScoped
public class ProductController {
	private static Logger logger = Logger.getLogger( ProductController.class.getName() );
	
	@Inject
	private IProductService productService;
	
	@Inject
	Tracer tracer;
	
	@GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Product getById(@PathParam("id") Long id) {
		Product p;
		/* Use a try-with-resources block to ensure that the active span
		 * gets closed even in the case of exception.*/
		try (Scope scope = tracer
				.buildSpan("getById")
				.withTag("layer", "Controller")
				.startActive(true)){
			logger.debug("Entering ProductController.getById()");
			p = productService.findById(id);
			if (p == null) {
				throw new ResourceNotFoundException("Product not found");
			}
		}	
		return p;    
    }

}
