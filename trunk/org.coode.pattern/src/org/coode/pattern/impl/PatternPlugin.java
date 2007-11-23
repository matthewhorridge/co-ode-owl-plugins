package org.coode.pattern.impl;

import org.apache.log4j.Logger;
import org.coode.pattern.api.Pattern;
import org.coode.pattern.ui.AbstractPatternRenderer;
import org.eclipse.core.runtime.IExtension;
import org.osgi.framework.Bundle;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.plugin.ProtegePlugin;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 14, 2007<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
class PatternPlugin<P extends Pattern> implements ProtegePlugin<AbstractPatternDescriptor<P>> {

    private Logger logger = Logger.getLogger(PatternPlugin.class);

    private IExtension extension;

    private static final String LABEL_PARAM = "label";
    private static final String DOC_URL_PARAM = "documentationUrl";
    private static final String RENDERER = "renderer";
    private static final String EDITOR = "editor";

    public PatternPlugin(IExtension extension) {
        this.extension = extension;
    }

    public String getId() {
        return extension.getExtensionPointUniqueIdentifier();
    }

    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }

    public AbstractPatternEditor<P> getEditor(){
        try {
            return (AbstractPatternEditor)getExtensionObject(EDITOR);
        }
        catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public AbstractPatternDescriptor<P> newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        AbstractPatternDescriptor descriptor = (AbstractPatternDescriptor)PluginUtilities.getInstance().getExtensionObject(extension, "class");

        try {
            descriptor.setLabel(PluginProperties.getParameterValue(extension, LABEL_PARAM, ""));
            descriptor.setReferenceURL(new URL(PluginProperties.getParameterValue(extension, DOC_URL_PARAM, "")));
            descriptor.setRenderer((AbstractPatternRenderer)getExtensionObject(RENDERER));
            descriptor.setPlugin(this);
        }
        catch (MalformedURLException e) {
            logger.error(e);
        }
        return descriptor;
    }

    private Object getExtensionObject(String attribute) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String value = PluginUtilities.getAttribute(extension, attribute);
        if (value != null){
            Bundle b = PluginUtilities.getInstance().getBundle(extension);
            return b.loadClass(value).newInstance();
        }
        return null;
    }
}
