package org.coode.oppl.lint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.oppl.ChangeExtractor;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLQuery;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.OPPLScriptVisitor;
import org.coode.oppl.OPPLScriptVisitorEx;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.BindingNode;
import org.semanticweb.owl.lint.LintActionException;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.LintVisitor;
import org.semanticweb.owl.lint.LintVisitorEx;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

public class OPPLLintScriptImpl extends OPPLLintScript {
	private String name;
	private final String description;
	private final OPPLScript opplScript;
	private final Variable returnVariable;
	private final OWLOntologyManager ontologyManager;

	/**
	 * @param opplScript
	 * @param returnVariable
	 */
	public OPPLLintScriptImpl(String name, OPPLScript opplScript, Variable returnVariable,
			String description, OWLOntologyManager ontologyManager) {
		this.name = name;
		this.opplScript = opplScript;
		this.returnVariable = returnVariable;
		this.description = description;
		this.ontologyManager = ontologyManager;
	}

	@Override
	public List<OWLAxiomChange> getChanges(OWLOntology ontology, OWLOntologyManager ontologyManager) {
		ChangeExtractor changeExtractor = new ChangeExtractor(
				this.opplScript.getConstraintSystem(), true);
		List<OWLAxiomChange> changes = this.opplScript.accept(changeExtractor);
		return changes;
	}

	@Override
	public Set<OWLObject> getDetectedObjects(OWLOntology ontology,
			OWLOntologyManager ontologyManager) {
		this.getChanges(ontology, ontologyManager);
		Set<BindingNode> leaves = this.opplScript.getConstraintSystem().getLeaves();
		Set<OWLObject> toReturn = new HashSet<OWLObject>();
		for (BindingNode leaf : leaves) {
			OWLObject assignmentValue = leaf.getAssignmentValue(this.getReturnVariable());
			toReturn.add(assignmentValue);
		}
		return toReturn;
	}

	public void accept(OPPLScriptVisitor visitor) {
		this.opplScript.accept(visitor);
	}

	public <P> P accept(OPPLScriptVisitorEx<P> visitor) {
		return this.opplScript.accept(visitor);
	}

	public List<OWLAxiomChange> getActions() {
		return this.opplScript.getActions();
	}

	public ConstraintSystem getConstraintSystem() {
		return this.opplScript.getConstraintSystem();
	}

	public List<Variable> getInputVariables() {
		return this.opplScript.getInputVariables();
	}

	public OPPLQuery getQuery() {
		return this.opplScript.getQuery();
	}

	public List<Variable> getVariables() {
		return this.opplScript.getVariables();
	}

	public String render() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.name);
		buffer.append("; ");
		buffer.append(this.opplScript.render());
		buffer.append("RETURN ");
		buffer.append(this.returnVariable.getName());
		buffer.append("; ");
		buffer.append(this.description);
		return buffer.toString();
	}

	@Override
	public Variable getReturnVariable() {
		return this.returnVariable;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see org.semanticweb.owl.lint.ActingLint#executeActions(org.semanticweb.owl.lint.LintReport)
	 */
	public void executeActions(Collection<? extends OWLOntology> ontologies)
			throws LintActionException {
		List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
		for (OWLOntology ontology : ontologies) {
			changes.addAll(this.getChanges(ontology, this.getOntologyManager()));
		}
		try {
			this.getOntologyManager().applyChanges(changes);
		} catch (OWLOntologyChangeException e) {
			throw new LintActionException(e);
		}
	}

	/**
	 * @return the ontologyManager
	 */
	public OWLOntologyManager getOntologyManager() {
		return this.ontologyManager;
	}

	/**
	 * @see org.semanticweb.owl.lint.Lint#detected(java.util.Set)
	 */
	public LintReport<OWLObject> detected(Collection<? extends OWLOntology> targets)
			throws LintException {
		LintReport<OWLObject> toReturn = LintManagerFactory.getInstance().getLintManager().getLintFactory().createLintReport(
				this);
		for (OWLOntology ontology : targets) {
			Set<OWLObject> detectedObjects = this.getDetectedObjects(
					ontology,
					this.getOntologyManager());
			for (OWLObject object : detectedObjects) {
				toReturn.add(object, ontology);
			}
		}
		return toReturn;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OPPLLintScriptImpl) {
			return this.getName().equals(((OPPLLintScriptImpl) obj).getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}

	public void addVariable(Variable variable) {
		this.opplScript.addVariable(variable);
	}

	@Override
	public OPPLScript getOPPLScript() {
		return this.opplScript;
	}

	public void accept(LintVisitor visitor) {
		visitor.visitActingLint(this);
	}

	public <P> P accept(LintVisitorEx<P> visitor) {
		return visitor.visitActingLint(this);
	}
}
