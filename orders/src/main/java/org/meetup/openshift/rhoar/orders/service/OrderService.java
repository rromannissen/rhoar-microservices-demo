package org.meetup.openshift.rhoar.orders.service;

import org.meetup.openshift.rhoar.orders.model.Order;
import org.meetup.openshift.rhoar.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class OrderService {
	
	@Autowired
	private OrderRepository repository;
	
	@Autowired
	Tracer tracer;
	
	/**
	 * Finds an {@link Order} using its {@code id} as search criteria
	 * @param id The {@link Order} {@code id}
	 * @return The {@link Order} with the supplied {@code id}, {@literal null} if no {@link Order} is found. 
	 */
	public Order findById(Long id) {
		Scope span = tracer.buildSpan("findById").startActive(true);
		log.debug("Entering OrderService.findById()");
		Order o = repository.findOne(id);	
		if (o != null) {
			//Force lazy loading of the OrderItem list
			o.getItems().size();
		}
		log.debug("Returning element: " + o);
		span.close();
		return o;
	}
}
