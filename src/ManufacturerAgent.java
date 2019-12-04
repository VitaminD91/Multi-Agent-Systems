
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.BatteryOntology;
import Coursework10111_ontology.CommunicationsOntology;
import Coursework10111_ontology.ComponentOntology;
import Coursework10111_ontology.DeviceOntology;
import Coursework10111_ontology.ManufacturerDeliveryOwns;
import Coursework10111_ontology.ManufacturerOwns;
import Coursework10111_ontology.OrderOntology;
import Coursework10111_ontology.Sell;
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
	private OrderOntology collectedOrder; // should be a list
	private List<ComponentOntology> collectedDelivery;
	private Codec codec = new SLCodec();
	private Ontology ontology = CommunicationsOntology.getInstance();

	@Override
	protected void setup() {
		// adds ontology and codec
		System.out.println(this.getClass().getCanonicalName() + ": " + "created");
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		SupplierAID = new AID("supplier", AID.ISLOCALNAME);

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
			System.out.println("Collect Delivery Contrustor");

			MessageTemplate delivery = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = receive(delivery);
			if (msg != null) {
				try {
					System.out.println("Delivery Received!");
					ContentElement ce = null;

					// let JADE convert from String to Java objects
					// Output will be a ContentElement

					ce = getContentManager().extractContent(msg);
					if (ce instanceof SupplierResponseOwns) {
						List<ComponentOntology> newDelivery = ((SupplierResponseOwns) ce).getComponentList();
						System.out.println(getName() + " received Delivery: " + newDelivery);
						collectedDelivery = newDelivery;
						String smallBattery = "2000mAh";
						String largeBattery = "3000mAh";
						String smallScreen = "5\"";
						String largeScreen = "7\"";
						String smallMemory = "4GB";
						String largeMemory = "8GB";
						String smallStorage = "64GB";
						String largeStorage = "256GB";
						
						

						HashMap<String,Integer> frequencymap = new HashMap<String,Integer>();
						for(int i = 0; i < collectedDelivery.size(); i++){
							
							//SMALL BATTERY
							if(frequencymap.containsKey(smallBattery)) {
								frequencymap.put(smallBattery, frequencymap.get(smallBattery)+1);
							}
							else {
								frequencymap.put(smallBattery, 1);
							}
							
							//LARGE BATTERY
							if(frequencymap.containsKey(largeBattery)) {
								frequencymap.put(largeBattery, frequencymap.get(largeBattery)+1);
							}
							else {
								frequencymap.put(largeBattery, 1);
							}
							
							//SMALL SCREEN
							if(frequencymap.containsKey(smallScreen)) {
								frequencymap.put(smallScreen, frequencymap.get(smallScreen)+1);
							}
							else {
								frequencymap.put(smallScreen, 1);
							}
							
							//LARGE SCREEN
							if(frequencymap.containsKey(largeScreen)) {
								frequencymap.put(largeScreen, frequencymap.get(largeScreen)+1);
							}
							else {
								frequencymap.put(largeScreen, 1);
							}
							
							//SMALL MEMORY
							if(frequencymap.containsKey(smallMemory)) {
								frequencymap.put(smallMemory, frequencymap.get(smallMemory)+1);
							}
							else {
								frequencymap.put(smallMemory, 1);
							}
							
							//LARGE MEMORY
							if(frequencymap.containsKey(largeMemory)) {
								frequencymap.put(largeMemory, frequencymap.get(largeMemory)+1);
							}
							else {
								frequencymap.put(largeMemory, 1);
							}
							
							//SMALL STORAGE
							if(frequencymap.containsKey(smallStorage)) {
								frequencymap.put(smallStorage, frequencymap.get(smallStorage)+1);
							}
							else {
								frequencymap.put(smallStorage, 1);
							}
							
							//LARGE STORAGE
							if(frequencymap.containsKey(largeStorage)) {
								frequencymap.put(largeStorage, frequencymap.get(largeStorage)+1);
							}
							else {
								frequencymap.put(largeStorage, 1);
							}
							
							
						}
						System.out.println("HERE LOOK -----> " + frequencymap);
					

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

	public class CollectOrders extends OneShotBehaviour {

		public CollectOrders(Agent a) {
			super(a);

		}

		@Override
		public void action() {
			System.out.println("Collect Orders Constructor");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = receive(mt);
			if (msg != null) {
				try {
					System.out.println("Message Received!");
					ContentElement ce = null;

					// let JADE convert from String to Java objects
					// Output will be a ContentElement

					ce = getContentManager().extractContent(msg);
					if (ce instanceof ManufacturerOwns) {
						OrderOntology order = ((ManufacturerOwns) ce).getOrder();
						System.out.println(getName() + " received order: " + order);
						collectedOrder = order;

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

			if (collectedOrder != null) {
				ACLMessage enquiry = new ACLMessage(ACLMessage.REQUEST);
				enquiry.addReceiver(SupplierAID);
				enquiry.setLanguage(codec.getName());
				enquiry.setOntology("my_ontology");

				SupplierOwns owns = new SupplierOwns();
				owns.setSupplier(SupplierAID);
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
