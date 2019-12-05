package Coursework10111_ontology;
import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class OrderOntology implements Concept {
	
	private int identificationNumber;
	private int quantityOfPhones;
	private double unitPrice;
	private int orderDueDays;
	private double latePenalty;
	private DeviceOntology device;
	private int daysWaited;

	public OrderOntology(){
		
	}
	
	
	public Integer getDaysWaited() {
		return daysWaited;
	}
	
	public void setDaysWaited(Integer daysWaited) {
		this.daysWaited = daysWaited;
	}
	
	
	public OrderOntology(int identificationNumber, int quantityOfPhones, double unitPrice,
			int orderDueDays, double latePenalty, DeviceOntology device) {
		
		this.identificationNumber = identificationNumber;
		this.quantityOfPhones = quantityOfPhones;
		this.unitPrice = unitPrice;
		this.orderDueDays = orderDueDays;
		this.latePenalty = latePenalty;
		this.device = device;
	}
	
	@Slot (mandatory = true)
	public Integer getQuantityOfPhones() {
		return quantityOfPhones;
	}
	
	public void setQuantityOfPhones(Integer quantityOfPhones) {
		this.quantityOfPhones = quantityOfPhones;
	}
	
	@Slot (mandatory = true)
	public double getUnitPrice() {
		return unitPrice;
	}
	
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	@Slot (mandatory = true)
	public int getOrderDueDays() {
		return orderDueDays;
	}
	
	public void setOrderDueDays(int orderDueDays) {
		this.orderDueDays = orderDueDays;
	}
	
	@Slot (mandatory = true)
	public double getLatePenalty() {
		return latePenalty;
	}
	
	public void setLatePenalty(double latePenalty) {
		this.latePenalty = latePenalty;
	}
	
	@Slot (mandatory = true)
	public int getIdentificationNumber() {
		return identificationNumber;
	}
	
	public void setIdentification(int identificationNumber) {
		
		this.identificationNumber = identificationNumber;
	}
	
	@Slot (mandatory = true)
	public DeviceOntology getDevice() {
		return device;
	}
	
	public void setDevice(DeviceOntology device) {
		this.device = device;
	}
	
	 @Override public String toString() {
		 return "| Order Number - " + identificationNumber + " | " + device +
				 " | Quantity:" + quantityOfPhones + " Price:" + unitPrice + 
				 "  Days Until Order Due:" + orderDueDays + 
				 "  Cost per day late:" + latePenalty; 
		 }

	
	
	
}