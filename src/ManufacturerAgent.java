
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.BatteryOntology;
import Coursework10111_ontology.ComponentOntology;
import Coursework10111_ontology.CustomerOntology;
import Coursework10111_ontology.DeviceOntology;
import Coursework10111_ontology.ManufacturerDeliveryOwns;
import Coursework10111_ontology.ManufacturerOntology;
import Coursework10111_ontology.ManufacturerOwns;
import Coursework10111_ontology.MemoryOntology;
import Coursework10111_ontology.OrderOntology;
import Coursework10111_ontology.ScreenOntology;
import Coursework10111_ontology.Sell;
import Coursework10111_ontology.StorageOntology;
import Coursework10111_ontology.SupplierOntology;
import Coursework10111_ontology.SupplierOwns;
import Coursework10111_ontology.SupplierResponseOwns;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
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

	public static String AGENT_TYPE = "Manufacturer";

	// Creates and array list of agent IDs for suppliers
	private ArrayList<AID> suppliers = new ArrayList<>();
	// Creates an array list of components to buy
	private ArrayList<String> componentsToBuy = new ArrayList<>();
	private AID tickerAgent;
	private AID CustomerAgent;
	private AID SupplierAID;
	private int numQueriesSent;
	private List<OrderOntology> collectedOrders = new ArrayList<OrderOntology>();
	private List<ComponentOntology> collectedDelivery;

	private HashMap<String, Integer> Warehouse = new HashMap<String, Integer>();

	private Codec codec = new SLCodec();
	private Ontology ontology = ManufacturerOntology.getInstance();
	private double totalProfit = 0;

	@Override
	protected void setup() {
		// adds ontology and codec
		System.out.println(this.getClass().getCanonicalName() + ": " + "created");
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		getContentManager().registerOntology(CustomerOntology.getInstance());
		getContentManager().registerOntology(SupplierOntology.getInstance());
		CustomerAgent = new AID("customer", AID.ISLOCALNAME);


		System.out.println("Hello Agent " + getAID().getName() + " is ready.");

		// add this agent to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(AGENT_TYPE);
		sd.setName(getLocalName() + "-manufacturer-agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

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
				}
				if (msg.getContent().equals("new day")) {
					System.out.println(this.getClass().getCanonicalName() + ": " + "Received new day");
					// spawn new sequential behaviour for day's activities
					SequentialBehaviour dailyActivity = new SequentialBehaviour();
					// sub-behaviours will execute in the order they are added
					dailyActivity.addSubBehaviour(new CollectDelivery(myAgent));
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

	public class CollectDelivery extends OneShotBehaviour {

		public CollectDelivery(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			System.out.println("Collect Delivery action");

			MessageTemplate delivery = MessageTemplate.MatchOntology(SupplierOntology.getInstance().getName());
			ACLMessage msg = receive(delivery);
			if (msg != null) {
				try {
					ContentElement ce = null;

					// let JADE convert from String to Java objects
					// Output will be a ContentElement

					ce = getContentManager().extractContent(msg);
					if (ce instanceof SupplierResponseOwns) {
						List<ComponentOntology> newDelivery = ((SupplierResponseOwns) ce).getComponentList();
						System.out.println(getName() + " received Delivery: " + newDelivery);
						collectedDelivery = newDelivery;
						float totalCost = 0f;
						for (int i = 0; i < collectedDelivery.size(); i++) {
							totalCost += collectedDelivery.get(i).getPrice();
							IncrementWarehouse(collectedDelivery.get(i).toString());
						}
						totalProfit -= totalCost;
						System.out.println(
								"Total cost: £" + totalCost + " for " + collectedDelivery.size() + " Components");


					}
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}
			} else {
				// block();
			}
		}

	}

	private void IncrementWarehouse(String componentName) {

		if (Warehouse.containsKey(componentName)) {
			Warehouse.put(componentName, Warehouse.get(componentName) + 1);

		} else {
			Warehouse.put(componentName, 1);
		}

	}

	public class CollectOrders extends OneShotBehaviour {

		public CollectOrders(Agent a) {
			super(a);

		}

		@Override
		public void action() {
			System.out.println("Collect Orders action");
			try {
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			MessageTemplate mt = MessageTemplate.MatchOntology(CustomerOntology.getInstance().getName());
			ACLMessage msg = receive(mt);

			if (msg != null) {
				System.out.println(msg.getOntology());
				try {
					System.out.println("Order Received! " + msg);

					ContentElement ce = null;

					// let JADE convert from String to Java objects
					// Output will be a ContentElement

					ce = getContentManager().extractContent(msg);
					if (ce instanceof ManufacturerOwns) {
						OrderOntology order = ((ManufacturerOwns) ce).getOrder();
						System.out.println(getName() + " received order: " + order);
						collectedOrders.add(order);

					}
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}
			} else {
				 block();

			}
		}
	}

	public class AssembleAvailableOrders extends OneShotBehaviour {

		public AssembleAvailableOrders(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			System.out.println("Assemble Available Orders Action");
			
			if (Warehouse.isEmpty()) {
				System.out.println("Warehouse is empty");
				for (OrderOntology collectedOrder : collectedOrders) {
					collectedOrder.setDaysWaited(collectedOrder.getDaysWaited()+1);
				}
				return;
			}
			List<OrderOntology> processedOrders = new ArrayList<OrderOntology>();
			for (OrderOntology collectedOrder : collectedOrders) {
				
				BatteryOntology desiredBattery = collectedOrder.getDevice().getBattery();
				ScreenOntology desiredScreen = collectedOrder.getDevice().getScreen();
				MemoryOntology desiredMemory = collectedOrder.getDevice().getMemory();
				StorageOntology desiredStorage = collectedOrder.getDevice().getStorage();
				
				int batteriesInWarehouse = Warehouse.get(desiredBattery.toString()) != null 
						? Warehouse.get(desiredBattery.toString()) : 0;
				int screensInWarehouse = Warehouse.get(desiredScreen.toString()) != null 
						? Warehouse.get(desiredScreen.toString()) : 0;
				int memoryInWarehouse = Warehouse.get(desiredMemory.toString()) != null 
						? Warehouse.get(desiredMemory.toString()) : 0;
				int storageInWarehouse = Warehouse.get(desiredStorage.toString()) != null 
						? Warehouse.get(desiredStorage.toString()) : 0;

				int quantityNeeded = collectedOrder.getQuantityOfPhones();
				if (batteriesInWarehouse >= quantityNeeded && screensInWarehouse >= quantityNeeded
						&& memoryInWarehouse >= quantityNeeded && storageInWarehouse >= quantityNeeded) {

					double pricePerPhone = collectedOrder.getUnitPrice();
					double totalGain = pricePerPhone * quantityNeeded;
					double totalLateFee = 0;
					int daysOverdue = collectedOrder.getDaysWaited() - collectedOrder.getOrderDueDays(); 
					if(daysOverdue > 0) {
						totalLateFee = collectedOrder.getLatePenalty() * daysOverdue;
					}
					processedOrders.add(collectedOrder);
					
					totalProfit += totalGain - totalLateFee;
							
					System.out.println("Sold " + quantityNeeded + " phones for £" + pricePerPhone
							+ " Total Gain: £" + totalGain + ". Late Fees: £" + totalLateFee);
				} else {
					System.out.println("Not enough components to assemble phone, awaiting next delivery");
					collectedOrder.setDaysWaited(collectedOrder.getDaysWaited()+1);
				} 
			}
			
			collectedOrders.removeAll(processedOrders);

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
			} catch (FIPAException e) {
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

			for (OrderOntology collectedOrder : collectedOrders) {
				if (collectedOrder != null) {
					ACLMessage enquiry = new ACLMessage(ACLMessage.REQUEST);
					enquiry.addReceiver(getAID("supplier1"));
					enquiry.setLanguage(codec.getName());
					enquiry.setOntology(ontology.getName());

					SupplierOwns owns = new SupplierOwns();
					owns.setSupplier(getAID("supplier1"));
					owns.setManufacturerOrder(collectedOrder);
					try {

						getContentManager().fillContent(enquiry, owns);
						send(enquiry);
					} catch (CodecException ce) {
						ce.printStackTrace();
					} catch (OntologyException oe) {
						oe.printStackTrace();
					}

				}

			}
		}
	}

	public class EndDay extends OneShotBehaviour {

		public EndDay(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			System.out.println("Manufacturer Profit Updated!: £" + totalProfit);
			
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
			myAgent.removeBehaviour(this);
		}

	}

}
