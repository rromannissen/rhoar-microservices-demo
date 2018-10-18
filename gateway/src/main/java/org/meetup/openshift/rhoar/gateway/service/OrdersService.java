package org.meetup.openshift.rhoar.gateway.service;

import org.meetup.openshift.rhoar.gateway.model.Order;
import org.meetup.openshift.rhoar.gateway.repository.CustomerRepository;
import org.meetup.openshift.rhoar.gateway.repository.InventoryRepository;
import org.meetup.openshift.rhoar.gateway.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdersService {
	
	@Autowired
	private Tracer tracer;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	public Order getById(Long id) {		
		Scope span = tracer.buildSpan("getById").startActive(true);
		log.debug("Entering OrdersService.getById()");
		Order o = orderRepository.getOrderById(id);
		if (o != null) {
			o.setCustomer(customerRepository.getCustomerById(o.getCustomer().getId()));
			o.setItems(inventoryRepository.getProductDetails(o.getItems()));
		}
		span.close();
		return o;
	}	

}
