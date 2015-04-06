package uk.ac.manchester.gong.opl.owl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.gong.opl.ReasonerFactory;


public class OWL_print_label {

	public static void main(String[] args) {
		try {
			// Load the ontology from disk
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            IRI physicalURI = IRI
                    .create("file:/home/pik/Bioinformatics/MolecularFunction/ontologies/gene_ontology_edit.owl");
            OWLOntology ontology = manager
                    .loadOntologyFromOntologyDocument(physicalURI);
			
            String ontologyURI = ontology.getOntologyID().getOntologyIRI()
                    .get().toString();
			
            OWLDataFactory factory = manager.getOWLDataFactory();  
            OWLClass catalytic_activity = factory.getOWLClass(IRI
                    .create(ontologyURI + "#GO_0003824"));
			
	        // Load the reasoner
	        OWLReasoner reasoner = ReasonerFactory.createReasoner();
	        
            Set<OWLClass> subClses = reasoner.getSubClasses(catalytic_activity,
                    false).getFlattened();

            List<String> regexps = new ArrayList<>();
            regexps.add("(.+?)\\s(halidohydrolase activity)");
            regexps.add("(.+?)\\s(glycerate dehydrogenase activity)");
            regexps.add("(.+?)\\s(oxidoreductase activity)");
            regexps.add("(.+?)\\s(polymerase activity)");
            regexps.add("(.+?)\\s(kinase activity)");
            regexps.add("(.+?)\\s(ATPase activity)");
            regexps.add("(.+?)\\s(enzyme activity)");

  
            
            List<String> checked_functions = new ArrayList<>();
            
            for(OWLClass cls : subClses) {
                for (OWLAnnotation annotAxiom : EntitySearcher.getAnnotations(
                        cls.getIRI(), ontology, factory.getRDFSLabel())) {
                    String label_value = annotAxiom.getValue().toString();
//    					System.out.println(label_value);
    					Iterator<String> regexp_iterator = regexps.iterator();
    					while(regexp_iterator.hasNext()){
    						String RegexpString = regexp_iterator.next();
//    						System.out.println(RegexpString);
	    					Pattern pattern = Pattern.compile(RegexpString);
	    					Matcher matcher = pattern.matcher(label_value);
	    					boolean matchFound = matcher.find();
	    					if(matchFound){
	    						String function = matcher.group(2);
	    						if(!checked_functions.contains(function)){
	    							checked_functions.add(function);
	    							System.out.println(function);
	    							System.out.println("  e.g.: " + matcher.group(0));
	    						}
	    						
	    					}
    					}
    				}
            	}
	      
	     
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
        } catch (OWLRuntimeException e) {
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
