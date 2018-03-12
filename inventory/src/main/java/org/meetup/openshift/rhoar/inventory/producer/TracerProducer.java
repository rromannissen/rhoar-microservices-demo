package org.meetup.openshift.rhoar.inventory.producer;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import com.uber.jaeger.Configuration;
import com.uber.jaeger.Configuration.SamplerConfiguration;
import com.uber.jaeger.samplers.ProbabilisticSampler;
import com.uber.jaeger.senders.HttpSender;

import io.opentracing.Tracer;

@Dependent
public class TracerProducer {
	@Inject
    @ConfigurationValue("jaeger.endpoint")
	String jaegerEndpoint;
	
	@Produces
	@Singleton
	public Tracer jaegerTracer() {
		return new Configuration("inventory", 
				new SamplerConfiguration(ProbabilisticSampler.TYPE, 1),
				new Configuration.ReporterConfiguration(
						new HttpSender(jaegerEndpoint,0))).getTracer();
	}
}
