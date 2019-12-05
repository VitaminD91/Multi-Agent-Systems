
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.BatteryOntology;
import Coursework10111_ontology.CommunicationsOntology;
import Coursework10111_ontology.ComponentOntology;
import Coursework10111_ontology.DeviceOntology;
import Coursework10111_ontology.MemoryOntology;
import Coursework10111_ontology.OrderOntology;
import Coursework10111_ontology.ScreenOntology;
import Coursework10111_ontology.StorageOntology;
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


public class SupplierAgent extends Agent {

	public static String AGENT_TYPE = "Supplier";

	private Codec codec = new SLCodec();
	private Ontology ontology = CommunicationsOntology.getInstance();
	// REFERENCES TICKER AGENT ID
	private AID tickerAgent;
	private AID ManufacturerAID;
	private ComponentOntology[] componentsForSale;

	@Override
	protected void setup() {
		componentsForSale = (ComponentOntology[]) getArguments();
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
					System.out.println(this.getClass().getCanonicalName() + ": " + "Received Order");
					ContentElement ce = null;

					// let JADE convert from String to Java objects
					// Output will be a ContentElement

					ce = getContentManager().extractContent(msg);
					if (ce instanceof SupplierOwns) {
						OrderOntology order = ((SupplierOwns) ce).getManufacturerOrder();
						int quantity = order.getQuantityOfPhones();
						System.out
								.println(getName() + " received order for " + order.getQuantityOfPhones() + " phones");
						DeviceOntology device = order.getDevice();
						BatteryOntology requiredBattery = device.getBattery();
						ScreenOntology requiredScreen = device.getScreen();
						StorageOntology requiredStorage = device.getStorage();
						MemoryOntology requiredMemory = device.getMemory();

						for (ComponentOntology c : componentsForSale) {
							if (c instanceof BatteryOntology
									&& ((BatteryOntology) c).getCapacity() == requiredBattery.getCapacity()) {
								requiredBattery.setPrice(c.getPrice());
								requiredBattery.setDeliveryTime(c.getDeliveryTime());
							}
							if (c instanceof ScreenOntology
									&& ((ScreenOntology) c).getSize() == requiredScreen.getSize()) {
								requiredScreen.setPrice(c.getPrice());
								requiredScreen.setDeliveryTime(c.getDeliveryTime());
							}
							if (c instanceof StorageOntology
									&& ((StorageOntology) c).getCapacity() == requiredStorage.getCapacity()) {
								requiredStorage.setPrice(c.getPrice());
								requiredStorage.setDeliveryTime(c.getDeliveryTime());
							}
							if (c instanceof MemoryOntology
									&& ((MemoryOntology) c).getCapacity() == requiredMemory.getCapacity()) {
								requiredMemory.setPrice(c.getPrice());
								requiredMemory.setDeliveryTime(c.getDeliveryTime());
							}
						}

						List<ComponentOntology> componentOrder = new ArrayList();

						for (int i = 0; i < quantity; i++) {
							componentOrder.add(requiredBattery);
							componentOrder.add(requiredScreen);
							componentOrder.add(requiredStorage);
							componentOrder.add(requiredMemory);
						}

						if (componentOrder != null) {
							ACLMessage shipment = new ACLMessage(ACLMessage.REQUEST);
							shipment.addReceiver(ManufacturerAID);
							shipment.setLanguage(codec.getName());
							shipment.setOntology("my_ontology");

							SupplierResponseOwns owns = new SupplierResponseOwns();
							owns.setManufacturer(ManufacturerAID);
							owns.setComponentList(componentOrder);
							getContentManager().fillContent(shipment, owns);
							// Shipment sends the components, with price and delivery time for each
							System.out.println("DARREN LOOK AT THIS ------> " + shipment);
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

	public class EndDayListener extends CyclicBehaviour {

		private Behaviour toRemove;

		public EndDayListener(Agent a, Behaviour toRemove) {
			super(a);
			this.toRemove = toRemove;
			/* this.toRemove = toRemove; */}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchContent("done");
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Order is finished
				ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
				tick.setContent("done");
				tick.addReceiver(tickerAgent);
				myAgent.send(tick);
				// remove behaviours

				/* myAgent.removeBehaviour(toRemove); */

				myAgent.removeBehaviour(this);
			}
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
					// SendPreviousOrders - FindManufacturer(), find AID of sender, reply with list,
					// wipe hashmap
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