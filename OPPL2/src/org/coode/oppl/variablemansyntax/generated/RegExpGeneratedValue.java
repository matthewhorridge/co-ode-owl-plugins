package org.coode.oppl.variablemansyntax.generated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coode.oppl.entity.OWLEntityRenderer;
import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitorEx;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;

public class RegExpGeneratedValue implements
		GeneratedValue<Collection<OWLEntity>>, OWLEntityVisitorEx<List<String>> {
	private final OWLEntityRenderer renderer;
	private final GeneratedValue<String> expression;
	private final Collection<OWLEntity> candidates = new ArrayList<OWLEntity>();
	private final Map<OWLEntity, List<String>> matches = new HashMap<OWLEntity, List<String>>();

	public RegExpGeneratedValue(Collection<? extends OWLObject> candidates,
			GeneratedValue<String> exp, OWLEntityRenderer r) {
		for (OWLObject o : candidates) {
			if (o instanceof OWLEntity) {
				this.candidates.add((OWLEntity) o);
			}
		}
		this.expression = exp;
		this.renderer = r;
	}

	public List<Collection<OWLEntity>> computePossibleValues() {
		return Arrays.asList(Collections
				.unmodifiableCollection(this.candidates));
	}

	public Collection<OWLEntity> getGeneratedValue(BindingNode node) {
		String exp = this.expression.getGeneratedValue(node);
		return this.getMatches(exp);
	}

	private Collection<OWLEntity> getMatches(String exp) {
		if (exp != null) {
			Pattern regExpression = Pattern.compile(exp);
			List<OWLEntity> toReturn = new ArrayList<OWLEntity>();
			for (OWLEntity e : this.candidates) {
				String toMatch = this.renderer.render(e);
				Matcher m = regExpression.matcher(toMatch);
				List<String> group = new ArrayList<String>();
				while (m.find()) {
					group.add(m.group());
				}
				if (group.size() > 0) {
					this.matches.put(e, group);
					toReturn.add(e);
				}
			}
			return toReturn;
		}
		return Collections.emptyList();
	}

	private List<String> retrieve(OWLEntity e) {
		if (this.matches.containsKey(e)) {
			return this.matches.get(e);
		}
		return Collections.emptyList();
	}

	public List<String> visit(OWLClass cls) {
		return this.retrieve(cls);
	}

	public List<String> visit(OWLObjectProperty property) {
		return this.retrieve(property);
	}

	public List<String> visit(OWLDataProperty property) {
		return this.retrieve(property);
	}

	public List<String> visit(OWLIndividual individual) {
		return this.retrieve(individual);
	}

	public List<String> visit(OWLDataType dataType) {
		return this.retrieve(dataType);
	}

	@Override
	public String toString() {
		return this.expression.toString();
	}
}
