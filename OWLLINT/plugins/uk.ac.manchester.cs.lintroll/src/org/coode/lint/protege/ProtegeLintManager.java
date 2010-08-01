/**
 * 
 */
package org.coode.lint.protege;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.lint.protege.loader.AbstractLintPluginLoader;
import org.coode.lint.protege.loader.extensions.LoaderFactoryPlugin;
import org.coode.lint.protege.loader.extensions.LoaderFactoryPluginLoader;
import org.coode.lint.protege.loader.extensions.LoaderFactoryProtegePluginInstanceAdapter;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.ProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.lint.Lint;

/**
 * @author Luigi Iannone
 * 
 */
public final class ProtegeLintManager {
	public interface LintSelectionListener {
		public void selectionChanged();
	}

	private final Set<Lint<?>> loadedLints = new HashSet<Lint<?>>();
	private final Set<Lint<?>> selectedLints = new HashSet<Lint<?>>();
	private static ProtegeLintManager instance = null;
	private final Set<LintSelectionListener> selectionListeners = new HashSet<ProtegeLintManager.LintSelectionListener>();
	private final Set<AbstractPluginLoader<? extends ProtegePlugin<LintProtegePluginInstanceAdapter<?>>>> loaders = new HashSet<AbstractPluginLoader<? extends ProtegePlugin<LintProtegePluginInstanceAdapter<?>>>>();

	private ProtegeLintManager(OWLEditorKit owlEditorKit) {
		assert owlEditorKit != null;
		// Loading both the default and the other kinds of Lint extensions
		// coming from other plug-ins
		this.initialiseLoaders(owlEditorKit);
		this.installFactories();
	}

	/**
	 * 
	 */
	private void initialiseLoaders(OWLEditorKit owlEditorKit) {
		// The default lint plug-in
		this.loaders.add(new ProtegeLintPluginLoader());
		// Now the others
		LoaderFactoryPluginLoader loaderFactoryPluginLoader = new LoaderFactoryPluginLoader();
		Set<LoaderFactoryPlugin> plugins = loaderFactoryPluginLoader
				.getPlugins();
		for (LoaderFactoryPlugin loaderFactoryPlugin : plugins) {
			try {
				LoaderFactoryProtegePluginInstanceAdapter<?> factory = loaderFactoryPlugin
						.newInstance();
				if (factory != null) {
					AbstractLintPluginLoader<?> pluginLoader = factory
							.createLintPluginLoader(owlEditorKit);
					if (pluginLoader != null) {
						this.loaders.add(pluginLoader);
					}
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

	private void installFactories() {
		this.loadedLints.clear();
		for (AbstractPluginLoader<? extends ProtegePlugin<LintProtegePluginInstanceAdapter<?>>> loader : this.loaders) {
			for (ProtegePlugin<LintProtegePluginInstanceAdapter<?>> protegePlugin : loader
					.getPlugins()) {
				LintProtegePluginInstanceAdapter<?> lint;
				try {
					lint = protegePlugin.newInstance();
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
	}

	public void reload(OWLEditorKit owlEditorKit) {
		if (owlEditorKit == null) {
			throw new NullPointerException("The OWL Editor kit cannot be null");
		}
		this.initialiseLoaders(owlEditorKit);
		this.installFactories();
		this.clearSelectedLint();
	}

	/**
	 * @return the instance
	 */
	public static ProtegeLintManager getInstance(OWLEditorKit owlEditorKit) {
		if (owlEditorKit == null) {
			throw new NullPointerException("The OWL editor kit cannot be null");
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

	public void clearSelectedLint() {
		boolean removed = !this.selectedLints.isEmpty();
		this.selectedLints.clear();
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
