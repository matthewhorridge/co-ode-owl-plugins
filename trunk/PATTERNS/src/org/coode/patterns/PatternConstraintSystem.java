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
package org.coode.patterns;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.OPPLException;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.IncompatibleValueException;
import org.coode.oppl.variablemansyntax.InputVariable;
import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.generated.CollectionGeneratedValue;
import org.coode.oppl.variablemansyntax.generated.GeneratedValue;
import org.coode.oppl.variablemansyntax.generated.GeneratedVariable;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Luigi Iannone
 * 
 *         Jun 19, 2008
 */
public class PatternConstraintSystem extends ConstraintSystem {
	public static final String THIS_CLASS_VARIABLE_NAME = "?_thisClass";
	public static final String THIS_CLASS_VARIABLE_CONSTANT_SYMBOL = "$thisClass";
	private Map<String, GeneratedVariable<?>> specialVariables = new HashMap<String, GeneratedVariable<?>>();
	private final ConstraintSystem constraintSystem;
	private Map<String, String> specialVariableRenderings = new HashMap<String, String>();

	public PatternConstraintSystem(ConstraintSystem cs,
			OWLOntologyManager ontologyManager) {
		super(cs.getOntology(), ontologyManager, cs.getReasoner());
		this.constraintSystem = cs;
		this.init();
	}

	@Override
	public Variable createVariable(String name, VariableType type)
			throws OPPLException {
		return this.constraintSystem.createVariable(name, type);
	}

	/**
	 * @param ontologyManager
	 * @param reasoner
	 */
	public PatternConstraintSystem(OWLOntology ontology,
			OWLOntologyManager ontologyManager, OWLReasoner reasoner) {
		super(ontology, ontologyManager, reasoner);
		this.constraintSystem = new ConstraintSystem(ontology, ontologyManager,
				reasoner);
		this.init();
	}

	private void init() {
		PatternConstant<OWLClass> patternConstant = new PatternConstant<OWLClass>(
				THIS_CLASS_VARIABLE_NAME, VariableType.CLASS,
				this.constraintSystem.getDataFactory());
		this.createSpecialVariable(patternConstant.getName(),
				THIS_CLASS_VARIABLE_CONSTANT_SYMBOL, patternConstant);
	}

	public PatternConstraintSystem(OWLOntology ontology,
			OWLOntologyManager ontologyManager) {
		super(ontology, ontologyManager);
		this.constraintSystem = new ConstraintSystem(ontology, ontologyManager);
		this.init();
	}

	public Variable getThisClassVariable() {
		return this.specialVariables
				.get(PatternConstraintSystem.THIS_CLASS_VARIABLE_NAME);
	}

	@Override
	public Variable getVariable(String name) {
		Variable variable = this.constraintSystem.getVariable(name);
		if (variable == null) {
			Iterator<String> it = this.specialVariables.keySet().iterator();
			boolean found = false;
			GeneratedVariable<?> specialVariable = null;
			while (!found && it.hasNext()) {
				String referenceName = it.next();
				specialVariable = this.specialVariables.get(referenceName);
				found = referenceName.compareTo(name) == 0
						|| this.specialVariableRenderings.get(specialVariable
								.getName()) != null
						&& this.specialVariableRenderings.get(
								specialVariable.getName()).compareTo(name) == 0;
			}
			if (found) {
				variable = specialVariable;
			}
		}
		return variable;
	}

	@Override
	public Variable getVariable(URI uri) {
		Variable variable = this.constraintSystem.getVariable(uri);
		if (variable == null) {
			variable = this.getSpecialVariable(uri);
		}
		return variable;
	}

	private Variable getSpecialVariable(URI uri) {
		boolean found = false;
		Iterator<? extends Variable> it = this.specialVariables.values()
				.iterator();
		Variable variable = null;
		while (!found && it.hasNext()) {
			variable = it.next();
			found = uri.equals(variable.getURI());
		}
		return found ? variable : null;
	}

	@Override
	public boolean isVariableURI(URI uri) {
		boolean found = this.constraintSystem.isVariableURI(uri);
		if (!found) {
			Iterator<? extends Variable> it = this.specialVariables.values()
					.iterator();
			while (!found && it.hasNext()) {
				Variable variable = it.next();
				found = uri.equals(variable.getURI());
			}
		}
		return found;
	}

