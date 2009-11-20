package org.coode.oppl.variablemansyntax.generated;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.coode.oppl.entity.OWLEntityRenderer;
import org.coode.oppl.utils.ParserFactory;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.ManchesterVariableSyntax;
import org.coode.oppl.variablemansyntax.PlainVariableVisitor;
import org.coode.oppl.variablemansyntax.VariableScope;
import org.coode.oppl.variablemansyntax.VariableScopeChecker;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.VariableVisitor;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

public class RegExpGeneratedVariable implements GeneratedVariable<OWLEntity> {
	private final String name;
	private final VariableType type;
	private final ConstraintSystem cs;
	private final String expression;
	private final Set<OWLEntity> possibleValues;

	public RegExpGeneratedVariable(String name, VariableType type,
			ConstraintSystem cs, String exp) {
		this.name = name;
		this.type = type;
		this.cs = cs;
		this.expression = exp;
		this.possibleValues = getMatches(this.expression, this.cs);
	}

	public static Set<OWLEntity> getMatches(String exp, ConstraintSystem cs) {
		Set<OWLEntity> toReturn = new HashSet<OWLEntity>();
		Pattern regExpression = Pattern.compile(exp);
		OWLEntityRenderer entityRenderer = ParserFactory.getInstance()
				.getOPPLFactory().getOWLEntityRenderer(cs);
		for (OWLEntity e : cs.getOntology().getReferencedEntities()) {
			String toMatch = entityRenderer.render(e);
			if (regExpression.matcher(toMatch).find()) {
				toReturn.add(e);
			}
		}
		return toReturn;
	}

	public String getOPPLFunction() {
		return "Match(\"" + this.expression + "\")";
	}

	protected OWLObject generateObject(OWLEntity v) {
		return v;
	}

	public <P> P accept(VariableVisitor<P> visitor) {
		return visitor.visit(this);
	}

	public void accept(PlainVariableVisitor visitor) {
		visitor.visit(this);
	}

	public boolean addPossibleBinding(OWLObject object)
			throws OWLReasonerException {
		if (object instanceof OWLEntity) {
			return this.possibleValues.add((OWLEntity) object);
		} else {
			throw new IllegalArgumentException(
					"Arguments must be of type OWLEntity: " + object);
		}
	}

	public void clearBindings() {
		this.possibleValues.clear();
	}

	public OWLObject getGeneratedOWLObject(BindingNode bindingNode) {
		return bindingNode.visit(this);
	}

	public String getName() {
		return this.name;
	}

	public Set<OWLObject> getPossibleBindings() {
		return Collections.<OWLObject> unmodifiableSet(this.possibleValues);
	}

	public VariableType getType() {
		return this.type;
	}

	public URI getURI() {
		String fragment = this.name.substring(this.name.indexOf('?') + 1);
		return URI.create(ManchesterVariableSyntax.NAMESPACE + fragment);
	}

	public GeneratedValue<OWLEntity> getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public VariableScope getVariableScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removePossibleBinding(OWLObject object) {
		return this.possibleValues.remove(object);
	}

	public void setValue(GeneratedValue<OWLEntity> value) {
		// TODO Auto-generated method stub
	}

	public void setVariableScope(VariableScope variableScope,
			VariableScopeChecker variableScopeChecker) {
		// TODO Auto-generated method stub
	}

	public <P> GeneratedVariable<P> replaceValue(GeneratedValue<P> replaceValue) {
		// TODO Auto-generated method stub
		return null;
	}
}
