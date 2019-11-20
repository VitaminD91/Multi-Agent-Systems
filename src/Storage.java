
public class Storage extends Component {

	public int capacity;
	
	public Storage(int _capacity) {
			
			capacity = _capacity;
		}
		
	public Storage(int _capacity, float _price, int _deliveryTime) {
		
		capacity = _capacity;
		price = _price;
		deliveryTime = _deliveryTime;
	}
	
	@Override
	public String toString() {
		return capacity + "GB" ;
	}

}
