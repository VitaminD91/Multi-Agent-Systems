package Coursework10111_ontology;
import jade.content.onto.annotations.Slot;

public class BatteryOntology extends ComponentOntology{
	
	private int capacity;
	private float price;
	private int deliveryTime;
	
	
	public BatteryOntology() {
		
	}
	
	@Slot (mandatory = true)
	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public BatteryOntology(int capacity) {
		this.capacity = capacity;
	}
	
	//Used for supplier inventory
	public BatteryOntology(int capacity, float price, int deliveryTime) {
		
		this.capacity = capacity;
		this.price = price;
		this.deliveryTime = deliveryTime;
	}
	
	@Slot (mandatory = true)
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
	
	@Slot (mandatory = true)
	public int getDeliveryTime() {
		return deliveryTime;
	}
	
	
	public void setDeliveryTime(int deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	@Override public String toString() { 
		return capacity + "mAh" ;
		}
	
}



