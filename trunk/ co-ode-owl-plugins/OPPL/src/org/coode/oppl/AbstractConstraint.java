package org.coode.oppl;

public interface AbstractConstraint {
	public abstract <O> O accept(ConstraintVisitor<O> visitor);

	public String render();
}