package org.coode.lint.protege;

import java.util.EnumSet;

import org.coode.lint.protege.loader.AbstractLintPluginLoader;
import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.semanticweb.owlapi.lint.Lint;

/**
 * Loader for Generic {@link Lint} specified as extension whose point is
 * uk.ac.manchester.cs.lintroll.lint
 */
public final class ProtegeLintPluginLoader extends
		AbstractLintPluginLoader<LintPlugin, LintProtegePluginInstanceAdapter<?>> {
	public ProtegeLintPluginLoader(OWLEditorKit owlEditorKit) {
		super("uk.ac.manchester.cs.lintroll", LintPlugin.LINT_PLUGIN_TYPE_ID, owlEditorKit);
	}

	/**
	 * This method needs to be overridden to create an instance of the desired
	 * plugin, based on the plugin <code>Extension</code>
	 * 
	 * @param extension
	 *            The <code>Extension</code> that describes the Java Plugin
	 *            Framework extension.
	 * @return A plugin object (typically some sort of wrapper around the
	 *         extension)
	 */
	@Override
	protected LintPlugin createInstance(IExtension extension) {
		return new LintPlugin(extension);
	}

	/**
	 * This method needs to be overriden to provide a
	 * <code>PluginExtensionMatcher</code>, which is used to filter the plugin
	 * extensions to a desired subset.
	 */
	@Override
	protected PluginExtensionMatcher getExtensionMatcher() {
		return new PluginExtensionMatcher() {
			/**
			 * Determines whether the specified <code>Extension</code>
			 * constitutes a "match" or not.
			 * 
			 * @param extension
			 *            The <code>Extension</code> to test.
			 * @return <code>true</code> if the <code>Extension</code> matches
			 *         or <code>false</code> if the <code>Extension</code> 
			 *         doesn't match.
			 */
			public boolean matches(IExtension extension) {
				return true;
			}
		};
	}

	@Override
	public EnumSet<EventType> getRelevantEventTypes() {
		return EnumSet.noneOf(EventType.class);
	}
}
