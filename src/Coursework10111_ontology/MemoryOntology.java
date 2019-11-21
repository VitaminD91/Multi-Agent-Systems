package Coursework10111_ontology;
import jade.content.onto.annotations.Slot;

public class MemoryOntology extends ComponentOntology{
	public int capacity;
	
	@Slot (mandatory = true)
	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		
		this.capacity = capacity;
	}
	
public MemoryOntology(int capacity) {
		
		this.capacity = capacity;
	}

	@Override public String toString() {
		return capacity + "GB" ; 
		}
	
}


/* THIS IS OLD CODE
 * public class MemoryOntology extends Component{
 * 
 * public int capacity;
 * 
 * public MemoryOntology(int _capacity) {
 * 
 * capacity = _capacity; }
 * 
 * public MemoryOntology(int _capacity, float _price, int _deliveryTime) {
 * 
 * capacity = _capacity; price = _price; deliveryTime = _deliveryTime; }
 * 

 * 
 * }
 */