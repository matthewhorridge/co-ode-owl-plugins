/**
 * 
 */
package org.coode.lint.protege;

import org.coode.lint.protege.configuration.LintConfigurationInitializer;
import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public interface LintProtegePluginInstance<O extends OWLObject> extends ProtegePluginInstance,
		Lint<O> {
	Lint<?> getOriginatingLint();

	LintConfigurationInitializer getLintConfigurationInitializer();
}
