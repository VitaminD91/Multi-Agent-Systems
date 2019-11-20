
public class Memory extends Component{

	public int capacity;
	
	public Memory(int _capacity) {
		
		capacity = _capacity;
	}
	
	public Memory(int _capacity, float _price, int _deliveryTime) {
		
		capacity = _capacity;
		price = _price;
		deliveryTime = _deliveryTime;
	}
	
	@Override
	public String toString() {
		return capacity + "GB" ;
	}

}
