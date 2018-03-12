package org.meetup.openshift.rhoar.inventory.service;

import org.meetup.openshift.rhoar.inventory.model.Product;

public interface IProductService {
	
	public Product findById(Long id);
}
