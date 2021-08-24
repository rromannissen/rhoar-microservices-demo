package io.konveyor.demo.customers.service;

import java.util.List;

import io.konveyor.demo.customers.model.Customer;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

public interface ICustomerService {
	public Customer findById(Long id);
	
	public List<Customer> findAll(Page page, Sort sort);
}
