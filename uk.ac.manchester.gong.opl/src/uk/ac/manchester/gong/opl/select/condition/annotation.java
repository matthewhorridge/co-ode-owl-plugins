/**
 * 
 * Copyright Mikel Egana Aranguren 
 * The label.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The label.java 
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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.gong.opl.select.SelectStatementResult;
import uk.ac.manchester.gong.opl.select.SelectStatementResultSet;

public class annotation implements MatchingCondition {
	private String propName;
	
	public annotation(String name) {
		this.propName = name;
	}
	public String getConditionName (){
		return propName;
	}
	public SelectStatementResultSet match(String select, OWLOntology ontology) {
		List results = new ArrayList();

		//	Get the regexp from the select statement
		String RegexpString = select.split(propName)[1].trim();

        SelectStatementResultSet resultSet = new SelectStatementResultSet(Collections.EMPTY_LIST, select);

        try{
        Pattern pattern = Pattern.compile(RegexpString);
        
		// Visit every class in the ontology and try to match
		for(OWLClass cls : ontology.getReferencedClasses()) {
			String finalComment = null;
			
			for(OWLAnnotationAxiom annotAxiom : cls.getAnnotationAxioms(ontology)){
				if(annotAxiom.getAnnotation().getAnnotationURI().getFragment().equals(propName)){
					String wholeComment = annotAxiom.getAnnotation().getAnnotationValue().toString();
					if(wholeComment.contains("@")){
						finalComment = wholeComment.split("@")[0];
					}
					else if(wholeComment.contains("^^")){
						finalComment = wholeComment.split("\"")[1];
					}
					else{
						finalComment = wholeComment;
					}
				}
			}
			if(finalComment!=null){
				Matcher matcher = pattern.matcher(finalComment);
				boolean matchFound = matcher.find();
				if(matchFound){
					SelectStatementResult result = new SelectStatementResult (cls);
					result.setregexpmatcher(matcher);
					results.add(result);
				}
			}
		}
         resultSet = new SelectStatementResultSet(results, select);
        }
        catch(PatternSyntaxException e){
            System.err.println("Invalid regular expression syntax: " + e.getMessage());
        }
        return resultSet;
	}

}