	public String resolvePatternConstants(String s) {
		String toReturn = s;
		for (String specialVariableName : this.specialVariables.keySet()) {
			GeneratedVariable<?> variable = this.specialVariables
					.get(specialVariableName);
			if (variable != null) {
				toReturn = toReturn.replaceAll("\\" + specialVariableName,
						variable.getName());
			}
		}
		return toReturn;
	}

	public boolean isThisClassVariable(Variable variable) {
		return variable.equals(this.specialVariables
				.get(PatternConstraintSystem.THIS_CLASS_VARIABLE_NAME));
	}

	@Override
	public Set<Variable> getVariables() {
		Set<Variable> toReturn = this.constraintSystem.getVariables();
		toReturn.addAll(this.specialVariables.values());
		return toReturn;
	}

	public String resolvePattern(String patternName,
			OWLOntologyManager ontologyManager, Set<String> visitedPatterns,
			List<PatternOPPLScript> dependencies, List<String>... args)
			throws PatternException {
		Set<String> visited = new HashSet<String>(visitedPatterns);
		PatternReference patternReference = new PatternReference(patternName,
				this, ontologyManager, visited, args);
		dependencies.add(patternReference.getExtractedPattern());
		VariableType variableType = VariableType
				.getVariableType(patternReference.getResolution().get(0));
		PatternReferenceGeneratedVariable patternReferenceGeneratedVariable = new PatternReferenceGeneratedVariable(
				variableType, PatternReferenceGeneratedVariable
						.getPatternReferenceGeneratedValue(patternReference));
		this.createSpecialVariable(patternReferenceGeneratedVariable.getName(),
				patternReference.toString(), patternReferenceGeneratedVariable);
		List<Variable> referenceVariables = patternReference
				.getExtractedPattern().getVariables();
		for (Variable variable : referenceVariables) {
			if (variable instanceof GeneratedVariable) {
				this.importVariable(variable);
			}
		}
		return patternReferenceGeneratedVariable.getName();
	}

	public InstantiatedPatternModel resolvePatternInstantiation(
			String patternName, OWLOntologyManager ontologyManager,
			Set<String> visitedPatterns, List<PatternOPPLScript> dependencies,
			List<String>... args) throws PatternException {
		PatternReference patternReference = new PatternReference(patternName,
				this, ontologyManager, visitedPatterns, args);
		dependencies.add(patternReference.getExtractedPattern());
		return patternReference.getInstantiation();
	}

	public void instantiateThisClass(PatternConstant<OWLClass> patternConstant) {
		this.createSpecialVariable(patternConstant.getName(),
				THIS_CLASS_VARIABLE_CONSTANT_SYMBOL, patternConstant);
	}

	@Override
	public Variable createStringGeneratedVariable(String name,
			VariableType type, GeneratedValue<String> value)
			throws IncompatibleValueException {
		return this.constraintSystem.createStringGeneratedVariable(name, type,
				value);
	}

	@Override
	public void removeVariable(Variable variable) {
		this.constraintSystem.removeVariable(variable);
	}

	@Override
	public Variable createIntersectionGeneratedVariable(String name,
			VariableType type, CollectionGeneratedValue<OWLClass> collection) {
		return this.constraintSystem.createIntersectionGeneratedVariable(name,
				type, collection);
	}

	@Override
	public Variable createUnionGeneratedVariable(String name,
			VariableType type, CollectionGeneratedValue<OWLClass> collection) {
		return this.constraintSystem.createUnionGeneratedVariable(name, type,
				collection);
	}

	@Override
	protected void setupLeaves() {
	}

	@Override
	public Set<InputVariable> getInputVariables() {
		return this.constraintSystem.getInputVariables();
	}

	@Override
	public String render(Variable variable) {
		GeneratedVariable<?> specialVariable = this.specialVariables
				.get(variable.getName());
		String rendering = specialVariable != null
				&& this.specialVariableRenderings
						.get(specialVariable.getName()) != null ? this.specialVariableRenderings
				.get(specialVariable.getName())
				: super.render(variable);
		return rendering;
	}

	private void createSpecialVariable(String name, String renderedName,
			GeneratedVariable<?> variable) {
		this.specialVariables.put(name, variable);
		this.specialVariableRenderings.put(name, renderedName);
	}

	@Override
	public void importVariable(Variable v) {
		this.constraintSystem.importVariable(v);
	}
}
