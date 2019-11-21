package Coursework10111_ontology;
import jade.content.onto.annotations.Slot;

public class StorageOntology extends ComponentOntology{
	
	private int capacity;
	@Slot (mandatory = true)
	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		
		this.capacity = capacity;
	}
	
	public StorageOntology(int capacity) {
		
		this.capacity = capacity;
	}
	
	  @Override public String toString() {
		  return capacity + "GB" ; 
		  }
}


/* THIS OLD CODE - Don't listen to it
 * public class StorageOntology extends Component {
 * 
 * public int capacity;
 * 
 * public StorageOntology(int _capacity) {
 * 
 * capacity = _capacity; }
 * 
 * public StorageOntology(int _capacity, float _price, int _deliveryTime) {
 * 
 * capacity = _capacity; price = _price; deliveryTime = _deliveryTime; }
 * 
  @Override public String toString() { return capacity + "GB" ; }
 * 
 * }
 */