package Coursework10111_ontology;
import jade.content.onto.annotations.Slot;

public class ScreenOntology extends ComponentOntology{
	
	private int size;
	@Slot (mandatory = true)
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		
		this.size = size;
	}
	
	public ScreenOntology(int size) {
		
		this.size = size;
	}
	
	 @Override public String toString() {
		 return size + "\"" ; 
		 }

}






/*This code old af
 * public class ScreenOntology extends Component {
 * 
 * public int size;
 * 
 * public ScreenOntology(int _size) {
 * 
 * size = _size;
 * 
 * }
 * 
 * public ScreenOntology(int _size, float _price, int _deliveryTime) {
 * 
 * size = _size; price = _price; deliveryTime = _deliveryTime;
 * 
 * }
 * 
 * * 
 * }
 */