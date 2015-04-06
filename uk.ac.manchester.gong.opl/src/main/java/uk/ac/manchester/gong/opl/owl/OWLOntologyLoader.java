/**
 * 
 * Copyright Mikel Egana Aranguren 
 * The OWLOntologyLoader.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The OWLOntologyLoader.java 
 * software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GPL for more details; a copy of the GPL is included with this product. 
 * 
 * For more info:
 * mikel.eganaaranguren@cs.manchester.ac.uk
 * http://www.gong.manchester.ac.uk
 * 
 */
package uk.ac.manchester.gong.opl.owl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import uk.ac.manchester.gong.opl.io.OPLFileException;

public class OWLOntologyLoader {
	private List<String> ontologies;
    private Map<String, IRI> namespace2uri;
	private OWLOntology centralOntology;

    public OWLOntologyLoader(List<String> ontologies) {
		this.ontologies = ontologies;
        namespace2uri = new HashMap<>();
	}
	public OWLOntologyManager getOWLOntologyManager () throws OWLException, OPLFileException{
		// Create the OWLOntologyManager
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); 
		Iterator<String> ontologiesIterator = ontologies.iterator();
		boolean onto = true;
		while (ontologiesIterator.hasNext()){
			String ontology = ontologiesIterator.next();
			// Very basic format checking
			try{
				// Get the things
				String [] ontology_meta = ontology.split("-");
                IRI physicalURI = IRI.create(ontology_meta[2]);
                IRI logicalURI = IRI.create(ontology_meta[1]);
				String NS = ontology_meta[0];
				
				// Put the things into place
				namespace2uri.put(NS,logicalURI);
                SimpleIRIMapper mapper = new SimpleIRIMapper(logicalURI,
                        physicalURI);
                manager.addIRIMapper(mapper);
				if(onto == true){
                    this.centralOntology = manager
                            .loadOntologyFromOntologyDocument(physicalURI);
				}
				else{
                    manager.loadOntologyFromOntologyDocument(physicalURI);
				}
				onto = false;
			}
			catch(ArrayIndexOutOfBoundsException e){
				throw new OPLFileException("Malformed OPL file: incorrect ontologies section.");
			}
		}
		return manager;
	}

    public Map<String, IRI> namespace2uri() {
		return namespace2uri;
	}
	public OWLOntology getCentralOntology (){
		return centralOntology;
	}
}
