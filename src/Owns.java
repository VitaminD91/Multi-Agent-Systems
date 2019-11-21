
import jade.content.Predicate;
import jade.core.AID;

public class Owns implements Predicate {
	private AID supplier;
	private Item item;
	
	public AID getSupplier() {
		return supplier;
	}
	
	public void setSupplier(AID supplier) {
		this.supplier = supplier;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
}
