/**
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.coode.oppl;

import java.util.ArrayList;
import java.util.List;

import org.coode.oppl.protege.model.OPPLStatementModelChangeListener;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.syntax.OPPLStart;
import org.coode.oppl.syntax.ParseException;
import org.coode.oppl.syntax.TokenMgrError;
import org.coode.oppl.utils.ParserFactory;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLStatementModel {
	private OPPLStart opplStatement;
	private OWLOntologyManager ontologyManager;
	private final OWLOntology ontology;
	protected boolean valid = false;
	protected List<OPPLStatementModelChangeListener> listeners = new ArrayList<OPPLStatementModelChangeListener>();

	/**
	 * @param ontologyManager
	 * @param constraintSystem
	 */
	public OPPLStatementModel(OWLOntology ontology,
			OWLOntologyManager ontologyManager) {
		this.ontologyManager = ontologyManager;
		this.ontology = ontology;
	}

	public void setOPPLStatement(String s) {
		ParserFactory.initParser(s, this.ontology, this.ontologyManager);
		try {
			this.opplStatement = (OPPLStart) OPPLParser.Start();
			this.valid = true;
		} catch (ParseException e) {
			this.valid = false;
		} catch (TokenMgrError e) {
			this.valid = false;
		} finally {
			for (OPPLStatementModelChangeListener listener : this.listeners) {
				listener.handleChange();
			}
		}
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return this.valid;
	}

	public void addChangeListener(OPPLStatementModelChangeListener l) {
		this.listeners.add(l);
	}

	public void removeChangeListener(OPPLStatementModelChangeListener l) {
		this.listeners.remove(l);
	}

	/**
	 * @return the opplStatement
	 */
	public OPPLStart getOpplStatement() {
		return this.opplStatement;
	}

	/**
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return this.ontology;
	}

	/**
	 * @param opplStatement
	 *            the opplStatement to set
	 */
	protected void setOpplStatement(OPPLStart opplStatement) {
		this.opplStatement = opplStatement;
	}
}
