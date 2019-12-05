import Coursework10111_ontology.BatteryOntology;
import Coursework10111_ontology.ComponentOntology;
import Coursework10111_ontology.MemoryOntology;
import Coursework10111_ontology.ScreenOntology;
import Coursework10111_ontology.StorageOntology;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;

public class MainContainer {

	public static void main(String[] args){
		
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		ContainerController myContainer = myRuntime.createMainContainer(myProfile);
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			ComponentOntology components1[] = {
					new BatteryOntology(2000, 70f, 1),
					new BatteryOntology(3000, 100f, 1),
					new ScreenOntology(5, 100f, 1),
					new ScreenOntology(7, 150f, 1),
					new StorageOntology(64, 25f, 1),
					new StorageOntology(256, 50f, 1),
					new MemoryOntology(4, 30f, 1),
					new MemoryOntology(8, 60f, 1)
			};
			
			ComponentOntology[] components2 = {
					new StorageOntology(64, 15f, 4),
					new StorageOntology(256, 40f, 4),
					new MemoryOntology(4, 20f, 4),
					new MemoryOntology(8, 35f, 4)
			};
			
			AgentController manufacturerAgent = myContainer.createNewAgent("manufacturer", ManufacturerAgent.class.getCanonicalName(), null);
			manufacturerAgent.start();
			
			AgentController supplierAgent1 = myContainer.createNewAgent("supplier1", SupplierAgent.class.getCanonicalName(), components1);
			supplierAgent1.start(); 
			AgentController supplierAgent2 = myContainer.createNewAgent("supplier2", SupplierAgent.class.getCanonicalName(), components2);
			supplierAgent2.start(); 
			
			
			
			AgentController customerAgent = myContainer.createNewAgent("customer", CustomerAgent.class.getCanonicalName(), null);
			customerAgent.start();

			AgentController syncTicker = myContainer.createNewAgent("sync", SyncTicker.class.getCanonicalName(), null);
			syncTicker.start();
			
			
			
			
			
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}
	}
}