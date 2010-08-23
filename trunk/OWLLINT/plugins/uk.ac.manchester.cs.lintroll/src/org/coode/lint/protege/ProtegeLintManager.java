/**
 * 
 */
package org.coode.lint.protege;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coode.lint.protege.loader.AbstractLintPluginLoader;
import org.coode.lint.protege.loader.extensions.LoaderFactoryPlugin;
import org.coode.lint.protege.loader.extensions.LoaderFactoryPluginLoader;
import org.coode.lint.protege.loader.extensions.LoaderFactoryProtegePluginInstanceAdapter;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.plugin.ProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.lint.configuration.DefaultLintConfigurationVisitorExAdapter;
import org.semanticweb.owlapi.lint.configuration.LintConfiguration;
import org.semanticweb.owlapi.lint.configuration.PropertyBasedLintConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

/**
 * @author Luigi Iannone
 * 
 */
public final class ProtegeLintManager implements Disposable {
	public interface LintSelectionListener {
		public void selectionChanged();
	}

	public interface LintLoadListener {
		public void loadChanged();
	}

	private final Set<LintProtegePluginInstance<?>> loadedLints = new HashSet<LintProtegePluginInstance<?>>();
	private final Set<Lint<?>> selectedLints = new HashSet<Lint<?>>();
	private static ProtegeLintManager instance = null;
	private final OWLEditorKit owlEditorKit;
	private final Set<LintSelectionListener> selectionListeners = new HashSet<ProtegeLintManager.LintSelectionListener>();
	private final Set<LintLoadListener> loadListeners = new HashSet<ProtegeLintManager.LintLoadListener>();
	private final Set<AbstractLintPluginLoader<?, ?>> loaders = new HashSet<AbstractLintPluginLoader<?, ?>>();
	private final Map<AbstractLintPluginLoader<?, ?>, Set<LintProtegePluginInstance<?>>> lintLoaderMap = new HashMap<AbstractLintPluginLoader<?, ?>, Set<LintProtegePluginInstance<?>>>();
	private final OWLModelManagerListener modelManagerListener = new OWLModelManagerListener() {
		public void handleChange(OWLModelManagerChangeEvent event) {
			ProtegeLintManager.this.resetLintManager();
			for (AbstractLintPluginLoader<?, ?> loader : ProtegeLintManager.this.loaders) {
				EnumSet<EventType> relevantEventTypes = loader
						.getRelevantEventTypes();
				if (relevantEventTypes.contains(event.getType())) {
					Set<LintProtegePluginInstance<?>> relevantLoadedLints = ProtegeLintManager.this.lintLoaderMap
							.get(loader);
					if (relevantLoadedLints != null) {
						ProtegeLintManager.this.loadedLints
								.removeAll(relevantLoadedLints);
						ProtegeLintManager.this.selectedLints
								.removeAll(relevantLoadedLints);
						// Need to detach the listeners form the removed lints'
						// configurations.
						for (LintProtegePluginInstance<?> lint : relevantLoadedLints) {
							lint.getLintConfiguration().removeAllListeners();
							ProtegeLintManager.this.loadedLints.remove(lint);
						}
					}
					ProtegeLintManager.this.installLintChecks(loader);
					ProtegeLintManager.this.notifyLoadChanged();
					ProtegeLintManager.this.notifySelectionChanged();
				}
			}
		}
	};

	/**
	 * 
	 */
	private void resetLintManager() {
		OWLReasoner reasoner = this.owlEditorKit.getOWLModelManager()
				.getReasoner();
		LintManagerFactory instance = LintManagerFactory.getInstance(
				this.owlEditorKit.getOWLModelManager().getOWLOntologyManager(),
				reasoner instanceof NoOpReasoner ? null : reasoner);
		LintManagerFactory.setInstance(instance);
	}

	private ProtegeLintManager(OWLEditorKit owlEditorKit) {
		assert owlEditorKit != null;
		this.owlEditorKit = owlEditorKit;
		this.owlEditorKit.getOWLModelManager().addListener(
				this.modelManagerListener);
		// Loading both the default and the other kinds of Lint extensions
		// coming from other plug-ins
		this.initialiseLoaders();
		this.installFactories();
		this.resetLintManager();
	}

