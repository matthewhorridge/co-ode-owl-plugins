/**
 * 
 * Copyright Mikel Egana Aranguren 
 * The Core.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The Core.java 
 * software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GPL for more details; a copy of the GPL is included with this product. 
 * 
 * For more info:
 * mikel.eganaaranguren@cs.manchester.ac.uk
 * http://www.gong.manchester.ac.uk
 * 
 */
package uk.ac.manchester.gong.opl;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.gong.opl.io.OPLFileReader;
import uk.ac.manchester.gong.opl.owl.OWLOntologyLoader;

import uk.ac.manchester.gong.opl.io.OPLFileException;

public class OPLCore {

    private String oplFilePath;
	private String newOntologyPath;

    public OPLCore (String oplFilePath, String newOntologyPath) {
		this.oplFilePath = oplFilePath;
		this.newOntologyPath = newOntologyPath;
	}

    public void work () throws IOException {
		// Read the OPL file
		OPLFileReader oplfilereader = new OPLFileReader(oplFilePath);
		
		// Load the ontology
		OWLOntologyLoader owlontologyloader = new OWLOntologyLoader(oplfilereader.getNSDeclarations());
		OWLOntologyManager owlontologymanager = null;
		Map<String, URI> ns2uri = null;
		try {
			owlontologymanager = owlontologyloader.getOWLOntologyManager();
			ns2uri = owlontologyloader.namespace2uri();
		} 
		catch (OWLException e) {e.printStackTrace();}
		catch (OPLFileException o) {o.printStackTrace();}
				
		// Parse the instructions 
		OPLInstructionsProcessor oplinstructionprocessor = new OPLInstructionsProcessor (ns2uri,owlontologymanager);
		try {
			oplinstructionprocessor.processOPLInstructions(oplfilereader.getInstructions());
		} 
		catch (OPLFileException e1) {e1.printStackTrace();}
		
		// Save the new ontology
		URI physicalURI2 = URI.create("file:" + newOntologyPath);
		try {
			
			owlontologymanager.saveOntology(owlontologyloader.getCentralOntology(), new RDFXMLOntologyFormat(), physicalURI2);

		} 
		catch (OWLException e) {e.printStackTrace();}
	}
}

