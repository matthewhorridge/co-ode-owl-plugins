/**
 * 
 * Copyright © Mikel Egana Aranguren 
 * The subClassOf.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The subClassOf.java 
 * software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GPL for more details; a copy of the GPL is included with this product. 
 * 
 * For more info:
 * mikel.eganaaranguren@cs.manchester.ac.uk
 * http://www.gong.manchester.ac.uk
 * 
 */
package uk.ac.manchester.gong.opl.select.condition;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import uk.ac.manchester.gong.opl.ReasonerFactory;
import uk.ac.manchester.gong.opl.javacc.select.OPLSelectParser;
import uk.ac.manchester.gong.opl.javacc.select.ParseException;
import uk.ac.manchester.gong.opl.select.SelectStatementResult;
import uk.ac.manchester.gong.opl.select.SelectStatementResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class equivalentTo implements MatchingCondition {
	
	private OWLOntologyManager manager;
	private Map ns2uri;
	
	public equivalentTo(OWLOntologyManager manager, Map ns2uri) {
		this.manager=manager;
		this.ns2uri=ns2uri;
	}
	public String getConditionName (){
		return "equivalenTo";
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.gong.opl.select.condition.MatchingCondition#match(java.lang.String, org.semanticweb.owl.model.OWLOntology)
	 */
	public SelectStatementResultSet match(String SelectExpression,OWLOntology ontology) {
		
		// List to store the results
		List results = new ArrayList();
		
		// Parse the expression and obtain an OWLExpression to query the reasoner
		OPLSelectParser oplselectparser = new OPLSelectParser();
		OWLDescription owldescription = null;
		try {
			owldescription = oplselectparser.parse(SelectExpression.split(getConditionName())[1], ns2uri, manager);
		} 
		catch (ParseException e1) {e1.printStackTrace();}
		
		// Create a reasoner and query it
        OWLReasoner reasoner = ReasonerFactory.createReasoner(manager);
        try {
			Set<OWLClass> subClsEs = reasoner.getEquivalentClasses(owldescription);
            for(OWLClass cls : subClsEs) {
                SelectStatementResult result = new SelectStatementResult (cls);
    			results.add(result);
            }
        } 
        catch (OWLReasonerException e) {e.printStackTrace();}
		
        // Create the result set and pass it
		SelectStatementResultSet resultSet = new SelectStatementResultSet(results, SelectExpression);
		return resultSet;
	}
}
