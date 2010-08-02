/**
 * 
 */
package org.coode.lint.protege.loader;

import org.coode.lint.protege.LintProtegePluginInstance;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.ProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class AbstractLintPluginLoader<O extends ProtegePlugin<? extends LintProtegePluginInstance<?>>>
		extends AbstractPluginLoader<O> {
	private final OWLEditorKit owlEditorKit;

	/**
	 * @param pluginId
	 * @param extensionPointId
	 */
	public AbstractLintPluginLoader(String pluginId, String extensionPointId,
			OWLEditorKit owlEditorKit) {
		super(pluginId, extensionPointId);
		if (owlEditorKit == null) {
			throw new NullPointerException("The OWL editor Kit cannot be null");
		}
		this.owlEditorKit = owlEditorKit;
	}

	public OWLEditorKit getOWLEditorKit() {
		return this.owlEditorKit;
	}
}
