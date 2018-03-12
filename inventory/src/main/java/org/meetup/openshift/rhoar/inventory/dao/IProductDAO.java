package org.meetup.openshift.rhoar.inventory.dao;

import org.meetup.openshift.rhoar.inventory.model.Product;

public interface IProductDAO {
	public Product findById(Long id);
}
