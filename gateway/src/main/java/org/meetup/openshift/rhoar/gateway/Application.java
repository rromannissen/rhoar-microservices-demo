package org.meetup.openshift.rhoar.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;

@SpringBootApplication
@EnableCircuitBreaker
public class Application
{
	
	@Value("${jaeger.endpoint}")
	String jaegerEndpoint;
	
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
	
}
