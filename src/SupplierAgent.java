
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.BatteryOntology;
import Coursework10111_ontology.ComponentOntology;
import Coursework10111_ontology.DeviceOntology;
import Coursework10111_ontology.ManufacturerOntology;
import Coursework10111_ontology.MemoryOntology;
import Coursework10111_ontology.OrderOntology;
import Coursework10111_ontology.ScreenOntology;
import Coursework10111_ontology.StorageOntology;
import Coursework10111_ontology.SupplierOntology;
import Coursework10111_ontology.SupplierOwns;
import Coursework10111_ontology.SupplierResponseOwns;
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
	private Ontology ontology = SupplierOntology.getInstance();
	// REFERENCES TICKER AGENT ID
	private AID tickerAgent;
	private AID ManufacturerAID;

	//i DON'T LIKE THIS
	@Override
	protected void setup() {

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		getContentManager().registerOntology(ManufacturerOntology.getInstance());
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
					System.out.println(this.getClass().getCanonicalName() + ": " + "Received Order");
					ContentElement ce = null;

					// let JADE convert from String to Java objects
					// Output will be a ContentElement

					ce = getContentManager().extractContent(msg);
					if (ce instanceof SupplierOwns) {
						OrderOntology order = ((SupplierOwns) ce).getManufacturerOrder();
						int quantity = order.getQuantityOfPhones();

						DeviceOntology device = order.getDevice();
						BatteryOntology requiredBattery = device.getBattery();
						ScreenOntology requiredScreen = device.getScreen();
						StorageOntology requiredStorage = device.getStorage();
						MemoryOntology requiredMemory = device.getMemory();
						
						List<ComponentOntology> componentOrder = new ArrayList();
						
						for(int i = 0; i < quantity; i++ ) {
							componentOrder.add(requiredBattery);
							componentOrder.add(requiredScreen);
							componentOrder.add(requiredStorage);
							componentOrder.add(requiredMemory);
						}
						
						if(componentOrder != null) {
							ACLMessage shipment = new ACLMessage(ACLMessage.REQUEST);
							shipment.addReceiver(ManufacturerAID);
							shipment.setLanguage(codec.getName());
							shipment.setOntology(ontology.getName());

							SupplierResponseOwns owns = new SupplierResponseOwns();
							owns.setManufacturer(ManufacturerAID);
							owns.setComponentList(componentOrder);
							getContentManager().fillContent(shipment, owns);
							// Shipment sends the components, with price and delivery time for each
							send(shipment);
						}
						
					}

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
					System.out.println(this.getClass().getCanonicalName() + ": " + "Received new day");
					
					myAgent.addBehaviour(new FindManufacturer(myAgent));
					CyclicBehaviour os = new ReceiveOrder(myAgent);
					myAgent.addBehaviour(os);
					ArrayList<Behaviour> cyclicBehaviours = new ArrayList<>();
					cyclicBehaviours.add(os);
					myAgent.addBehaviour(new EndDay(myAgent));
				} else {
					myAgent.doDelete();
				}
			} else {
				block();
			}
		}

	}
}