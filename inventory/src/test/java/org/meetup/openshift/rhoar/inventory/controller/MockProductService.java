package org.meetup.openshift.rhoar.inventory.controller;

import javax.enterprise.context.ApplicationScoped;

import org.meetup.openshift.rhoar.inventory.model.Product;
import org.meetup.openshift.rhoar.inventory.service.IProductService;

import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class MockProductService implements IProductService {
	
	@Override
	public Product findById(Long id) {
		if (id == 1L) {
			Product product = new Product();
			product.setId(1L);
			product.setName("Test");
			product.setDescription("Test Product");
		return product;
		} else {
			return null;
		}
	}

}