	/**
	 * 
	 */
	private void initialiseLoaders() {
		this.loaders.clear();
		// The default lint plug-in
		this.loaders.add(new ProtegeLintPluginLoader(this.owlEditorKit));
		// Now the others
		LoaderFactoryPluginLoader loaderFactoryPluginLoader = new LoaderFactoryPluginLoader();
		Set<LoaderFactoryPlugin> plugins = loaderFactoryPluginLoader
				.getPlugins();
		for (LoaderFactoryPlugin loaderFactoryPlugin : plugins) {
			try {
				LoaderFactoryProtegePluginInstanceAdapter<?, ?> factory = loaderFactoryPlugin
						.newInstance();
				if (factory != null) {
					AbstractLintPluginLoader<?, ?> pluginLoader = factory
							.createLintPluginLoader(this.owlEditorKit);
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
		this.lintLoaderMap.clear();
		for (AbstractLintPluginLoader<?, ?> loader : this.loaders) {
			this.installLintChecks(loader);
		}
		this.notifyLoadChanged();
	}

	/**
	 * @param loader
	 */
	private boolean installLintChecks(
			final AbstractLintPluginLoader<?, ?> loader) {
		boolean changed = false;
		for (ProtegePlugin<? extends LintProtegePluginInstance<?>> protegePlugin : loader
				.getPlugins()) {
			LintProtegePluginInstance<?> lint;
			try {
				final LintProtegePluginInstance<?> newInstance = protegePlugin
						.newInstance();
				lint = newInstance == null ? null
						: newInstance
								.getLintConfiguration()
								.accept(
										new DefaultLintConfigurationVisitorExAdapter<LintProtegePluginInstance<?>>() {
											@Override
											protected LintProtegePluginInstance<?> doDefault(
													LintConfiguration lintConfiguration) {
												return newInstance;
											}

											@Override
											public LintProtegePluginInstance<?> visitPropertiesBasedLintConfiguration(
													PropertyBasedLintConfiguration abstractPropertiesBasedLintConfiguration) {
												return loader
														.buildPropertyBasedLint(newInstance);
											}
										});
				if (lint != null) {
					this.loadedLints.add(lint);
					Set<LintProtegePluginInstance<?>> set = this.lintLoaderMap
							.get(loader);
					if (set == null) {
						set = new HashSet<LintProtegePluginInstance<?>>();
					}
					set.add(lint);
					changed = true;
					this.lintLoaderMap.put(loader, set);
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
		return changed;
	}

	public void reload() {
		this.initialiseLoaders();
		this.installFactories();
		this.clearSelectedLints();
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

	public void addLoadedLint(LintProtegePluginInstance<?> lint) {
		boolean add = this.loadedLints.add(lint);
		if (add) {
			this.notifyLoadChanged();
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

	/**
	 * 
	 */
	private void notifyLoadChanged() {
		for (LintLoadListener l : this.loadListeners) {
			l.loadChanged();
		}
	}

	public void addAllSelectedLint(Collection<? extends Lint<?>> lints) {
		boolean add = this.selectedLints.addAll(lints);
		if (add) {
			this.notifySelectionChanged();
		}
	}

	public void addAllLoadedLint(
			Collection<? extends LintProtegePluginInstance<?>> lints) {
		boolean add = this.loadedLints.addAll(lints);
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

	public void removeAllLoadedLint(Collection<? extends Lint<?>> lints) {
		boolean removed = this.loadedLints.removeAll(lints);
		if (removed) {
			this.clearSelectedLints();
			this.notifyLoadChanged();
		}
	}

	public void clearSelectedLints() {
		boolean removed = !this.selectedLints.isEmpty();
		this.selectedLints.clear();
		if (removed) {
			this.notifySelectionChanged();
		}
	}

	public void clearLoadedLints() {
		boolean removed = !this.loadedLints.isEmpty();
		this.loadedLints.clear();
		if (removed) {
			this.clearSelectedLints();
			this.notifyLoadChanged();
		}
	}

	public void removeSelectedLint(Lint<?> lint) {
		boolean removed = this.selectedLints.remove(lint);
		if (removed) {
			this.notifySelectionChanged();
		}
	}

	public void removeLoadedLint(Lint<?> lint) {
		boolean removed = this.loadedLints.remove(lint);
		if (removed) {
			this.notifyLoadChanged();
		}
	}

	public void addLintSelectionListener(LintSelectionListener l) {
		if (l == null) {
			throw new NullPointerException("The listener cannot be null");
		}
		this.selectionListeners.add(l);
	}

	public void addLintLoadListener(LintLoadListener l) {
		if (l == null) {
			throw new NullPointerException("The listener cannot be null");
		}
		this.loadListeners.add(l);
	}

	public void removeLintSelectionListener(LintSelectionListener l) {
		this.selectionListeners.remove(l);
	}

	public void removeLintLoadListener(LintLoadListener l) {
		this.loadListeners.remove(l);
	}

	public void dispose() throws Exception {
		this.owlEditorKit.getOWLModelManager().removeListener(
				this.modelManagerListener);
		instance = null;
	}
}
