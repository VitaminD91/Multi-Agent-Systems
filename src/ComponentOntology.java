import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class ComponentOntology implements Concept {
	private String name;
	private int size;
	
	@Slot(mandatory = true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Slot(mandatory = true)
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
}