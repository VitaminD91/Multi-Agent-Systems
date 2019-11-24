package Coursework10111_ontology;
import jade.content.onto.annotations.Slot;

public class BatteryOntology extends ComponentOntology{
	
	private int capacity;
	
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
	
	@Override public String toString() { 
		return capacity + "mAh" ;
		}
	
}


// OLD CODE
	
	/*
	 * public int capacity;
	 * 
	 * public BatteryOntology(int _capacity) {
	 * 
	 * capacity = _capacity; }
	 * 
	 * public BatteryOntology(int _capacity, float _price, int _deliveryTime) {
	 * 
	 * capacity = _capacity; price = _price; deliveryTime = _deliveryTime; }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

