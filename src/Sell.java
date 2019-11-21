import jade.content.AgentAction;
import jade.core.AID;

public class Sell implements AgentAction {
	private AID manufacturer;
	private Item item;
	
	public AID getManufacturer() {
		return manufacturer;
	}
	
	public void setBuyer(AID manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}	
	
}
