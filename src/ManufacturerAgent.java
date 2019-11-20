import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;

public class ManufacturerAgent extends Agent{
	
	// Receives order from Customers
	
	// Checks Warehouse Parts, Time available and Late Fees
	
	// Decides which orders to accept
	
		// IF any orders can be created with current stock
				// ACCEPT
		
		// IF 2 orders (or more) quantitiesOfPhones < 50
				// ACCEPT (if profits > cost)
	
		// IF 1 order quantitiesOfPhones < 50 & profits > than other orders
				// ACCEPT 
	
	// Generates list of parts required for orders
	
	// Generates numberOfDaysLeft for Order
	
		// IF (numberOfDaysLeft  <= 2 ) { SUPPLIER = 1}
		// IF (numberOfDaysLeft > 2 && < 4 && Order !contain screens || batteries) 
		//	  { SUPPLIER = 2 }
		// ELSE { SUPPLIER = 1 }
	
	// Sends request to Suppliers for parts
	
		// Supplier accepts and parts are added at start of new day
	
	// Construct phones based on days left to deliver 
	
		// IF Late Fee for one order is < then other, work on highest late fee 
	
	// When Order complete, it is sent on the same day
	
	// Profit generated (TotalValueShipped(d) - PenaltyForLateOrders(d) - WarehouseStorage(d)
	//						- SuppliesPurchased(d) )

		
	

}
