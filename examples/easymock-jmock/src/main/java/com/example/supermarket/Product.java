package com.example.supermarket;

import java.math.BigDecimal;

public class Product {

	private String sku;
	private String description;
	private BigDecimal price;
	
	public Product(String sku, String description, BigDecimal price) {
		super();
		this.sku = sku;
		this.description = description;
		this.price = price;
	}
	public String getSku() {
		return sku;
	}
	public String getDescription() {
		return description;
	}
	public BigDecimal getPrice() {
		return price;
	}
	
	
}
