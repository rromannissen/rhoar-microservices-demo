package org.meetup.openshift.rhoar.orders.repository;

import org.meetup.openshift.rhoar.orders.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

}
