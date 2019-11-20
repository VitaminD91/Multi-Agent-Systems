
public class Battery extends Component{
	
	public int capacity;
	
	public Battery(int _capacity) {
		
		capacity = _capacity;
	}
	
	public Battery(int _capacity, float _price, int _deliveryTime) {
		
		capacity = _capacity;
		price = _price;
		deliveryTime = _deliveryTime;
	}
	
	@Override
	public String toString() {
		return capacity + "mAh" ;
	}

	
}
