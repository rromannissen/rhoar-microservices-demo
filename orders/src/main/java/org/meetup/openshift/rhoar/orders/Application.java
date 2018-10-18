package org.meetup.openshift.rhoar.orders;

import javax.ws.rs.container.DynamicFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;
import io.opentracing.Tracer;
import io.opentracing.contrib.jaxrs2.server.ServerTracingDynamicFeature;
import io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter;

@SpringBootApplication
public class Application {
	
	@Value("${jaeger.endpoint}")
	String jaegerEndpoint;
	
	@Autowired
	private Tracer tracer;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider();
    }
    
    @Bean
    public Tracer tracer() {
    	SamplerConfiguration samplerConfiguration = new SamplerConfiguration();
    	ReporterConfiguration reporterConfiguration = new ReporterConfiguration();
    	SenderConfiguration senderConfiguration = new SenderConfiguration();
    	
        return new Configuration("orders")
        		.withSampler(samplerConfiguration
        				.withType(ProbabilisticSampler.TYPE)
        				.withParam(1))
        		.withReporter(reporterConfiguration
        				.withSender(senderConfiguration
        						.withEndpoint(jaegerEndpoint)))
        		.getTracer();
    }
    
    /* Register SpanFinishingFilter and ServerTracingDynamicFeature for the JAX-RS
     * instrumentation to work */
    
    @Bean
    public FilterRegistrationBean spanFinishingFilterBean() {
    	final FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
    	filterRegBean.setFilter(new SpanFinishingFilter());
    	filterRegBean.addUrlPatterns("/*");
    	filterRegBean.setEnabled(Boolean.TRUE);
    	filterRegBean.setName("SpanFinishingFilter");
    	filterRegBean.setAsyncSupported(Boolean.TRUE);
    	return filterRegBean;
    }
    
    @Bean
    public DynamicFeature serverTracingDynamicFeatureBean() {
    	return new ServerTracingDynamicFeature.Builder(tracer)
    	        .build();
    }
}
