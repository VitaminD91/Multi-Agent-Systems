
import jade.core.Agent;

import java.util.ArrayList;
import java.util.HashMap;

import Coursework10111_ontology.CommunicationsOntology;
import Coursework10111_ontology.OrderOntology;
import Coursework10111_ontology.Owns;
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
	private Codec codec = new SLCodec();
	private Ontology ontology = CommunicationsOntology.getInstance();
	private AID manufacturerAID;

	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		manufacturerAID = new AID("manufacturer", AID.ISLOCALNAME);
		addBehaviour(new GenerateOrder());
		/* addBehaviour(new SendOrder()); */
		
		System.out.println("Hello Agent "+getAID().getName()+" is ready.");

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

	class GenerateOrder extends OneShotBehaviour {

		@Override
		public void action() {

			ACLMessage enquiry = new ACLMessage(ACLMessage.REQUEST);
			enquiry.addReceiver(manufacturerAID);
			enquiry.setLanguage(codec.getName());
			enquiry.setOntology("my_ontology");
			// prepare content
			// Generates an order with random specs
			OrderHelpers ordermanager = new OrderHelpers();
			OrderOntology order = ordermanager.generateOrder();
			System.out.println(getName() + " created order: " + order);
			
			Owns owns = new Owns();
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

	/*
	 * class SendOrder extends OneShotBehaviour {
	 * 
	 * 
	 * @Override public void action() { // prepare the action request message
	 * ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
	 * msg.addReceiver(manufacturerAID); msg.setLanguage(codec.getName());
	 * msg.setOntology(getName()); // prepare the content
	 * 
	 * 
	 * Action enquiry = new Action(); enquiry.setAction(enquiry);
	 * enquiry.setActor(manufacturerAID); try { // let JADE convert from java
	 * objexts to string getContentManager().fillContent(msg, enquiry); // send the
	 * wrapper object send(msg); } catch (CodecException ce) { ce.printStackTrace();
	 * } catch (OntologyException oe) { oe.printStackTrace(); }
	 * 
	 * } }
	 */

	protected void takeDown() {

	}

}
