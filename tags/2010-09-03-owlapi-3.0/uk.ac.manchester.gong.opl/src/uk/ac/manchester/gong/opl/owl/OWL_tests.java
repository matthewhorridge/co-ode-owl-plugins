package uk.ac.manchester.gong.opl.owl;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.OWLEntityRemover;


import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import uk.ac.manchester.gong.opl.ReasonerFactory;


public class OWL_tests {

	public static void main(String[] args) {
		try {
			// Load the ontology from disk
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	        URI physicalURI = URI.create("file:/home/pik/Bioinformatics/OPL/sample_for_opl.owl");
			OWLOntology ontology = manager.loadOntologyFromPhysicalURI(physicalURI);
			
			// Print all the classes
	        for(OWLClass cls : ontology.getReferencedClasses()) {
	        	System.out.println(cls);
	        }
	        

	        // Load the reasoner
	        OWLReasoner reasoner = ReasonerFactory.createReasoner(manager);
	      
	        String ontologyURI = ontology.getURI().toString(); 
	        
            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLObjectProperty produces = factory.getOWLObjectProperty(URI.create(ontologyURI + "#produces"));  
            OWLClass chorizo = factory.getOWLClass(URI.create(ontologyURI + "#chorizo"));

            OWLDescription producesSomeChorizo = factory.getOWLObjectSomeRestriction(produces, chorizo);

            Set<Set<OWLClass>> subClsSets = reasoner.getSubClasses(producesSomeChorizo);
            Set<OWLClass> subClses = OWLReasonerAdapter.flattenSetOfSets(subClsSets);

            for(OWLClass cls : subClses) {
                System.out.println(">>>>>>>>" + cls);
            }
            
            // Remove classes
	        OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
	        for(OWLClass cls : ontology.getReferencedClasses()) {
	        	cls.accept(remover);
	        }
	        manager.applyChanges(remover.getChanges());
	        remover.reset();
	        System.out.println("Number of classes: " + ontology.getReferencedClasses().size());
           
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLReasonerException e) {
			e.printStackTrace();
		} catch (OWLOntologyChangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  
}











/////////////////////////////////// PARSER AND OTHER SHIT

//OWLAxiom owlsubclassof(OWLEntity owlentity, Map ns2uri, OWLOntologyManager manager):
//{
//	OWLDescription superclass;
//}
//{
//	<Var><SUBCLASSOF>superclass=owlresolvedclass(owlentity, ns2uri, manager)
//	{	
//		OWLAxiom axiom = null;
//		try{			
//			OWLDataFactory factory = owlentity.getOWLDataFactory();
//			axiom = factory.getOWLSubClassAxiom((OWLDescription)owlentity,superclass);
//			System.out.println(axiom);
//		}
//		catch (OWLException e1){e1.printStackTrace();}
//		return axiom;
//	}
//}

////////////////////////////////// OLD SHIT

//try {
//	// LOAD ONTOLOGY
//	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//	URI physicalURI = URI.create("file:/Users/pik/Desktop/opl.owl");
//	OWLOntology owlontology = manager.loadOntologyFromPhysicalURI(physicalURI);
//	OWLDataFactory factory = owlontology.getOWLDataFactory();
//	String ns = "OPL:";
//	System.out.println(ns.split(":")[0]);
//	System.out.println(ns.substring(0,ns.length()-1));
//	System.out.println(Integer.parseInt("1"));
//	
//	// ADD AXIOM
//	// add label = xabier [en] to class xabier
//	OWLClass xab = factory.getOWLClass(URI.create(owlontology.getURI() + "#xabier"));
//	OWLAnnotation annot = factory.getOWLLabelAnnotation("xabier");
//	System.out.println(((OWLEntity)xab).getURI().toString().split("#")[0]);
////	OWLDeclarationAxiom decl = factory.getOWLDeclarationAxiom(xab);
//	OWLAxiom axiom =  factory.getOWLEntityAnnotationAxiom((OWLEntity) xab, annot);
//    AddAxiom addAxiom = new AddAxiom(owlontology, axiom);
//    manager.applyChange(addAxiom);
//      
//	
//	// QUERY THE REASONER WITH ANONIMOUS CLASS
//	Reasoner reasoner = new Reasoner (manager);
//	reasoner.setOntology(owlontology);
//	
//	// Simply get superclasses of Mikel (this works fine)
////    OWLClass role = factory.getOWLClass(URI.create(owlontology.getURI() + "#mikel"));
////    System.out.println(reasoner.getSuperClasses(role));
//	
//	
//	//has_origin some spain
//	
//    OWLClass spain = factory.getOWLClass(URI.create(owlontology.getURI() + "spain"));
//    OWLObjectProperty has_origin = factory.getOWLObjectProperty(URI.create(owlontology.getURI() + "has_origin"));
//    OWLDescription finalowldescription = (OWLDescription) factory.getOWLObjectSomeRestriction(has_origin, spain);
//    System.out.println(finalowldescription);
//    System.out.println(reasoner.getSubClasses(finalowldescription));
//    
//    //has_role only (PhDstudent and boyfriend)
////	OWLClass phd = factory.getOWLClass(URI.create(owlontology.getURI() + "#PhDstudent"));
////	OWLClass boy = factory.getOWLClass(URI.create(owlontology.getURI() + "#boyfriend)"));
////	HashSet inter = new HashSet ();
////	inter.add(phd);
////	inter.add(boy);
////	OWLDescription filler = factory.getOWLObjectIntersectionOf(inter);
////	System.out.println(owlontology.getURI());
////	OWLObjectProperty has_role = factory.getOWLObjectProperty(URI.create(owlontology.getURI() + "#has_role"));
////	OWLDescription finalowldescription = (OWLDescription) factory.getOWLObjectAllRestriction(has_role, filler);
////	System.out.println(finalowldescription);
////    System.out.println(reasoner.getSubClasses(finalowldescription));
//	
//    // GET RDFS_LABEL
////    for(OWLClass cls : owlontology.getReferencedClasses()) {
//////    	HashSet annots = (HashSet) cls.getAnnotationAxioms(owlontology);
////    	for(OWLAnnotationAxiom annotAxiom : cls.getAnnotationAxioms(owlontology)){
////    		System.out.println(annotAxiom.getAnnotation().getAnnotationURI().getFragment());
////    		System.out.println(annotAxiom.getAnnotation().getAnnotationValue().toString().split("@")[0]);
////    	}
////    }
//    
//	// SAVE NEW ONTOLOGY
////	URI physicalURI2 = URI.create("file:/Users/pik/Desktop/opl_tests.owl");
////	manager.saveOntology(owlontology, new RDFXMLOntologyFormat(), physicalURI2);
//} 
//catch (OWLException e1) {e1.printStackTrace();} catch (Exception e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}
