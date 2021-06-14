package org.meetup.openshift.ordermanagement.repository;

import org.meetup.openshift.ordermanagement.model.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

}
