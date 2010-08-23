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
package uk.ac.manchester.cs.owl.lint.commons;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.lint.LintPattern;
import org.semanticweb.owlapi.lint.PatternReport;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Feb 18, 2008
 */
public final class SimpleMatchBasedPatternReport<O extends OWLObject>
		implements PatternReport<O> {
	private final Set<Match<O>> delegate = new HashSet<Match<O>>();
	private final LintPattern<O> lintPattern;

	public SimpleMatchBasedPatternReport(LintPattern<O> pattern) {
		this(pattern, Collections.<Match<O>> emptySet());
	}

	public SimpleMatchBasedPatternReport(LintPattern<O> pattern,
			Collection<? extends Match<O>> matches) {
		if (pattern == null) {
			throw new NullPointerException("The pattern cannot be null");
		}
		if (matches == null) {
			throw new NullPointerException(
					"The matches collection cannot be null");
		}
		this.lintPattern = pattern;
		this.delegate.addAll(matches);
	}

	/**
	 * @see org.semanticweb.owlapi.lint.PatternReport#getAffectedOWLObjects(org.semanticweb.owl.model.OWLOntology)
	 */
	public Set<O> getAffectedOWLObjects(OWLOntology ontology) {
		Set<O> toReturn = new HashSet<O>();
		for (Match<O> match : this) {
			if (match.getOntology().equals(ontology)) {
				toReturn.add(match.getOWLObject());
			}
		}
		return toReturn;
	}

	/**
	 * @see org.semanticweb.owlapi.lint.PatternReport#getAffectedOntologies()
	 */
	public Set<OWLOntology> getAffectedOntologies() {
		Set<OWLOntology> toReturn = new HashSet<OWLOntology>();
		for (Match<O> match : this) {
			toReturn.add(match.getOntology());
		}
		return toReturn;
	}

	/**
	 * @see org.semanticweb.owlapi.lint.PatternReport#isAffected(org.semanticweb.owl.model.OWLOntology)
	 */
	public boolean isAffected(OWLOntology ontology) {
		return this.getAffectedOntologies().contains(ontology);
	}

	/**
	 * @see org.semanticweb.owlapi.lint.PatternReport#getLintPattern()
	 */
	public LintPattern<O> getLintPattern() {
		return this.lintPattern;
	}

	/**
	 * 
	 * @see java.util.Map#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.delegate.equals(o);
	}

	/**
	 * 
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(Match<O> e) {
		return this.delegate.add(e);
	}

	/**
	 * 
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends Match<O>> c) {
		return this.delegate.addAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		this.delegate.clear();
	}

	/**
	 * 
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.delegate.contains(o);
	}

	/**
	 * 
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return this.delegate.containsAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	/**
	 * 
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	/**
	 * 
	 * @see java.util.Set#iterator()
	 */
	public Iterator<Match<O>> iterator() {
		return this.delegate.iterator();
	}

	/**
	 * 
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.delegate.remove(o);
	}

	/**
	 * 
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return this.delegate.removeAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return this.delegate.retainAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#size()
	 */
	public int size() {
		return this.delegate.size();
	}

	/**
	 * 
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		return this.delegate.toArray();
	}

	/**
	 * 
	 * @see java.util.Set#toArray(Object[])
	 */
	public <T> T[] toArray(T[] a) {
		return this.delegate.toArray(a);
	}
}
