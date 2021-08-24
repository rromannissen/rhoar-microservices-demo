package io.konveyor.demo.customers.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.konveyor.demo.customers.model.Customer;
import io.konveyor.demo.customers.repository.CustomerRepository;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class CustomerServiceTest {
	
	@Inject
	CustomerService service;
	
	@InjectMock
	CustomerRepository customerRepository;
	
	Customer customer;
	
	@BeforeEach
	void initCustomer() {
		customer = new Customer();
		customer.setId(1L);
		customer.setUsername("mockusername");
		customer.setName("Test User Mock");
		customer.setSurname("Test Surname Mock");
		customer.setAddress("Test Address Mock");
		customer.setCity("Test City Mock");
		customer.setCountry("Test Country Mock");
		customer.setZipCode("MOCKZIP");
	}
	
	
	@Test
	public void findByIdExistingTest() {
		
		when(customerRepository.findById(1L)).thenReturn(customer);
		
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
		
		when(customerRepository.findById(2L)).thenReturn(null);
		
		Customer c = service.findById(2L);
		assertThat(c, is(nullValue()));
	}
}
