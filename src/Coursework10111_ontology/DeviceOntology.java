package Coursework10111_ontology;
import java.awt.Component;
import java.util.List;

import jade.content.Concept;
import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;

public class DeviceOntology implements Concept{
	
	BatteryOntology battery;
	ScreenOntology screen;
	MemoryOntology memory;
	StorageOntology storage;
	private List<Component> components;
	
	@Slot(mandatory = true)
	
	
	@AggregateSlot(cardMin = 4)
	public List<Component> getComponents() {
		return components;
	}
	
	public void setComponents(List<Component> components) {
		this.components = components;
	}
	
	public DeviceOntology(BatteryOntology battery, ScreenOntology screen, MemoryOntology memory,
			StorageOntology storage) {
		
		this.battery = battery;
		this.screen = screen;
		this.memory = memory;
		this.storage = storage;
		
	}
	
	
	
	@Override public String toString() {
		String deviceString = "Screen: " + screen + " Battery: " + battery + 
				" Memory: " + memory + " Storage: " + storage;
		return deviceString;
				}
	
}