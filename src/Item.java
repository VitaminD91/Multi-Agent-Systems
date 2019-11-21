import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class Item implements Concept {
	
	private int identificationNumber;
	@Slot (mandatory = true)
	public int getIdentificationNumber() {
		return identificationNumber;
	}
	
	public void setIdentification(int identificationNumber) {
		
		this.identificationNumber = identificationNumber;
	}
	
}