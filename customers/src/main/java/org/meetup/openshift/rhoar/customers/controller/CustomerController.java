package org.meetup.openshift.rhoar.customers.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.customers.exception.ResourceNotFoundException;
import org.meetup.openshift.rhoar.customers.model.Customer;
import org.meetup.openshift.rhoar.customers.service.ICustomerService;

import io.opentracing.Scope;
import io.opentracing.Tracer;


@Path("/customers")
@ApplicationScoped
public class CustomerController {
	
	private static Logger logger = Logger.getLogger( CustomerController.class.getName() );
	
	@Inject
	private ICustomerService customerService;
	
	@Inject
	Tracer tracer;
	
	@GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Customer getById(@PathParam("id") Long id) {
		Customer c;
		/* Use a try-with-resources block to ensure that the active span
		 * gets closed even in the case of exception.*/
		try (Scope scope = tracer
				.buildSpan("getById")
				.withTag("layer", "Controller")
				.startActive(true)){
			logger.debug("Entering CustomerController.getById()");
			c = customerService.findById(id);
			if (c == null) {
				throw new ResourceNotFoundException("Customer not found");
			}
		}
		return c;   
    }

}
