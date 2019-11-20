
public class Seller1 {

	public void test() {
		
		int deliveryTime = 1;
		
		Battery smallBattery = new Battery(2000, 70f, deliveryTime);
		Battery largeBattery = new Battery(3000, 100f, deliveryTime);
		Screen smallScreen = new Screen(5, 100f, deliveryTime);
		Screen largeScreen = new Screen(7, 150f, deliveryTime);
		Storage smallStorage = new Storage(64, 25f, deliveryTime);
		Storage largeStorage = new Storage(256, 50f, deliveryTime);
		Memory smallMemory = new Memory(4, 30f, deliveryTime);
		Memory largeMemory = new Memory(8, 60f, deliveryTime);	
	
	}
}
