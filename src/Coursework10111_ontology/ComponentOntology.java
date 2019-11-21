package Coursework10111_ontology;
import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class ComponentOntology implements Concept {
	private float price;
	private int deliveryTime;
	private String name;
	
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
	
	@Slot(mandatory = true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}