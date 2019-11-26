package Coursework10111_ontology;

import jade.content.Predicate;
import jade.core.AID;

public class SupplierOwns implements Predicate {

		private AID supplier;
		private OrderOntology manufacturerOrder;
		
		public AID getSupplier() {
			return supplier;
		}
		
		public void setSupplier(AID supplier) {
			this.supplier = supplier;
		}
		
		public OrderOntology getManufacturerOrder() {
			return manufacturerOrder;
		}
		
		public void setManufacturerOrder(OrderOntology manufacturerOrder) {
			this.manufacturerOrder = manufacturerOrder;
		}

	}


