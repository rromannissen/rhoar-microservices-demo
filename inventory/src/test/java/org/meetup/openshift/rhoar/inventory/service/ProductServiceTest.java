package org.meetup.openshift.rhoar.inventory.service;

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
import org.meetup.openshift.rhoar.inventory.RestApplication;
import org.meetup.openshift.rhoar.inventory.controller.MockProductService;
import org.meetup.openshift.rhoar.inventory.dao.ProductDAO;
import org.meetup.openshift.rhoar.inventory.model.Product;

@RunWith(Arquillian.class)
public class ProductServiceTest {
	@Deployment
    public static Archive<?> createDeployment() {
		
		List<MavenResolvedArtifact> artifacts = Arrays.asList(Maven.resolver().loadPomFromFile("pom.xml")
                .importDependencies(ScopeType.COMPILE).resolve()
                .withTransitivity().asResolvedArtifact());
        
        WebArchive archive = ShrinkWrap.create( WebArchive.class, "inventory.war" )
        	.addPackages(true, RestApplication.class.getPackage())
        	.addPackage(Product.class.getPackage())
        	.addPackage(ProductService.class.getPackage())
            .addClass(MockProductDAO.class)
            .deleteClass(ProductDAO.class)
            .deleteClass(MockProductService.class)
            .addAsResource("project-local.yml", "project-defaults.yml")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsResource("META-INF/load.sql", "META-INF/load.sql");
        
        for (MavenResolvedArtifact a : artifacts){
        	archive.addAsLibrary(a.asFile());
        }
        return archive; 
    }
	
	@Inject
	private ProductService service;
	
	@Test
	public void findByIdExistingTest() {
		Product p = service.findById(new Long(1));
		assertThat(p.getId(), equalTo(new Long(1)));
		assertThat(p.getName(), equalTo("Test Product Mock"));
		assertThat(p.getDescription(), equalTo("Test Description Mock"));
	}
	
	@Test
	public void findByIdNonExistingTest() {
		Product p = service.findById(new Long(2));
		assertThat(p, is(nullValue()));
	}
}
