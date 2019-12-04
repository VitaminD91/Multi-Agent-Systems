package Coursework10111_ontology;

import java.util.ArrayList;
import java.util.List;

import jade.content.Predicate;
import jade.core.AID;

public class SupplierResponseOwns implements Predicate{
	
 
		private AID manufacturer;
		private List<ComponentOntology> componentList;
		
		public AID getManufacturer() {
			return manufacturer;
		}
		
		public void setManufacturer(AID manufacturer) {
			this.manufacturer = manufacturer;
		}
		
		public List<ComponentOntology> getComponentList() {
			return componentList;
		}
		
		public void setComponentList(List<ComponentOntology> componentList) {
			this.componentList = componentList;
		}

	}

