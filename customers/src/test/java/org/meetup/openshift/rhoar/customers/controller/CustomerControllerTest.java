package org.meetup.openshift.rhoar.customers.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

//TODO: Refactor to use the quarkus-junit5-mockito extension and the @InjectMock annotation instead when Red Hat build of Quarkus 1.4 is available.

@QuarkusTest
public class CustomerControllerTest {
	
	@Test
	public void getByIdExisting() {
		
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
		
		given()
			.when().get("/customers/2")
			.then()
			.statusCode(404);		
	}
}
