package org.meetup.openshift.rhoar.orders.repository;

import org.meetup.openshift.rhoar.orders.model.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

}
