package org.meetup.openshift.rhoar.inventory.service;

import javax.enterprise.context.ApplicationScoped;

import org.meetup.openshift.rhoar.inventory.dao.IProductDAO;
import org.meetup.openshift.rhoar.inventory.model.Product;

@ApplicationScoped
public class MockProductDAO implements IProductDAO{
	
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
