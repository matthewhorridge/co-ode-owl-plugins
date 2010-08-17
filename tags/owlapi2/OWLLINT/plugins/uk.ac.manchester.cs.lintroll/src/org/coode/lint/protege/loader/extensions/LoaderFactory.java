/**
 * 
 */
package org.coode.lint.protege.loader.extensions;

import org.coode.lint.protege.LintProtegePluginInstance;
import org.coode.lint.protege.loader.AbstractLintPluginLoader;
import org.protege.editor.core.plugin.ProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Luigi Iannone
 * 
 */
public interface LoaderFactory<O extends ProtegePlugin<E>, E extends LintProtegePluginInstance<?>> {
	AbstractLintPluginLoader<O, E> createLintPluginLoader(OWLEditorKit owlEditorKit);
}
