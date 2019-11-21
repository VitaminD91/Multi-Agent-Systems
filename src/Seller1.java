import Coursework10111_Ontology.BatteryOntology;
import Coursework10111_Ontology.MemoryOntology;
import Coursework10111_Ontology.ScreenOntology;
import Coursework10111_Ontology.StorageOntology;

public class Seller1 {

	public void test() {
		
		int deliveryTime = 1;
		
		BatteryOntology smallBattery = new BatteryOntology(2000, 70f, deliveryTime);
		BatteryOntology largeBattery = new BatteryOntology(3000, 100f, deliveryTime);
		ScreenOntology smallScreen = new ScreenOntology(5, 100f, deliveryTime);
		ScreenOntology largeScreen = new ScreenOntology(7, 150f, deliveryTime);
		StorageOntology smallStorage = new StorageOntology(64, 25f, deliveryTime);
		StorageOntology largeStorage = new StorageOntology(256, 50f, deliveryTime);
		MemoryOntology smallMemory = new MemoryOntology(4, 30f, deliveryTime);
		MemoryOntology largeMemory = new MemoryOntology(8, 60f, deliveryTime);	
	
	}
}
