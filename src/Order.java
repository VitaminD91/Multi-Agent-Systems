
public class Order {
	public int quantityOfPhones;
	public double unitPrice;
	public int orderDueDays;
	public double latePenalty;
	public Device device;
	
	public Order() {
		
	}
	
	public Order(int quantityOfPhones, double unitPrice, int orderDueDays, double latePenalty, Device device) {
		this.quantityOfPhones = quantityOfPhones;
		this.unitPrice = unitPrice;
		this.orderDueDays = orderDueDays;
		this.latePenalty = latePenalty;
		this.device = device;
	}

}
 