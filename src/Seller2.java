
public class Seller2 {
	
	public void test() {
		
		int deliveryTime = 4;
		
		Storage smallStorage = new Storage(64, 15f, deliveryTime);
		Storage largeStorage = new Storage(256, 40f, deliveryTime);
		Memory smallMemory = new Memory(4, 20f, deliveryTime);
		Memory largeMemory = new Memory(8, 35f, deliveryTime);
	}

}
