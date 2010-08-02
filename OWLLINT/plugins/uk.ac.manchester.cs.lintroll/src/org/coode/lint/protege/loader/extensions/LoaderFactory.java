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
public interface LoaderFactory<O extends ProtegePlugin<? extends LintProtegePluginInstance<?>>> {
	AbstractLintPluginLoader<O> createLintPluginLoader(OWLEditorKit owlEditorKit);
}
