package io.konveyor.demo.customers.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.konveyor.demo.customers.model.Customer;
import io.konveyor.demo.customers.service.ICustomerService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class CustomerControllerTest {
	
	@InjectMock
	ICustomerService customerService;
	
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
	public void getByIdExisting() {
		
		when(customerService.findById(1L)).thenReturn(customer);
		
		given()
			.when().get("/customers/1")
			.then()
			.statusCode(200)
			.body("id", is(1))
			.body("username", is("mockusername"))
			.body("name", is("Test User Mock"))
			.body("surname", is("Test Surname Mock"))
			.body("address", is("Test Address Mock"))
			.body("city", is("Test City Mock"))
			.body("country", is("Test Country Mock"))
			.body("zipCode", is("MOCKZIP"));		
	}
	
	@Test
	public void getByIdNonExisting() {
		
		when(customerService.findById(2L)).thenReturn(null);
		
		given()
			.when().get("/customers/2")
			.then()
			.statusCode(404);		
	}
}
