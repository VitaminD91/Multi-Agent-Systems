
import jade.core.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Coursework10111_ontology.CustomerOntology;
import Coursework10111_ontology.OrderOntology;
import Coursework10111_ontology.ManufacturerOwns;
import jade.content.abs.AbsContentElement;
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

// Generate an order

// Send order to Manufacturer

// takeDown on completed order

public class CustomerAgent extends Agent {

	public static String AGENT_TYPE = "Customer";

	private Codec codec = new SLCodec();
	private Ontology ontology = CustomerOntology.getInstance();
	private AID manufacturerAID;
	private AID tickerAgent;

	protected void setup() {
		System.out.println(this.getClass().getCanonicalName() + ": " + "created");

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		manufacturerAID = new AID("manufacturer", AID.ISLOCALNAME);
		addBehaviour(new TickerWaiter(this));
		

		System.out.println("Hello Agent " + getAID().getName() + " is ready.");

		// add this agent to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Customer");
		sd.setName(getLocalName() + "-customer-agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
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
			//System.out.println("Customer Ticker Waiter");
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
	
						dailyActivity.addSubBehaviour(new GenerateOrder());
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

	class GenerateOrder extends OneShotBehaviour {

		@Override
		public void action() {
			System.out.println("Customer Agent GenerateOrder");

			ACLMessage enquiry = new ACLMessage(ACLMessage.REQUEST);
			enquiry.addReceiver(manufacturerAID);
			enquiry.setLanguage(codec.getName());
			enquiry.setOntology(ontology.getName());
			// prepare content
			// Generates an order with random specs
			OrderHelpers ordermanager = new OrderHelpers();
			OrderOntology order = ordermanager.generateOrder();
			System.out.println(getName() + " created order: " + order);

			ManufacturerOwns owns = new ManufacturerOwns();
			owns.setManufacturer(manufacturerAID);
			owns.setOrder(order);
			try {
				// Let JADE convert from Java objects to string
				getContentManager().fillContent(enquiry, owns);
				send(enquiry);

			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
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

	protected void takeDown() {

	}

}
