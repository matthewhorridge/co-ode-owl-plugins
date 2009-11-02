package org.coode.oppl.variablemansyntax.generated;

import java.util.List;

import org.coode.oppl.variablemansyntax.bindingtree.BindingNode;
import org.semanticweb.owl.model.OWLEntity;

public class RegExpGeneratedValue implements GeneratedValue<OWLEntity> {
	private final OWLEntity entity;

	public RegExpGeneratedValue(OWLEntity e) {
		this.entity = e;
	}

	public List<OWLEntity> computePossibleValues() {
		return null;
	}

	public OWLEntity getGeneratedValue(BindingNode node) {
		return this.entity;
	}
}
