package Coursework10111_ontology;
import jade.content.onto.annotations.Slot;

public class ScreenOntology extends ComponentOntology{
	
	private int size;
	private float price;
	private int deliveryTime;
	
	public ScreenOntology() {
		
	}
	
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
	
	//Used for supplier inventory
		public ScreenOntology(int size, float price, int deliveryTime) {
			
			this.size = size;
			this.price = price;
			this.deliveryTime = deliveryTime;
		}
		
		@Slot (mandatory = true)
		public float getPrice() {
			return price;
		}
		
		public void setPrice(float price) {
			this.price = price;
		}
		
		@Slot (mandatory = true)
		public int getDeliveryTime() {
			return deliveryTime;
		}
		
		
		public void setDeliveryTime(int deliveryTime) {
			this.deliveryTime = deliveryTime;
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