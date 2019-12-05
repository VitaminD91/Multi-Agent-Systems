import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.ComponentOntology;

public class Warehouse {

	private HashMap<String, List<ComponentOntology>> Inventory = new HashMap<String, List<ComponentOntology>>();

	private void addToWarehouse(ComponentOntology component) {
		
		Inventory.put(component.getName(), null);

	}
	
	private List<ComponentOntology> removeFromWarehouse(ComponentOntology component) {
		
		return Inventory.remove(component.getName());
	}
}
