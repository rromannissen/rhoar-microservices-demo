package org.meetup.openshift.rhoar.orders.actuator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ThreadInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.meetup.openshift.rhoar.orders.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.DumpEndpoint;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.stereotype.Component;

/**
 * Spring Actuator endpoints are accessible via Spring MVC by default. This class
 * exposes the endpoints through JAX-RS instead.
 * 
 * @author Patrick Ruhkopf
 *
 */
@Path("/")
@Component
public class ActuatorEndpoints {
	
	@Autowired
	private HealthEndpoint health;

	@Autowired
	private MetricsEndpoint metrics;

	@Autowired
	private DumpEndpoint dump;

	@GET
	@Path("/health")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getHealth() {
		return health.invoke();
	}

	@GET
	@Path("/metrics")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getMetrics() {
		return this.metrics.invoke();
	}

	@GET
	@Path("/metrics/{name:.*}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getMetric(@PathParam("name") final String name) {
		final Object value = this.metrics.invoke().get(name);
		if (value == null) {
			throw new ResourceNotFoundException("No such metric: " + name);
		}
		return value;
	}

	@GET
	@Path("/dump")
	@Produces(MediaType.TEXT_PLAIN)
	public Object getThreadDump() {
		return new StreamingOutput() {
			@Override
			public void write(final OutputStream os) throws IOException, WebApplicationException {
				final Writer writer = new BufferedWriter(new OutputStreamWriter(os));
				for (final ThreadInfo thread : dump.invoke()) {
					writer.write(thread.toString());
				}
				writer.flush();
			}
		};
	}
}