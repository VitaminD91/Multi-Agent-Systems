

import java.util.Random;

import Coursework10111_ontology.BatteryOntology;
import Coursework10111_ontology.DeviceOntology;
import Coursework10111_ontology.MemoryOntology;
import Coursework10111_ontology.ScreenOntology;
import Coursework10111_ontology.StorageOntology;

public class Assembler {

	public DeviceOntology getRandomDevice() {

		BatteryOntology battery;
		ScreenOntology screen;
		MemoryOntology memory;
		StorageOntology storage;
		
		
		if(Math.random() < 0.5) {
			// Small smartphone
			screen = new ScreenOntology(5);
			battery = new BatteryOntology(2000);
		} else {
			// phablet (okay boomer)
			screen = new ScreenOntology(7);
			battery = new BatteryOntology(3000);
		}
		
		if(Math.random() < 0.5) {
			memory = new MemoryOntology(4);
		}
		else {
			memory = new MemoryOntology(8);
		}
		
		if(Math.random() < 0.5) {
			storage = new StorageOntology(64);
		}
		else {
			storage = new StorageOntology(256);
		}
				
		DeviceOntology myDevice = new DeviceOntology(battery, screen, memory, storage);
		
		return myDevice;
	}
}
