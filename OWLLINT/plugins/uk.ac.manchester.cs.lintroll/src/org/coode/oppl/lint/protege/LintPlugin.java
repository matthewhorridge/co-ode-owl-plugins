/**
 * 
 */
package org.coode.oppl.lint.protege;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.ProtegePlugin;

/**
 * @author Luigi Iannone
 * 
 */
public final class LintPlugin implements ProtegePlugin<LintProtegePluginInstanceAdapter<?>> {
	public static final String LINT_PLUGIN_TYPE_ID = "lint";
	private final IExtension extension;

	public LintPlugin(IExtension extension) {
		this.extension = extension;
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePlugin#getId()
	 */
	public String getId() {
		return this.extension.getUniqueIdentifier();
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePlugin#getDocumentation()
	 */
	public String getDocumentation() {
		return JPFUtil.getDocumentation(this.extension);
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePlugin#newInstance()
	 */
	public LintProtegePluginInstanceAdapter<?> newInstance() throws ClassNotFoundException,
			IllegalAccessException, InstantiationException {
		ExtensionInstantiator<LintProtegePluginInstanceAdapter<?>> instantiator = new ExtensionInstantiator<LintProtegePluginInstanceAdapter<?>>(
				this.extension);
		LintProtegePluginInstanceAdapter<?> lint = LintProtegePluginInstanceAdapter.buildLintProtegePluginInstanceAdapter(
				instantiator.instantiate(),
				this.extension);
		return lint;
	}
}
