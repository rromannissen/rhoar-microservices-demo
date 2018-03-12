package org.meetup.openshift.rhoar.gateway.repository;

import org.meetup.openshift.rhoar.gateway.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderRepository {
	@Autowired
	Tracer tracer;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${services.orders.url}")
	String ordersServiceURL;
	
	@HystrixCommand(commandKey = "Orders", fallbackMethod = "getFallbackOrder", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
	})
	public Order getOrderById(Long id) {
		ActiveSpan span = tracer.buildSpan("getOrderById").startActive();
		log.debug("Entering OrderRepository.getOrderById()");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(ordersServiceURL)
				.pathSegment( "{order}");
		Order o = restTemplate.getForObject(
				builder.buildAndExpand(id).toUriString(), 
				Order.class);
		if (o == null)
			log.debug("Obtained null order");
		else
			log.debug(o.toString());
		span.close();
		return o;
	}

	public Order getFallbackOrder(Long id, Throwable e) {
		log.warn("Failed to obtain Order, " + e.getMessage() + " for order with id " + id);
		return null;
	}
}
