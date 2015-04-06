package org.coode.lint.protege.loader.extensions;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;

public final class LoaderFactoryPluginLoader extends
		AbstractPluginLoader<LoaderFactoryPlugin> {
	public LoaderFactoryPluginLoader() {
		super("uk.ac.manchester.cs.lintroll",
				LoaderFactoryPlugin.LOADER_FACTORY_PLUGIN_TYPE_ID);
	}

	@Override
	protected LoaderFactoryPlugin createInstance(IExtension extension) {
		return new LoaderFactoryPlugin(extension);
	}

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
}
