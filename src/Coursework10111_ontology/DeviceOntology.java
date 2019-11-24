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
	
	
	public DeviceOntology() {
		
	}

	public DeviceOntology(BatteryOntology battery, ScreenOntology screen, MemoryOntology memory,
			StorageOntology storage) {
		
		this.battery = battery;
		this.screen = screen;
		this.memory = memory;
		this.storage = storage;
		
	}
	
	@Slot (mandatory = true)
	public BatteryOntology getBattery() {
		return battery;
	}
	
	public void setBattery(BatteryOntology battery) {
		this.battery = battery;
	}
	
	@Slot (mandatory = true)
	public ScreenOntology getScreen() {
		return screen;
	}
	
	public void setScreen(ScreenOntology screen) {
		this.screen = screen;
	}
	
	@Slot (mandatory = true)
	public MemoryOntology getMemory() {
		return memory;
	}
	
	public void setMemory(MemoryOntology memory) {
		this.memory = memory;
	}
	
	@Slot (mandatory = true)
	public StorageOntology getStorage() {
		return storage;
	}
	
	public void setStorage(StorageOntology storage) {
		this.storage = storage;
	}
	
	
	
	@Override public String toString() {
		String deviceString = "Screen: " + screen + " Battery: " + battery + 
				" Memory: " + memory + " Storage: " + storage;
		return deviceString;
				}
	
}