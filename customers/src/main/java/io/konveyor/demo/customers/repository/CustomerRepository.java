package io.konveyor.demo.customers.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import io.konveyor.demo.customers.model.Customer;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
	
	private static Logger logger = Logger.getLogger( CustomerRepository.class.getName() );
	
	@Inject
	Tracer tracer;
	
	public Customer findById(Long id) {
		Span childSpan = tracer.buildSpan("findById").start();
		childSpan.setTag("layer", "Repository");
		logger.debug("Entering CustomerRepository.findById()");
		Customer c = find("id", id).firstResult();
		childSpan.finish();
		return c;
	}
	
	public List<Customer> findAll(Page page, Sort sort) {
		Span childSpan = tracer.buildSpan("findAll").start();
		childSpan.setTag("layer", "Repository");
		logger.debug("Entering CustomerRepository.findAll()");
		List<Customer> c = Customer.findAll(sort)
				.page(page)
				.list();
		childSpan.finish();
		return c;
	}

}
