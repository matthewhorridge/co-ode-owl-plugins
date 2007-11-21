/**
 * 
 * Copyright Mikel Egana Aranguren 
 * The SelectStatementResult.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The SelectStatementResult.java 
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

import java.util.regex.Matcher;

import org.semanticweb.owl.model.OWLObject;

public class SelectStatementResult {
	private OWLObject matchedOWLObject;
	private Matcher regexpmatcher;

    public SelectStatementResult(OWLObject matchedOWLObject) {
		this.matchedOWLObject = matchedOWLObject;
	}

    public OWLObject matchedOWLObject (){
		return matchedOWLObject;
	}

    public Matcher regexpmatcher (){
		return regexpmatcher;
	}

    public void setregexpmatcher (Matcher regexpmatcher){
		this.regexpmatcher = regexpmatcher;
	}
}
