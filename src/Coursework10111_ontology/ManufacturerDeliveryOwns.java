package Coursework10111_ontology;

import java.util.List;

import jade.core.AID;

public class ManufacturerDeliveryOwns {
	
	private AID supplier;
	private List<ComponentOntology> delivery;
	
	public AID getSupplier() {
		return supplier;
	}
	
	public void setSupplier(AID supplier) {
		this.supplier = supplier;
	}
	
	public List<ComponentOntology> getDelivery() {
		return delivery;
	}
	
	public void setDelivery(List<ComponentOntology> delivery) {
		this.delivery = delivery;
	}

}
