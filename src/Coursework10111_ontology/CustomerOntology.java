package Coursework10111_ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

public class CustomerOntology extends BeanOntology {
	
	private static Ontology theInstance = new CustomerOntology("customer");

	// singleton pattern
	public static Ontology getInstance() {
		return theInstance;
	}

	private CustomerOntology(String name) {
		super(name);
		try {
			add("Coursework10111_ontology");
		} catch (BeanOntologyException e) {
			e.printStackTrace();
		}
	}
	
	 @Override public String toString() {
		 return " " + theInstance; 
		 }


}
