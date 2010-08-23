/**
 * 
 */
package org.coode.lint.protege.loader;

import java.util.EnumSet;

import org.coode.lint.protege.LintProtegePluginInstance;
import org.coode.lint.protege.configuration.ProtegePropertyBasedLint;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.ProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class AbstractLintPluginLoader<O extends ProtegePlugin<E>, E extends LintProtegePluginInstance<?>>
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

	/**
	 * Returns the event types that are relevant for this AbstractPluginLoader.
	 * When one of them is raised the ProtegeLintManager try and reload it.
	 * 
	 * @return an EnumSet<EventType>
	 */
	public abstract EnumSet<EventType> getRelevantEventTypes();

	public <P extends OWLObject> ProtegePropertyBasedLint<P> buildPropertyBasedLint(
			LintProtegePluginInstance<P> lint) {
		return ProtegePropertyBasedLint.buildProtegePropertyBasedLint(lint);
	}
}
