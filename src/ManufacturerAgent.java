
import java.util.ArrayList;
import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

//*******************************WHAT I WANT THIS TO DO************************************//
// Receives order from Customers

// Checks Warehouse Parts, Time available and Late Fees

// Decides which orders to accept

// IF any orders can be created with current stock
// ACCEPT

// IF 2 orders (or more) quantitiesOfPhones <= 50
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

//****************************************************************************************//

public class ManufacturerAgent extends Agent {

	// Creates and array list of agent IDs for suppliers
	private ArrayList<AID> suppliers = new ArrayList<>();
	// Creates an array list of components to buy
	private ArrayList<String> componentsToBuy = new ArrayList<>();
	private AID tickerAgent;
	private int numQueriesSent;

	@Override
	protected void setup() {
		// add this agent to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Manufacturer");
		sd.setName(getLocalName() + "-manufacturer-agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// add components to buy
		componentsToBuy.add("Large Screen");
		// componentsToBuy.add(screen.size * quantityOfPhones);
		// componentsToBuy.add(battery.capacity * quantityOfPhones);
		// componentsToBuy.add(memory.capacity * quantityOfPhones);
		// componentsToBuy.add(storage.capacity * quantityOfPhones);

		addBehaviour(new TickerWaiter(this));
	}

	@Override
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	public class TickerWaiter extends CyclicBehaviour {

		// behaviour to wait for new day
		public TickerWaiter(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchContent("new day"),
					MessageTemplate.MatchContent("terminate"));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				if (tickerAgent == null) {
					tickerAgent = msg.getSender();

					if (msg.getContent().equals("new day")) {
						// spawn new sequential behaviour for day's activities
						SequentialBehaviour dailyActivity = new SequentialBehaviour();
						// sub-behaviours will execute in the order they are added
						dailyActivity.addSubBehaviour(new CollectOrders(myAgent));
						dailyActivity.addSubBehaviour(new AssembleAvailableOrders(myAgent));
						dailyActivity.addSubBehaviour(new FindSuppliers(myAgent));
						dailyActivity.addSubBehaviour(new SendComponentOrder(myAgent));
						dailyActivity.addSubBehaviour(new EndDay(myAgent));
						myAgent.addBehaviour(dailyActivity);
					} else {
						// Terminate message to end simulation
						myAgent.doDelete();
					}
				} else {
					block();
				}
			}
		}
	}

	public class CollectOrders extends OneShotBehaviour {

		public CollectOrders(Agent a) {
			super(a);
			System.out.println("Collect Orders Constructor");
		}

		@Override
		public void action() {
			System.out.println("Collect Orders Action");
		}
	}

	public class AssembleAvailableOrders extends OneShotBehaviour {

		public AssembleAvailableOrders(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			System.out.println("Assemble Available Orders Action");
		}
	}

	public class FindSuppliers extends OneShotBehaviour {

		public FindSuppliers(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			DFAgentDescription supplierTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("supplier");
			supplierTemplate.addServices(sd);
			try {
				suppliers.clear();
				DFAgentDescription[] agentsType1 = DFService.search(myAgent, supplierTemplate);
				for (int i = 0; i < agentsType1.length; i++) {
					suppliers.add(agentsType1[i].getName());
				}
			}
			catch (FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class SendComponentOrder extends OneShotBehaviour {

		public SendComponentOrder(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			//send out a call for components
			numQueriesSent = 0;
			for(String componentName : componentsToBuy) {
				ACLMessage enquiry = new ACLMessage(ACLMessage.CFP);
				enquiry.setContent(componentName);
				enquiry.setConversationId(componentName);
				for (AID supplier : suppliers) {
					enquiry.addReceiver(supplier);
					numQueriesSent++;
				}
				myAgent.send(enquiry);
			}
		}
	}

	public class EndDay extends OneShotBehaviour {

		public EndDay(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(tickerAgent);
			msg.setContent("done");
			myAgent.send(msg);
			// Send a message to each supplier that we have finished
			ACLMessage supplierDone = new ACLMessage(ACLMessage.INFORM);
			supplierDone.setContent("done");
			for (AID supplier : suppliers) {
				supplierDone.addReceiver(supplier);
			}
			myAgent.send(supplierDone);
		}

	}

}


