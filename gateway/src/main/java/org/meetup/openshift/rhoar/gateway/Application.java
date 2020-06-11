package org.meetup.openshift.rhoar.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;

import feign.opentracing.hystrix.TracingConcurrencyStrategy;
import io.opentracing.Tracer;

@SpringBootApplication
@EnableCircuitBreaker
public class Application
{
	
	@Value("${jaeger.endpoint}")
	String jaegerEndpoint;
	
	@Autowired
	private Tracer tracer;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
    }
    
	
	@Bean
    public RestTemplate restTemplate() {   	
    	return new RestTemplate();
    }
    
    @Bean
    public HystrixCommandAspect hystrixAspect() {
      return new HystrixCommandAspect();
    }
    
    @Bean
    TracingConcurrencyStrategy hystrixTracingConcurrencyStrategy() {
      return TracingConcurrencyStrategy.register(tracer);
    }
	
	
}
