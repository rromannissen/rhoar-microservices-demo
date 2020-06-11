package org.meetup.openshift.rhoar.inventory.service;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.meetup.openshift.rhoar.inventory.model.Product;

import io.quarkus.test.junit.QuarkusTest;

//TODO: Refactor to use the quarkus-junit5-mockito extension and the @InjectMock annotation instead when Red Hat build of Quarkus 1.4 is available.

@QuarkusTest
public class ProductServiceTest {

	@Inject
	private ProductService service;
	
	@Test
	public void findByIdExistingTest() {
		Product p = service.findById(1L);
		assertThat(p.getId(), equalTo(1L));
		assertThat(p.getName(), equalTo("Test"));
		assertThat(p.getDescription(), equalTo("Test Product"));
	}
	
	@Test
	public void findByIdNonExistingTest() {
		Product p = service.findById(2L);
		assertThat(p, is(nullValue()));
	}
	
}
