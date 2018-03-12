package org.meetup.openshift.rhoar.gateway.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meetup.openshift.rhoar.gateway.exception.ResourceNotFoundException;
import org.meetup.openshift.rhoar.gateway.model.Order;
import org.meetup.openshift.rhoar.gateway.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Path("/orders")
@Component
@Slf4j
public class OrdersController {
	
	@Autowired
	private OrdersService orderService;
	
	@Autowired
	Tracer tracer;
	@GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON } )
    public Order getById(@PathParam("id") Long id) {
		Order o;
		/* Use a try-with-resources block to ensure that the active span
		 * gets closed even in the case of exception.*/
		try(ActiveSpan span = tracer.buildSpan("getById").startActive()){
			log.debug("Entering OrderController.getById()");
			o = orderService.getById(id);
			if (o == null) {
				throw new ResourceNotFoundException("Requested order doesn't exist");
			}
		}
		return o;
	}
}
