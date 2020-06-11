package org.meetup.openshift.rhoar.gateway.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.meetup.openshift.rhoar.gateway.command.ProductCommand;
import org.meetup.openshift.rhoar.gateway.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

@Component
@Slf4j
public class InventoryRepository {
	
	@Autowired
	Tracer tracer;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${hystrix.threadpool.ProductsThreads.coreSize}")
	private int threadSize;
	
	@Value("${services.inventory.url}")
	String inventoryServiceURL;
	
	public List<OrderItem> getProductDetails(List<OrderItem> items){
		Span span = tracer.buildSpan("getProductDetails").start();
		log.debug("Entering OrdersService.getProductDetails()");
		List<OrderItem> detailedItems = new ArrayList<>();
		for(int index= 0; index < items.size();) {
			List<Observable<OrderItem>> observables = new ArrayList<>();
			int batchLimit = Math.min( index + threadSize, items.size() );
			for( int batchIndex = index; batchIndex < batchLimit; batchIndex++ )
			{
				observables.add( new ProductCommand( items.get(batchIndex), inventoryServiceURL, restTemplate ).toObservable() );
			}
			log.info("Will get product detail from " + observables.size() + " items");
			Observable<OrderItem[]> zipped = Observable.zip( observables, objects->
			{
				OrderItem[] detailed = new OrderItem[objects.length];
				for( int batchIndex = 0; batchIndex < objects.length; batchIndex++ )
				{
					detailed[batchIndex] = (OrderItem)objects[batchIndex];
				}
				return detailed;
			} );
			Collections.addAll( detailedItems, zipped.toBlocking().first() );
			index += threadSize;
		}
		span.finish();
		return detailedItems;
	}

}
