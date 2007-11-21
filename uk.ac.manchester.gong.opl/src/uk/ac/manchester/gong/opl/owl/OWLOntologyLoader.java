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

import java.net.URI;
import java.util.*;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.SimpleURIMapper;

import uk.ac.manchester.gong.opl.io.OPLFileException;

public class OWLOntologyLoader {
	private List<String> ontologies;
	private Map<String, URI> namespace2uri;
	private OWLOntology centralOntology;

    public OWLOntologyLoader(List<String> ontologies) {
		this.ontologies = ontologies;
		this.namespace2uri = new HashMap<String, URI>();
	}
	public OWLOntologyManager getOWLOntologyManager () throws OWLException, OPLFileException{
		// Create the OWLOntologyManager
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); 
		Iterator ontologiesIterator = ontologies.iterator();
		boolean centralOntology = true;
		while (ontologiesIterator.hasNext()){
			String ontology = (String) ontologiesIterator.next();
			// Very basic format checking
			try{
				// Get the things
				String [] ontology_meta = ontology.split("-");
				URI physicalURI = URI.create(ontology_meta[2]);
				URI logicalURI = URI.create(ontology_meta[1]);
				String NS = ontology_meta[0];
				
				// Put the things into place
				namespace2uri.put(NS,logicalURI);
				SimpleURIMapper mapper = new SimpleURIMapper(logicalURI, physicalURI);
				manager.addURIMapper(mapper);
				if(centralOntology == true){
					this.centralOntology = manager.loadOntologyFromPhysicalURI(physicalURI);
				}
				else{
					manager.loadOntologyFromPhysicalURI(physicalURI);
				}
				centralOntology = false;
			}
			catch(ArrayIndexOutOfBoundsException e){
				throw new OPLFileException("Malformed OPL file: incorrect ontologies section.");
			}
		}
		return manager;
	}
	public Map<String, URI> namespace2uri (){
		return namespace2uri;
	}
	public OWLOntology getCentralOntology (){
		return this.centralOntology;
	}
}
