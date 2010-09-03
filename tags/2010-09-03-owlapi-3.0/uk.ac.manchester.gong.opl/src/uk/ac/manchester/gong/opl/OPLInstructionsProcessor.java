/**
 * 
 * Copyright Mikel Egana Aranguren 
 * The OPLInstructionsProcessor.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The OPLInstructionsProcessor.java 
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

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.io.PrintStream;
import java.net.URI;

import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.gong.opl.action.ActionStatementProcessor;
import uk.ac.manchester.gong.opl.io.OPLFileException;
import uk.ac.manchester.gong.opl.select.SelectStatementProcessor;
import uk.ac.manchester.gong.opl.select.SelectStatementResult;
import uk.ac.manchester.gong.opl.select.SelectStatementResultSet;

public class OPLInstructionsProcessor {

    private PrintStream log = System.out;

    private Map<String, URI> ns2uri;
    private OWLOntologyManager manager;
    
    private ActionStatementProcessor actionstatementprocessor;
    private SelectStatementProcessor selectstatementprocessor;

    public OPLInstructionsProcessor(Map<String, URI> ns2uri, OWLOntologyManager manager) {
        this.ns2uri = ns2uri;
        this.manager = manager;
        actionstatementprocessor = new ActionStatementProcessor(ns2uri, manager);
        selectstatementprocessor = new SelectStatementProcessor (ns2uri, manager);
    }

    public void setLog(PrintStream stream){
        log = stream;
    }

    public void processOPLInstructions (List<String> instructions) throws OPLFileException{

        // Process each instruction
		for (String instruction : instructions){
			String [] selectActions = instruction.split(";");
			// Very basic format checking ...
			if (selectActions.length < 2){
				throw new OPLFileException("Malformed OPL file: incorrect instructions section.");
			}
			// It seems fine
			else{
				// Process Select
				log.println("[SELECT] " + selectActions[0]);
				SelectStatementResultSet selectstatementresultset = selectstatementprocessor.processSelectStatement(selectActions[0]);
				
				// Testing Select Statement
				if(selectstatementresultset!=null){
					List res = selectstatementresultset.getSelectStatementResults();
					Iterator resIterator = res.iterator();
					while(resIterator.hasNext()){
						log.println("[MATCH] " + (((SelectStatementResult)resIterator.next()).matchedOWLObject()));
					}
				}
				
				// Process actions
				
				// !!! Uncomment this to continue !!! 
				
				for (int i=1; i<selectActions.length; i++){
				    log.println("[ACTION] " + selectActions[i]);
					actionstatementprocessor.processActionStatement(selectActions[i], selectstatementresultset);
				}
			}
		}
	}

}
