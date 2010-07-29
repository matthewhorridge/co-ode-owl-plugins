/**
 * 
 */
package org.coode.oppl.lint.protege;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.lint.Lint;

/**
 * @author Luigi Iannone
 * 
 */
public class ProtegeLintManager {
	public interface LintSelectionListener {
		public void selectionChanged();
	}

	private final Set<Lint<?>> loadedLints = new HashSet<Lint<?>>();
	private final Set<Lint<?>> selectedLints = new HashSet<Lint<?>>();
	private static ProtegeLintManager instance = null;
	private final Set<LintSelectionListener> selectionListeners = new HashSet<ProtegeLintManager.LintSelectionListener>();

	private ProtegeLintManager(OWLEditorKit owlEditorKit) {
		assert owlEditorKit != null;
		this.installFactories(owlEditorKit);
	}

	private void installFactories(OWLEditorKit owlEditorKit) {
		assert owlEditorKit != null;
		this.loadedLints.clear();
		ProtegeLintPluginLoader loader = new ProtegeLintPluginLoader();
		for (LintPlugin plugin : loader.getPlugins()) {
			try {
				LintProtegePluginInstanceAdapter<?> lint = plugin.newInstance();
				lint.initialise();
				this.loadedLints.add(lint);
			} catch (ClassNotFoundException e) {
				ProtegeApplication.getErrorLog().logError(e);
			} catch (IllegalAccessException e) {
				ProtegeApplication.getErrorLog().logError(e);
			} catch (InstantiationException e) {
				ProtegeApplication.getErrorLog().logError(e);
			} catch (Exception e) {
				ProtegeApplication.getErrorLog().logError(e);
			}
		}
		this.loadOPPLLints(owlEditorKit);
	}

	/**
	 * @param owlEditorKit
	 */
	private void loadOPPLLints(OWLEditorKit owlEditorKit) {
		ProtegeOPPLLintPluginLoader opplLintPluginLoader = new ProtegeOPPLLintPluginLoader(
				owlEditorKit);
		for (OPPLLintPlugin plugin : opplLintPluginLoader.getPlugins()) {
			try {
				LintProtegePluginInstanceAdapter<?> lint = plugin.newInstance();
				if (lint != null) {
					lint.initialise();
					this.loadedLints.add(lint);
				}
			} catch (ClassNotFoundException e) {
				ProtegeApplication.getErrorLog().logError(e);
			} catch (IllegalAccessException e) {
				ProtegeApplication.getErrorLog().logError(e);
			} catch (InstantiationException e) {
				ProtegeApplication.getErrorLog().logError(e);
			} catch (Exception e) {
				ProtegeApplication.getErrorLog().logError(e);
			}
		}
	}

	public void reload(OWLEditorKit owlEditorKit) {
		this.installFactories(owlEditorKit);
	}

	/**
	 * @return the instance
	 */
	public static ProtegeLintManager getInstance(OWLEditorKit owlEditorKit) {
		if (owlEditorKit == null) {
			throw new NullPointerException("The OWLEditorKit cannot be null");
		}
		if (instance == null) {
			instance = new ProtegeLintManager(owlEditorKit);
		}
		return instance;
	}

	/**
	 * @return the loadedLint
	 */
	public Set<Lint<?>> getLoadedLints() {
		return new HashSet<Lint<?>>(this.loadedLints);
	}

	/**
	 * @return the selectedLint
	 */
	public Set<Lint<?>> getSelectedLints() {
		return new HashSet<Lint<?>>(this.selectedLints);
	}

	public void addSelectedLint(Lint<?> lint) {
		boolean add = this.selectedLints.add(lint);
		if (add) {
			this.notifySelectionChanged();
		}
	}

	/**
	 * 
	 */
	private void notifySelectionChanged() {
		for (LintSelectionListener l : this.selectionListeners) {
			l.selectionChanged();
		}
	}

	public void addAllSelectedLint(Collection<? extends Lint<?>> lints) {
		boolean add = this.selectedLints.addAll(lints);
		if (add) {
			this.notifySelectionChanged();
		}
	}

	public void removeAllSelectedLint(Collection<? extends Lint<?>> lints) {
		boolean removed = this.selectedLints.removeAll(lints);
		if (removed) {
			this.notifySelectionChanged();
		}
	}

	public void removeSelectedLint(Lint<?> lint) {
		boolean removed = this.selectedLints.remove(lint);
		if (removed) {
			this.notifySelectionChanged();
		}
	}

	public void addLintSelectionListener(LintSelectionListener l) {
		if (l == null) {
			throw new NullPointerException("The listener cannot be null");
		}
		this.selectionListeners.add(l);
	}

	public void removeLintSelectionListener(LintSelectionListener l) {
		this.selectionListeners.remove(l);
	}
}
