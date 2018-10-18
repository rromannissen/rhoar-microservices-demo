package org.meetup.openshift.rhoar.gateway.repository;

import org.meetup.openshift.rhoar.gateway.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerRepository {
	
	@Autowired
	Tracer tracer;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${services.customers.url}")
	String customersServiceURL;
	
	@HystrixCommand(commandKey = "Customers", fallbackMethod = "getFallbackCustomer", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
	})
	public Customer getCustomerById(Long id) {
		Scope span = tracer.buildSpan("getCustomerById").startActive(true);
		log.debug("Entering OrdersService.getCustomerById()");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(customersServiceURL)
				.pathSegment( "{customer}");		
		Customer c = restTemplate.getForObject(
				builder.buildAndExpand(id).toUriString(), 
				Customer.class);
		//Trigger fallback if no result is obtained.
		if (c == null) {
			throw new RuntimeException();
		}
		log.debug(c.toString());
		span.close();
		return c;
	}
	
	public Customer getFallbackCustomer(Long id, Throwable e) {
		log.warn("Failed to obtain Customer, " + e.getMessage() + " for customer with id " + id);
		Customer c = new Customer();
		c.setId(id);
		c.setUsername("Unknown");
		c.setName("Unknown");
		c.setSurname("Unknown");
		c.setAddress("Unknown");
		c.setCity("Unknown");
		c.setCountry("Unknown");
		c.setZipCode("Unknown");
		return c;
	}

}
