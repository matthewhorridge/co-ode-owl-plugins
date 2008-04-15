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
package uk.ac.manchester.cs.lintroll.ui.preference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.lint.Lint;

/**
 * @author Luigi Iannone
 * 
 */
public class LintRollPreferences {
	private static Set<Lint> loadedLints;
	private static Set<Lint> selectedLints;
	private static Set<LintRollPreferenceChangeListener> listeners = new HashSet<LintRollPreferenceChangeListener>();
	static {
		loadedLints = new HashSet<Lint>();
		selectedLints = new HashSet<Lint>();
	}

	public static Set<Lint> getLoadedLints() {
		return new HashSet<Lint>(loadedLints);
	}

	/**
	 * @return the selectedLints
	 */
	public static Set<Lint> getSelectedLints() {
		return new HashSet<Lint>(selectedLints);
	}

	public static void addSelectedLint(Lint lint) {
		boolean changed = selectedLints.add(lint);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints,
						LintRollPreferenceChangeEvent.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void removeSelectedLint(Lint lint) {
		boolean changed = selectedLints.remove(lint);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints,
						LintRollPreferenceChangeEvent.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void addLoadedLint(Lint lint) {
		boolean changed = loadedLints.add(lint);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints,
						LintRollPreferenceChangeEvent.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void removeLoadedLint(Lint lint) {
		boolean changed = loadedLints.remove(lint);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints,
						LintRollPreferenceChangeEvent.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void clearSelected() {
		selectedLints.clear();
		for (LintRollPreferenceChangeListener listener : listeners) {
			listener.handleChange(new LintRollPreferenceChangeEvent(
					selectedLints,
					LintRollPreferenceChangeEvent.SELECTED_LINT_CHANGE));
		}
	}

	public static void addAllSelected(Collection<? extends Lint> lints) {
		boolean change = selectedLints.addAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints,
						LintRollPreferenceChangeEvent.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void addAllLoaded(Collection<? extends Lint> lints) {
		boolean change = loadedLints.addAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints,
						LintRollPreferenceChangeEvent.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void removeAllSelected(Collection<? extends Lint> lints) {
		boolean change = selectedLints.removeAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints,
						LintRollPreferenceChangeEvent.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void removeAllLoaded(Collection<? extends Lint> lints) {
		boolean change = loadedLints.removeAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints,
						LintRollPreferenceChangeEvent.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void addLintRollPreferenceChangeListener(
			LintRollPreferenceChangeListener listener) {
		listeners.add(listener);
	}
}
