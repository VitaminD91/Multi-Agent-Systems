import Coursework10111_Ontology.MemoryOntology;
import Coursework10111_Ontology.StorageOntology;

public class Seller2 {
	
	public void test() {
		
		int deliveryTime = 4;
		
		StorageOntology smallStorage = new StorageOntology(64, 15f, deliveryTime);
		StorageOntology largeStorage = new StorageOntology(256, 40f, deliveryTime);
		MemoryOntology smallMemory = new MemoryOntology(4, 20f, deliveryTime);
		MemoryOntology largeMemory = new MemoryOntology(8, 35f, deliveryTime);
	}

}
