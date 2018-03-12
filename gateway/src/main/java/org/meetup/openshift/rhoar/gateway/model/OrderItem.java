package org.meetup.openshift.rhoar.gateway.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meetup.openshift.rhoar.gateway.serialization.ProductDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem implements Serializable {

	private static final long serialVersionUID = 95662333489566465L;
	
	private Product product;
	private Integer quantity;
	private BigDecimal price;
	
	
	@JsonProperty("product")
	public Product getProduct() {
		return this.product;
	}
	
	@JsonProperty("productUID")
	@JsonDeserialize(using = ProductDeserializer.class)
	public void setProduct(Product product) {
		this.product = product;
	}
}
