import jade.core.Agent;

import java.util.ArrayList;
import java.util.HashMap;

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
	private ArrayList<AID> manufacturers = new ArrayList<>();
	
	
	protected void setup() {
	
		
		class GenerateOrder extends OneShotBehaviour {
			
			@Override
			public void action() {
				
				OrderHelpers ordermanager = new OrderHelpers();
				Order order = ordermanager.generateOrder();
		
				}
			}
	}
	
	
	
	protected void takeDown() {
		
	}

}
