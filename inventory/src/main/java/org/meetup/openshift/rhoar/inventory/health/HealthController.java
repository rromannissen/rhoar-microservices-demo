package org.meetup.openshift.rhoar.inventory.health;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.swarm.health.Health;
import org.wildfly.swarm.health.HealthStatus;

@Path("/health")
public class HealthController {
	
	@GET
    @Health
    @Path("/status")
    public HealthStatus check() {
        return HealthStatus.named("server-state").up()
            .withAttribute("date", new Date().toString());
    }
}
