package Coursework10111_ontology;

import jade.content.Predicate;
import jade.core.AID;

public class ManufacturerOwns implements Predicate {
	private AID manufacturer;
	private OrderOntology order;
	
	public AID getManufacturer() {
		return manufacturer;
	}
	
	public void setManufacturer(AID manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public OrderOntology getOrder() {
		return order;
	}
	
	public void setOrder(OrderOntology order) {
		this.order = order;
	}

}
