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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.gong.opl.ReasonerFactory;
import uk.ac.manchester.gong.opl.javacc.select.OPLSelectParser;
import uk.ac.manchester.gong.opl.javacc.select.ParseException;
import uk.ac.manchester.gong.opl.select.SelectStatementResult;
import uk.ac.manchester.gong.opl.select.SelectStatementResultSet;


public class equivalentTo implements MatchingCondition {
	
	private OWLOntologyManager manager;
	private Map<String, IRI> ns2uri;
	
	public equivalentTo(OWLOntologyManager manager, Map<String, IRI> ns2uri) {
		this.manager=manager;
		this.ns2uri=ns2uri;
	}
	@Override
    public String getConditionName (){
		return "equivalenTo";
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.gong.opl.select.condition.MatchingCondition#match(java.lang.String, org.semanticweb.owl.model.OWLOntology)
	 */
	@Override
    public SelectStatementResultSet match(String SelectExpression,OWLOntology ontology) {
		
		// List to store the results
		List<SelectStatementResult> results = new ArrayList<>();
		
		// Parse the expression and obtain an OWLExpression to query the reasoner
        OWLClassExpression owldescription = null;
		try {
			owldescription = OPLSelectParser.parse(SelectExpression.split(getConditionName())[1], ns2uri, manager);
		} 
		catch (ParseException e1) {e1.printStackTrace();}
		
		// Create a reasoner and query it
        OWLReasoner reasoner = ReasonerFactory.createReasoner();
        Set<OWLClass> subClsEs = reasoner.getEquivalentClasses(owldescription)
                .getEntities();
            for(OWLClass cls : subClsEs) {
                SelectStatementResult result = new SelectStatementResult (cls);
    			results.add(result);
            }

        // Create the result set and pass it
		SelectStatementResultSet resultSet = new SelectStatementResultSet(results, SelectExpression);
		return resultSet;
	}
}
