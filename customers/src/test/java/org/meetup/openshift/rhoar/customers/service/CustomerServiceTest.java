package org.meetup.openshift.rhoar.customers.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meetup.openshift.rhoar.customers.RestApplication;
import org.meetup.openshift.rhoar.customers.controller.MockCustomerService;
import org.meetup.openshift.rhoar.customers.dao.CustomerDAO;
import org.meetup.openshift.rhoar.customers.model.Customer;

@RunWith(Arquillian.class)
public class CustomerServiceTest {
	
	@Deployment
    public static Archive<?> createDeployment() {
		
		List<MavenResolvedArtifact> artifacts = Arrays.asList(Maven.resolver().loadPomFromFile("pom.xml")
                .importDependencies(ScopeType.COMPILE).resolve()
                .withTransitivity().asResolvedArtifact());
        
        WebArchive archive = ShrinkWrap.create( WebArchive.class, "inventory.war" )
        	.addPackages(true, RestApplication.class.getPackage())
        	.addPackage(Customer.class.getPackage())
        	.addPackage(CustomerService.class.getPackage())
            .addClass(MockCustomerDAO.class)
            .deleteClass(CustomerDAO.class)
            .deleteClass(MockCustomerService.class)
            .addAsResource("project-local.yml", "project-defaults.yml")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsResource("META-INF/load.sql", "META-INF/load.sql");
        
        for (MavenResolvedArtifact a : artifacts){
        	archive.addAsLibrary(a.asFile());
        }
        return archive; 
    }
	
	@Inject
	private CustomerService service;
	
	@Test
	public void findByIdExistingTest() {
		Customer c = service.findById(new Long(1));
		assertThat(c.getId(), equalTo(new Long(1)));
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
		Customer c = service.findById(new Long(3));
		assertThat(c, is(nullValue()));
	}
}
