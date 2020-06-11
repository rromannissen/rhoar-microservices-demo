package org.meetup.openshift.rhoar.customers.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.meetup.openshift.rhoar.customers.model.Customer;

import io.quarkus.test.junit.QuarkusTest;

//TODO: Refactor to use the quarkus-junit5-mockito extension and the @InjectMock annotation instead when Red Hat build of Quarkus 1.4 is available.

@QuarkusTest
public class CustomerServiceTest {
	
	@Inject
	CustomerService service;
	
	@Test
	public void findByIdExistingTest() {
		Customer c = service.findById(1L);
		assertThat(c.getId(), equalTo(1L));
		assertThat(c.getUsername(), equalTo("mockusername"));
		assertThat(c.getName(), equalTo("Test User Mock"));
		assertThat(c.getSurname(), equalTo("Test Surname Mock"));
		assertThat(c.getAddress(), equalTo("Test Address Mock"));
		assertThat(c.getZipCode(), equalTo("MOCKZIP"));
		assertThat(c.getCity(), equalTo("Test City Mock"));
		assertThat(c.getCountry(), equalTo("Test Country Mock"));
	}
	
	@Test
	public void findByIdNonExistingTest() {
		Customer c = service.findById(2L);
		assertThat(c, is(nullValue()));
	}
}
