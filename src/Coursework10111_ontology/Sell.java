package Coursework10111_ontology;
import jade.content.AgentAction;
import jade.core.AID;

public class Sell implements AgentAction {
	private AID manufacturer;
	private OrderOntology order;
	
	public AID getManufacturer() {
		return manufacturer;
	}
	
	public void setBuyer(AID manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public OrderOntology getOrder() {
		return order;
	}
	
	public void setOrder(OrderOntology order) {
		this.order = order;
	}	
	
}
