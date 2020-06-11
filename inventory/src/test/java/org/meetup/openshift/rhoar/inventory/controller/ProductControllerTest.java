package org.meetup.openshift.rhoar.inventory.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

//TODO: Refactor to use the quarkus-junit5-mockito extension and the @InjectMock annotation instead when Red Hat build of Quarkus 1.4 is available.

@QuarkusTest
public class ProductControllerTest {

	@Test
	public void getByIdExisting() {
		
		given()
			.when().get("/products/1")
			.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Test"))
			.body("description", is("Test Product"));		
	}
	
	@Test
	public void getByIdNonExisting() {
		
		given()
			.when().get("/products/2")
			.then()
			.statusCode(404);		
	}

}
