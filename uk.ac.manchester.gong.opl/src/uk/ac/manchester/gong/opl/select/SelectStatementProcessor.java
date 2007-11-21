/**
 * 
 * Copyright  Mikel Egana Aranguren 
 * The SelectStatementProcessor.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The SelectStatementProcessor.java 
 * software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GPL for more details; a copy of the GPL is included with this product. 
 * 
 * For more info:
 * mikel.eganaaranguren@cs.manchester.ac.uk
 * http://www.gong.manchester.ac.uk
 * 
 */
package uk.ac.manchester.gong.opl.select;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import uk.ac.manchester.gong.opl.select.condition.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SelectStatementProcessor {

    private Map<String, URI> ns2uri;
    private OWLOntologyManager manager;

    private List<MatchingCondition> conditions = new ArrayList<MatchingCondition>();

    public SelectStatementProcessor(Map<String, URI> ns2uri, OWLOntologyManager manager) {
        this.ns2uri = ns2uri;
        this.manager = manager;

        List<URI> annotationURIs = new ArrayList<URI>();

        for (OWLOntology ont : manager.getOntologies()) {
            annotationURIs.addAll(ont.getAnnotationURIs());
        }

        for (URI uri : annotationURIs){
            addAnnotation(uri);
        }

        for (URI uri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTIES) {
            addAnnotation(uri);
        }

        conditions.add(new subClassOf(manager, ns2uri));
        conditions.add(new equivalentTo(manager, ns2uri));
        conditions.add(new SubPropertyOf(manager, ns2uri));
        conditions.add(new descendantOf(manager, ns2uri));
    }

    private void addAnnotation(URI annotURI) {
        // @@TODO handle annotation URIs without a fragment
        String name = annotURI.getFragment();
        if (name == null){
            System.err.println("TODO: Handle annotation URIs without a fragment: " + annotURI);
        }
        else{
            conditions.add(new annotation(name));
        }
    }

    public SelectStatementResultSet processSelectStatement (String selectStatement){
		// Depending on the SelectStatement, we will use one condition or another
		SelectStatementResultSet selectstatementresultset = null;
		String condtype = getConditionType (selectStatement);
		OWLOntology ontology = getReferredOntology (selectStatement);

        for (MatchingCondition condition : conditions) {
            String conditionName = condition.getConditionName();
            if (conditionName.equals(condtype)) {
                selectstatementresultset = condition.match(selectStatement, ontology);
            }
        }
        return selectstatementresultset;
	}

    private String getConditionType (String selectStatement){
        return selectStatement.split(" ")[4];
	}

    private OWLOntology getReferredOntology (String selectStatement){
		String var = selectStatement.split(" ")[1];
		String NS = var.split(":")[0];
        return manager.getOntology(ns2uri.get(NS));
	}
}
