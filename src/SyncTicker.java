
	import java.util.ArrayList;

	import jade.core.AID;
	import jade.core.Agent;
	import jade.core.behaviours.Behaviour;
	import jade.domain.DFService;
	import jade.domain.FIPAException;
	import jade.domain.FIPAAgentManagement.DFAgentDescription;
	import jade.domain.FIPAAgentManagement.ServiceDescription;
	import jade.lang.acl.ACLMessage;
	import jade.lang.acl.MessageTemplate;

	public class SyncTicker extends Agent {
		public static final int NUM_DAYS = 100;

		@Override
		protected void setup() {
			System.out.println("setup");
			// add this agent to the yellow pages
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("ticker-agent");
			sd.setName(getLocalName() + "-ticker-agent");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
			// wait for the other agents to start
			doWait(5000);
			addBehaviour(new SynchAgentsBehaviour(this));
		}

		@Override
		protected void takeDown() {
			// deregister from the yellow pages
			try {
				DFService.deregister(this);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

		public class SynchAgentsBehaviour extends Behaviour {
			private int step = 0;
			private int numFinReceived = 0; // finished messages from other agents
			private int day = 0;
			private ArrayList<AID> simulationAgents = new ArrayList<>();

			public SynchAgentsBehaviour(Agent a) {
				super(a);
			}

			@Override
			public void action() {
//				System.out.println("Sync Agents Action");
				switch (step) {
					case 0:
						sendNewDayToAgents();
						break;
					case 1:
						waitForDoneAgents();
						break;
				}
			}

			private void waitForDoneAgents() {
//				System.out.println("wait for done");
				// wait to receive a "done" message from all agents
				MessageTemplate mt = MessageTemplate.MatchContent("done");
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					numFinReceived++;
					if (numFinReceived >= simulationAgents.size()) {
						step++;
					}
				} else {
					block();
				}
			}

			private void sendNewDayToAgents() {
//				System.out.println("send new day");
				System.out.println("");
				System.out.println("---------Start of Day " + (day+1) + "------------");
				System.out.println("");
				// find all agents using dictionary service
				DFAgentDescription manufacturerTemplate = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(ManufacturerAgent.AGENT_TYPE);
				manufacturerTemplate.addServices(sd);
				DFAgentDescription supplierTemplate = new DFAgentDescription();
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType(SupplierAgent.AGENT_TYPE);
				supplierTemplate.addServices(sd2);
				DFAgentDescription customerTemplate = new DFAgentDescription();
				ServiceDescription sd3 = new ServiceDescription();
				sd3.setType(CustomerAgent.AGENT_TYPE);
				customerTemplate.addServices(sd3);
				try {
					DFAgentDescription[] manufacturerAgents = DFService.search(myAgent, manufacturerTemplate);
					for (int i = 0; i < manufacturerAgents.length; i++) {
						simulationAgents.add(manufacturerAgents[i].getName()); // this is the AID
					}
					DFAgentDescription[] supplierAgents = DFService.search(myAgent, supplierTemplate);
					for (int i = 0; i < supplierAgents.length; i++) {
						simulationAgents.add(supplierAgents[i].getName()); // this is the AID
					}
					DFAgentDescription[] customerAgents = DFService.search(myAgent, customerTemplate);
					for (int i = 0; i < customerAgents.length; i++) {
						simulationAgents.add(customerAgents[i].getName());
					}
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				// send new day message to each agent
				ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
				tick.setContent("new day");
				System.out.println("SENDING NEW DAY - TICK = " + tick);
				for (AID id : simulationAgents) {
					tick.addReceiver(id);
					System.out.println(this.getClass().getCanonicalName() + ": " + "SENDING TICK TO AGENT " + id);
				}
				myAgent.send(tick);
				step++;
				day++;
			}

			@Override
			public boolean done() {
				return step == 2;
			}

			@Override
			public void reset() {
				super.reset();
				step = 0;
				simulationAgents.clear();
				numFinReceived = 0;
			}

			@Override
			public int onEnd() {
				System.out.println("");
				System.out.println("---------End of day " + day + "------------");
				System.out.println("");
				if (day == NUM_DAYS) {
					// send termination message to each agent
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setContent("terminate");
					for (AID agent : simulationAgents) {
						msg.addReceiver(agent);
					}
					myAgent.send(msg);
					myAgent.doDelete();
				} else {
					reset();
					myAgent.addBehaviour(this);
				}

				return 0;
			}

		}

	}

