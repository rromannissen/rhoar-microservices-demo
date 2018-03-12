package org.meetup.openshift.rhoar.customers.listener;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer; 

@WebListener
public class TracingContextListener implements ServletContextListener{

	@Inject
	Tracer tracer;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		GlobalTracer.register(tracer);	
	}
	
	

}
