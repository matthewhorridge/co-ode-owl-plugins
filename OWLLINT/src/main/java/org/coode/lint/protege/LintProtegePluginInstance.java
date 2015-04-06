/**
 * 
 */
package org.coode.lint.protege;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintProtegePluginInstance<O extends OWLObject> extends
		ProtegePluginInstance, Lint<O> {
	String getId();

	String getName();
}
