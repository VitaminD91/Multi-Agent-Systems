
public class OrderManager {
	
	public void generateOrder() {
		
		Assembler assembler = new Assembler();
		assembler.getRandomDevice();
	
		
		// Quantity of Phones FLOOR(1 + 50 * rand)
		int quantityOfPhones = (int) Math.floor(1 + 50 * Math.random());
		
		// Unit Price for one phone FLOOR(100 + 500 * rand)
		double unitPrice = Math.floor(100 + 500 * Math.random());
	
		// Number of days until order due FLOOR(1+10 * rand)
		int orderDueDays = (int) Math.floor(1 + 10 * Math.random());
		
		// Per-day penalty for late delivery quantity of phone * FLOOR (1 + 50 * rand)
		double latePenalty = quantityOfPhones * Math.floor(1 + 50 * Math.random());
		
		
		System.out.println("Quantity of phones: " + quantityOfPhones);
		System.out.println("Price per Unit: " + unitPrice);
		System.out.println("Days until order due: " + orderDueDays);
		System.out.println("Penalty per day late: " + latePenalty);
	}

}
