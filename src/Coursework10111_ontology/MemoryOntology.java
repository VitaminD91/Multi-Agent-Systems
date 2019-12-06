package Coursework10111_ontology;

import jade.content.onto.annotations.Slot;

public class MemoryOntology extends ComponentOntology {
	public int capacity;
	public float price;
	public int deliveryTime;

	public MemoryOntology() {

	}

	@Slot(mandatory = true)
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {

		this.capacity = capacity;
	}

	public MemoryOntology(int capacity) {

		this.capacity = capacity;
	}

//Used for supplier inventory
	public MemoryOntology(int capacity, float price, int deliveryTime) {
		
		this.capacity = capacity;
		this.price = price;
		this.deliveryTime = deliveryTime;
	}

	@Slot(mandatory = true)
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Slot(mandatory = true)
	public int getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(int deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	@Override
	public String toString() {
		return capacity + "GB";
	}

}
