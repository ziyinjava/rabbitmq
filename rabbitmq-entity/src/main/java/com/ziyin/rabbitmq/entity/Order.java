package com.ziyin.rabbitmq.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable {

	private static final long serialVersionUID = -5506222428210344986L;
	private String id;
	private String name;
	
	public Order() {
	}
	public Order(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
