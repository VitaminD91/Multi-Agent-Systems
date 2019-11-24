package Coursework10111_ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

public class CommunicationsOntology extends BeanOntology {
	
	private static Ontology theInstance = new CommunicationsOntology("my_ontology");

	// singleton pattern
	public static Ontology getInstance() {
		return theInstance;
	}

	private CommunicationsOntology(String name) {
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
