/**
 * 
 */
package org.coode.lint.protege.loader.extensions;

import org.coode.lint.protege.LintProtegePluginInstanceAdapter;
import org.coode.lint.protege.loader.AbstractLintPluginLoader;
import org.protege.editor.core.plugin.ProtegePlugin;
import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Luigi Iannone
 * 
 */
public interface LoaderFactory<O extends ProtegePlugin<LintProtegePluginInstanceAdapter<?>>> {
	AbstractLintPluginLoader<O> createLintPluginLoader(OWLEditorKit owlEditorKit);
}
