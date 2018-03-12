package org.meetup.openshift.rhoar.inventory.controller;

import javax.enterprise.context.ApplicationScoped;

import org.meetup.openshift.rhoar.inventory.model.Product;
import org.meetup.openshift.rhoar.inventory.service.IProductService;

@ApplicationScoped
public class MockProductService implements IProductService {

	@Override
	public Product findById(Long id) {
		Product p = null;
		if (id.equals(new Long(1))) {
			p = new Product();
			p.setId(new Long(1));
			p.setName("Test Product Mock");
			p.setDescription("Test Description Mock");
		}
		return p;
	}

}
