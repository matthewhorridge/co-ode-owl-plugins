package org.coode.oppl;

public interface AbstractConstraint {
	public abstract <O> O accept(ConstraintVisitorEx<O> visitor);

	public String render();
}