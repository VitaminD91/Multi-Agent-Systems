package Coursework10111_ontology;
import jade.content.AgentAction;
import jade.core.AID;

public class Sell implements AgentAction {
	private AID manufacturer;
	private OrderOntology item;
	
	public AID getManufacturer() {
		return manufacturer;
	}
	
	public void setBuyer(AID manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public OrderOntology getItem() {
		return item;
	}
	
	public void setItem(OrderOntology item) {
		this.item = item;
	}	
	
}
