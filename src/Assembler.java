import java.util.Random;

public class Assembler {

	public Device getRandomDevice() {

		
		Device myDevice = new Device();
		
		
		if(Math.random() < 0.5) {
			// Small smartphone
			myDevice.screen = new Screen(5);
			myDevice.battery = new Battery(2000);
		} else {
			// phablet (okay boomer)
			myDevice.screen = new Screen(7);
			myDevice.battery = new Battery(3000);
		}
		
		if(Math.random() < 0.5) {
			myDevice.memory = new Memory(4);
		}
		else {
			myDevice.memory = new Memory(8);
		}
		
		if(Math.random() < 0.5) {
			myDevice.storage = new Storage(64);
		}
		else {
			myDevice.storage = new Storage(256);
		}
		
		System.out.println("screen: " + myDevice.screen + "  battery: " + myDevice.battery + " memory: " + myDevice.memory + " storage: " + myDevice.storage);
		
		return myDevice;
	}
}
