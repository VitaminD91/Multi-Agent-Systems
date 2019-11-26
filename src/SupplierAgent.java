
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.BatteryOntology;
import Coursework10111_ontology.CommunicationsOntology;
import Coursework10111_ontology.DeviceOntology;
import Coursework10111_ontology.MemoryOntology;
import Coursework10111_ontology.OrderOntology;
import Coursework10111_ontology.ScreenOntology;
import Coursework10111_ontology.StorageOntology;
import Coursework10111_ontology.SupplierOwns;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


//***********WHAT I WANT THIS TO DO************//

// Receives request for parts from Manufacturer

// Accepts request

// Generates total cost

// Sends parts to Manufacturer 

//*******************************************//

public class SupplierAgent extends Agent {
	
	public static String AGENT_TYPE = "Supplier";

	private Codec codec = new SLCodec();
	private Ontology ontology = CommunicationsOntology.getInstance();

	// CREATES A HASHMAP OF ORDERS FOR SALE
	private HashMap<Integer, Float> componentsForSale = new HashMap<>();
	// REFERENCES TICKER AGENT ID
	private AID tickerAgent;
	private AID ManufacturerAID;

	//
	@Override
	protected void setup() {

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		System.out.println("Hello Agent " + getAID().getName() + " is ready.");

		// ADD THIS AGENT TO THE YELLOW PAGES
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Supplier");
		sd.setName(getLocalName() + "-Supplier-agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// add components for sale
		componentsForSale.put(2000, 0f);

		addBehaviour(new TickerWaiter(this));
		
	
	}

	public class FindManufacturer extends OneShotBehaviour {

		public FindManufacturer(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			DFAgentDescription manufacturerTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("manufacturer");
			manufacturerTemplate.addServices(sd);
			try {
				DFAgentDescription[] manufacturerAgents = DFService.search(myAgent, manufacturerTemplate);
				/*
				 * LOOPING THROUGH LIST OF MANUFACTURERS AND ADDING TO LIST for (int i = 0; i <
				 * agentsType1.length; i++) { manufacturers.add(agentsType1[i].getName()); //
				 * this is the AID }
				 */
				DFAgentDescription manufacturerInList = manufacturerAgents[0];
				if (manufacturerInList != null) {
					ManufacturerAID = manufacturerInList.getName();
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

	}

	public class ReceiveOrder extends CyclicBehaviour {

		public ReceiveOrder(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				try {
					System.out.println("supplier received message!");
					ContentElement ce = null;

					// let JADE convert from String to Java objects
					// Output will be a ContentElement

					ce = getContentManager().extractContent(msg);
					if (ce instanceof SupplierOwns) {
						OrderOntology order = ((SupplierOwns) ce).getManufacturerOrder();
						System.out.println(getName() + " received order for " + order.getQuantityOfPhones() + " phones");
						DeviceOntology device = order.getDevice();
						int quantity = order.getIdentificationNumber();
						BatteryOntology requiredBattery = device.getBattery();
						ScreenOntology requiredScreen = device.getScreen();
						StorageOntology requiredStorage = device.getStorage();
						MemoryOntology requiredMemory = device.getMemory();
						
						
						
						
						
					}

				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}

			}
		}
	}

	/*
	 * public class PurchaseOrdersServer extends CyclicBehaviour { public void
	 * action() { MessageTemplate mt =
	 * MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL); ACLMessage msg
	 * = myAgent.receive(mt); if (msg != null) { // ACCEPT_PROPOSAL message
	 * received. Process it ACLMessage reply = msg.createReply();
	 * reply.setPerformative(ACLMessage.INFORM); } } }
	 */

	public class EndDayListener extends CyclicBehaviour {
		
		 private Behaviour toRemove;
		 
		

		public EndDayListener(Agent a, Behaviour toRemove) {
			super(a);
			this.toRemove = toRemove;
			/* this.toRemove = toRemove; */}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchContent("done");
				// Order is finished
			ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
			tick.setContent("done");
			tick.addReceiver(tickerAgent);
			myAgent.send(tick);
			// remove behaviours
			
			myAgent.removeBehaviour(toRemove);
			
			myAgent.removeBehaviour(this);
		}
	}

	public class TickerWaiter extends CyclicBehaviour {

		// BEHAVIOUR TO WAIT FOR NEW DAY
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
					myAgent.addBehaviour(new FindManufacturer(myAgent));
					CyclicBehaviour os = new ReceiveOrder(myAgent);
					myAgent.addBehaviour(os);
					ArrayList<Behaviour> cyclicBehaviours = new ArrayList<>();
					cyclicBehaviours.add(os);
					myAgent.addBehaviour(new EndDayListener(myAgent, this));
				} else {
					myAgent.doDelete();
				}
			} else {
				block();
			}
		}

	}
}