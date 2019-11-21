
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
	private ArrayList<AID> manufacturers = new ArrayList<>();
	private AID manufactuerAID;

	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

	}
	
	class GenerateOrder extends OneShotBehaviour {

		@Override
		public void action() {

			OrderHelpers ordermanager = new OrderHelpers();
			OrderOntology order = ordermanager.generateOrder();
			System.out.println(getName() + " created order: ");

			ACLMessage enquiry = new ACLMessage(ACLMessage.CFP);
			enquiry.setLanguage(codec.getName());
			enquiry.setOntology("my_ontology");
			// prepare content
			Owns owns = new Owns();
			owns.setManufacturer(manufactuerAID);
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

	protected void takeDown() {

	}

}
