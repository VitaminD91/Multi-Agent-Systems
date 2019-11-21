
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.CommunicationsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
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
	
	private  Codec codec = new SLCodec();
	private Ontology ontology = CommunicationsOntology.getInstance();

	// CREATES A HASHMAP OF ORDERS FOR SALE
	private HashMap<String, Float> componentsForSale = new HashMap<>();
	// REFERENCES TICKER AGENT ID
	private AID tickerAgent;
	// CREATES LIST WITH MANUFACTURER ID
	private ArrayList<AID> manufacturers = new ArrayList<>();

	//
	@Override
	protected void setup() {
		
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		System.out.println("Hello Agent "+getAID().getName()+" is ready.");

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
		componentsForSale.put("Large Screen", 0f);
		System.out.println("Supplier "+getAID().getName()+" has "+ componentsForSale.size()+ " component(s)");
		
		addBehaviour(new TickerWaiter(this));
		

	}
	
	public class OffersServer extends CyclicBehaviour {

		public OffersServer(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				String order = msg.getContent();
				if (componentsForSale.containsKey(order)) {
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(componentsForSale.get(order)));
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
				}
				myAgent.send(reply);
			} else {
				block();
			}
		}
	}

	public class EndDayListener extends CyclicBehaviour {
		private int manufacturerFinished = 0;
		private List<Behaviour> toRemove;

		public EndDayListener(Agent a, List<Behaviour> toRemove) {
			super(a);
			this.toRemove = toRemove;
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchContent("done");
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				manufacturerFinished++;
			} else {
				block();
			}
			if (manufacturerFinished == manufacturers.size()) {
				// Order is finished
				ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
				tick.setContent("done");
				tick.addReceiver(tickerAgent);
				myAgent.send(tick);
				// remove behaviours
				for (Behaviour b : toRemove) {
					myAgent.removeBehaviour(b);
				}
				myAgent.removeBehaviour(this);
			}
		}
	}

	public class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL message received. Process it
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
			}
		}
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
				manufacturers.clear();
				DFAgentDescription[] agentsType1 = DFService.search(myAgent, manufacturerTemplate);
				for (int i = 0; i < agentsType1.length; i++) {
					manufacturers.add(agentsType1[i].getName()); // this is the AID
				}
			} catch (FIPAException e) {
				e.printStackTrace();
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
					myAgent.addBehaviour(new FindManufacturer(myAgent));
					CyclicBehaviour os = new OffersServer(myAgent);
					myAgent.addBehaviour(os);
					ArrayList<Behaviour> cyclicBehaviours = new ArrayList<>();
					cyclicBehaviours.add(os);
					myAgent.addBehaviour(new EndDayListener(myAgent, cyclicBehaviours));
				} else {
					myAgent.doDelete();
				}
			} else {
				block();
			}
		}

	}
}