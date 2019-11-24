import Coursework10111_ontology.DeviceOntology;
import Coursework10111_ontology.OrderOntology;

public class OrderHelpers {

	public OrderOntology generateOrder() {

		Assembler assembler = new Assembler();
		DeviceOntology device = assembler.getRandomDevice();

		// Quantity of Phones FLOOR(1 + 50 * rand)
		int quantityOfPhones = (int) Math.floor(1 + 50 * Math.random());

		// Unit Price for one phone FLOOR(100 + 500 * rand)
		double unitPrice = Math.floor(100 + 500 * Math.random());

		// Number of days until order due FLOOR(1+10 * rand)
		int orderDueDays = (int) Math.floor(1 + 10 * Math.random());

		// Per-day penalty for late delivery quantity of phone * FLOOR (1 + 50 * rand)
		double latePenalty = quantityOfPhones * Math.floor(1 + 50 * Math.random());
		
		int identificationNumber = 1;

		OrderOntology order = new OrderOntology(identificationNumber, quantityOfPhones, unitPrice, orderDueDays,
				latePenalty, device);
		System.out.println("                                       ");
		System.out.println("Order Generated - ");
		System.out.println("Quantity of phones: " + quantityOfPhones);
		System.out.println("Price per Unit: " + unitPrice);
		System.out.println("Days until order due: " + orderDueDays);
		System.out.println("Penalty per day late: " + latePenalty);
		System.out.println("                                       ");

		return order;

	}

}
