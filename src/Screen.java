
public class Screen extends Component {

	public int size;

	public Screen(int _size) {

		size = _size;

	}

	public Screen(int _size, float _price, int _deliveryTime) {

		size = _size;
		price = _price;
		deliveryTime = _deliveryTime;

	}
	
	@Override
	public String toString() {
		return size + "\"" ;
	}

}
