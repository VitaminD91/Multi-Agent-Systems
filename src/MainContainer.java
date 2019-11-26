
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
		System.out.println("Jade is a load of shit");
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			String[] components = {};
			
			AgentController manufacturerAgent = myContainer.createNewAgent("manufacturer", ManufacturerAgent.class.getCanonicalName(), components);
			manufacturerAgent.start();
			
			AgentController supplierAgent = myContainer.createNewAgent("supplier", SupplierAgent.class.getCanonicalName(), components);
			supplierAgent.start(); 
			
			
			
			AgentController customerAgent = myContainer.createNewAgent("customer", CustomerAgent.class.getCanonicalName(), components);
			customerAgent.start();

			AgentController syncTicker = myContainer.createNewAgent("sync", SyncTicker.class.getCanonicalName(), null);
			syncTicker.start();
			
			
			
			
			
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}
	}
}
