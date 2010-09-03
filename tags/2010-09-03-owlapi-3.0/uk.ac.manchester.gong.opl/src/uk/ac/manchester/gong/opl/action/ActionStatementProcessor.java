/**
 *
 * Copyright Mikel Egana Aranguren 
 * The ActionStatementProcessor.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The ActionStatementProcessor.java 
 * software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GPL for more details; a copy of the GPL is included with this product. 
 *
 * For more info:
 * mikel.eganaaranguren@cs.manchester.ac.uk
 * http://www.gong.manchester.ac.uk
 *
 */
package uk.ac.manchester.gong.opl.action;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.OWLEntityRemover;


import uk.ac.manchester.gong.opl.javacc.actions.OPLActionsParser;
import uk.ac.manchester.gong.opl.javacc.actions.ParseException;
import uk.ac.manchester.gong.opl.select.SelectStatementResult;
import uk.ac.manchester.gong.opl.select.SelectStatementResultSet;

public class ActionStatementProcessor {

    private Map<String, URI> ns2uri;
    private OWLOntologyManager manager;

    public ActionStatementProcessor(Map<String, URI> ns2uri, OWLOntologyManager manager) {
        this.ns2uri = ns2uri;
        this.manager = manager;
    }

    public void processActionStatement (String ActionStatement, SelectStatementResultSet selectstatementresultset) {
        // Simulate the OWLEntity
//		OWLOntology ontology = null;
//		OWLEntity owlentity = null;
//		try {
//			ontology = manager.getOntology((URI)ns2uri.get("OPL"));
//			owlentity = ontology.getOWLDataFactory().getOWLClass(URI.create(ontology.getURI() + "#xabier"));
//		} 
//		catch (OWLException e1) {e1.printStackTrace();}

        List<SelectStatementResult> selectedresults = selectstatementresultset.getSelectStatementResults();

        Pattern pattern = Pattern.compile("REMOVE\\s(\\w+):\\?x$");
        Matcher matcher = pattern.matcher(ActionStatement);
        boolean matchFound = matcher.find();
        System.out.println("[ACTION] " + ActionStatement);
        if (matchFound){
            for (SelectStatementResult selectResult : selectedresults){
                OWLEntity owlentity = (OWLEntity)selectResult.matchedOWLObject();

//				System.out.println(ns2uri.get(matcher.group(1)));
//				System.out.println(owlentity.getURI().toString().split("#")[0]);

//				OWLOntology ontology = manager.getOntology(URI.create((String)ns2uri.get(matcher.group(1))));
                OWLOntology ontology = manager.getOntology(URI.create(owlentity.getURI().toString().split("#")[0]));
                OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));

                owlentity.accept(remover);
                try {
                    manager.applyChanges(remover.getChanges());
                }
                catch (OWLOntologyChangeException e) {
                    e.printStackTrace();
                }
                remover.reset();
            }
        }
        else{ // ADD ACTION
            for (SelectStatementResult selectResult : selectedresults){
                OPLActionsParser actionsparser = new OPLActionsParser();
                try {
                    actionsparser.parse(ActionStatement,ns2uri,manager, selectResult);
                }
                catch (ParseException e) {e.printStackTrace();}
            }
        }
    }
}
