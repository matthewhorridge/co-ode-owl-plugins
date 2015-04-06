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
import java.util.Map;

import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.gong.opl.io.OPLFileException;
import uk.ac.manchester.gong.opl.io.OPLFileReader;
import uk.ac.manchester.gong.opl.owl.OWLOntologyLoader;

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
        Map<String, IRI> ns2uri = null;
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
        IRI physicalURI2 = IRI.create("file:" + newOntologyPath);
		try {
			
			owlontologymanager.saveOntology(owlontologyloader.getCentralOntology(), new RDFXMLDocumentFormat(), physicalURI2);

		} 
		catch (OWLException e) {e.printStackTrace();}
	}
}

