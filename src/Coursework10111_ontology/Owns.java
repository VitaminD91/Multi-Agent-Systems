package Coursework10111_ontology;

import jade.content.Predicate;
import jade.core.AID;

public class Owns implements Predicate {
	private AID supplier;
	private AID customer;
	private AID manufacturer;
	private OrderOntology order;
	
	public AID getManufacturer() {
		return manufacturer;
	}
	
	public void setManufacturer(AID manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public AID getCustomer() {
		return customer;
	}
	
	public void setCustomer(AID customer) {
		this.customer = customer;
	}
	
	public OrderOntology getOrder() {
		return order;
	}
	
	public void setOrder(OrderOntology order) {
		this.order = order;
	}

}
