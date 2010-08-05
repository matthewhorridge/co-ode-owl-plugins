package org.coode.lint.protege.loader.extensions;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.ProtegePlugin;

public final class LoaderFactoryPlugin implements
		ProtegePlugin<LoaderFactoryProtegePluginInstanceAdapter<?, ?>> {
	private final IExtension extension;
	public static final String LOADER_FACTORY_PLUGIN_TYPE_ID = "loaderfactory";

	/**
	 * @param extension
	 */
	public LoaderFactoryPlugin(IExtension extension) {
		if (extension == null) {
			throw new NullPointerException("The extension cannot be null");
		}
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

	public LoaderFactoryProtegePluginInstanceAdapter<?, ?> newInstance()
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		ExtensionInstantiator<LoaderFactoryProtegePluginInstanceAdapter<?, ?>> instantiator = new ExtensionInstantiator<LoaderFactoryProtegePluginInstanceAdapter<?, ?>>(
				this.extension);
		LoaderFactoryProtegePluginInstanceAdapter<?, ?> loaderFactory = LoaderFactoryProtegePluginInstanceAdapter.buildLoaderFactoryProtegePluginInstanceAdapter(
				instantiator.instantiate(),
				this.extension);
		return loaderFactory;
	}

	/**
	 * @return the extension
	 */
	public IExtension getExtension() {
		return this.extension;
	}
}
