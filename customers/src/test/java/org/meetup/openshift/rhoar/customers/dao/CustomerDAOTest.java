package org.meetup.openshift.rhoar.customers.dao;

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
import org.meetup.openshift.rhoar.customers.model.Customer;
import org.meetup.openshift.rhoar.customers.service.MockCustomerDAO;

@RunWith(Arquillian.class)
public class CustomerDAOTest {
	@Deployment
    public static Archive<?> createDeployment() {
		
		List<MavenResolvedArtifact> artifacts = Arrays.asList(Maven.resolver().loadPomFromFile("pom.xml")
                .importDependencies(ScopeType.COMPILE).resolve()
                .withTransitivity().asResolvedArtifact());
        
        WebArchive archive = ShrinkWrap.create( WebArchive.class, "inventory.war" )
        	.addPackages(true, RestApplication.class.getPackage())
        	.addPackage(Customer.class.getPackage())
            .addPackage(CustomerDAO.class.getPackage())
            .deleteClass(MockCustomerDAO.class)
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
	private CustomerDAO dao;
	
	/** Data import is performed at application startup and no modifications are
	 * currently performed in the tests, so assertions can be done safely without
	 * the need to reset the data before each test**/
	@Test
	public void findByIdExistingTest() {
		Customer c = dao.findById(new Long(1));
		assertThat(c.getId(), equalTo(new Long(1)));
		assertThat(c.getUsername(), equalTo("testusername"));
		assertThat(c.getName(), equalTo("Test Name"));
		assertThat(c.getSurname(), equalTo("Test Surname"));
		assertThat(c.getAddress(), equalTo("Test Address"));
		assertThat(c.getZipCode(), equalTo("TESTZIP"));
		assertThat(c.getCity(), equalTo("Test City"));
		assertThat(c.getCountry(), equalTo("Test Country"));
	}
	
	@Test
	public void findByIdNonExistingTest() {
		Customer c = dao.findById(new Long(3));
		assertThat(c, is(nullValue()));
	}
}
