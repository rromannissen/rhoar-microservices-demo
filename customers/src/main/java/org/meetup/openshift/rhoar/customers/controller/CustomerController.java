package org.meetup.openshift.rhoar.customers.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.meetup.openshift.rhoar.customers.exception.ResourceNotFoundException;
import org.meetup.openshift.rhoar.customers.model.Customer;
import org.meetup.openshift.rhoar.customers.service.ICustomerService;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;


@Path("/customers")
@ApplicationScoped
public class CustomerController {
	
	private static Logger logger = Logger.getLogger( CustomerController.class.getName() );
	
	@Inject
	ICustomerService customerService;
	
	@Inject
	Tracer tracer;
	
	@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
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
			logger.debug("Returning customer: " + c.toString());
		}
		return c;   
    }
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response findAll(@QueryParam("sort") String sortString,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize) {
		Page page = Page.of(pageIndex, pageSize);
        Sort sort = getSortFromQuery(sortString);
        return Response.ok(customerService.findAll(page, sort)).build();
	}

	/**
	 * This method tries to mimic the behavior of Spring MVC's @EnableSpringDataWebSupport annotation when it comes to the sort parameter.
	 * @param sortString The string containing the sort query to be used. Must have the "field,asc/desc" format or the second part of the query will be ignored.
	 * @return The {@link Sort} object with the sort criteria to apply.
	 */
	private Sort getSortFromQuery(String sortString) {
		if (sortString != null && !sortString.equals("")) {
			List<String> sortQuery = Arrays.asList(sortString.split(","));
			if (sortQuery == null || sortQuery.size()== 0 || sortQuery.size() >2)	
				return null;
			else {
				if (sortQuery.size() == 1) {
					return Sort.by(sortQuery.get(0));
				} else {
					if (sortQuery.get(1).equals("asc")) {
						return Sort.ascending(sortQuery.get(0));
					} else {
						if (sortQuery.get(1).equals("desc")) {
							return Sort.descending(sortQuery.get(0));
						} else {
							return Sort.by(sortQuery.get(0));
						}
					}
				}
			}	
		}
		return null;
	}

}
