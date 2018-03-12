package org.meetup.openshift.rhoar.inventory.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meetup.openshift.rhoar.inventory.RestApplication;
import org.meetup.openshift.rhoar.inventory.model.Product;
import org.meetup.openshift.rhoar.inventory.service.ProductService;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

@RunWith(Arquillian.class)
public class ProductControllerTest {
	
	private static String port = System.getProperty("arquillian.swarm.http.port", "18080");

    private Client client;

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        Properties properties = new Properties();
        properties.put("swarm.http.port", port);
        return new Swarm(properties).withProfile("local");
    }
	
	@Deployment
    public static Archive<?> createDeployment() {
		
		List<MavenResolvedArtifact> artifacts = Arrays.asList(Maven.resolver().loadPomFromFile("pom.xml")
                .importDependencies(ScopeType.COMPILE).resolve()
                .withTransitivity().asResolvedArtifact());
        
        WebArchive archive = ShrinkWrap.create( WebArchive.class, "inventory.war" )
        	.addPackages(true, RestApplication.class.getPackage())
        	.addPackage(Product.class.getPackage())
        	.addPackage(ProductController.class.getPackage())
            .addClass(MockProductService.class)
            .deleteClass(ProductService.class)
            .addAsResource("project-local.yml", "project-defaults.yml")
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsResource("META-INF/load.sql", "META-INF/load.sql");
        
        for (MavenResolvedArtifact a : artifacts){
        	archive.addAsLibrary(a.asFile());
        }
        return archive; 
    }
	
	@Before
    public void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }
	
    @Test
    @RunAsClient
	public void getByIdExistingTest() {
    	WebTarget target = client.target("http://localhost:" + port).path("/products").path("/1");
    	Response response = target.request(MediaType.APPLICATION_JSON).get();
    	assertThat(response.getStatus(), equalTo(new Integer(200)));
    	JsonObject value = Json.parse(response.readEntity(String.class)).asObject();
    	assertThat(value.getLong("id", 0), equalTo(new Long(1)));
    	assertThat(value.getString("name", null), equalTo("Test Product Mock"));
    	assertThat(value.getString("description", null), equalTo("Test Description Mock"));
	}
    
    @Test
    @RunAsClient
	public void getByIdNonExistingTest() {
    	WebTarget target = client.target("http://localhost:" + port).path("/products").path("/2");
    	Response response = target.request(MediaType.APPLICATION_JSON).get();
    	assertThat(response.getStatus(), equalTo(new Integer(404)));
    }
}
