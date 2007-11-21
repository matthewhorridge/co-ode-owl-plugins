package org.coode.pattern.impl;

import org.coode.pattern.ui.PatternSelectionModel;
import org.coode.pattern.api.*;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 8, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
class PatternManagerImpl extends AbstractPluginLoader<PatternPlugin> implements PatternManager {

    private Logger logger = Logger.getLogger(PatternManagerImpl.class);

    private static final String PLUGIN_ID = "org.coode.pattern";
    private static final String EXTENSION_POINT = "Pattern";

    private static PatternManager instance;

    private Set<PatternDescriptor> registeredPatterns = new HashSet<PatternDescriptor>();

    private PatternSelectionModel patternSelectionModel;

    private PatternScanner patternScanner;

    private final List<PatternListener> listeners = new ArrayList<PatternListener>();

    public static PatternManager getInstance() {
        if (instance == null) {
            instance = new PatternManagerImpl();
        }
        return instance;
    }

    private PatternManagerImpl() {
        super(PLUGIN_ID, EXTENSION_POINT);
        patternSelectionModel = new PatternSelectionModel();
        patternScanner = new PatternScannerImpl();

        // load and register pattern plugins
        for (PatternPlugin patternPlugin : getPlugins()){
            try {
                final AbstractPatternDescriptor patternDescriptor = patternPlugin.newInstance();
                registerPattern(patternDescriptor);
            }
            catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    public PatternSelectionModel getPatternSelectionModel() {
        return patternSelectionModel;
    }

    public void registerPattern(PatternDescriptor descr) {
        // @@TODO could split them up based on if they are class pattern or prop pattern etc??????
        registeredPatterns.add(descr);
    }

    public Set<PatternDescriptor> getRegisteredPatterns() {
        return new HashSet<PatternDescriptor>(registeredPatterns);
    }

    public PatternScanner getPatternScanner() {
        return patternScanner;
    }

    public final void addPatternListener(PatternListener l) {
        listeners.add(l);
    }

    public final void removePatternListener(PatternListener l) {
        listeners.remove(l);
    }

    public void notifyPatternChanged(Pattern pattern) {
        for (PatternListener l : listeners){
            l.patternChanged(pattern);
        }
    }

    public PatternDescriptor getRegisteredPattern(String name) {
        for (PatternDescriptor descr : registeredPatterns){
            if (descr.getLabel().equals(name)){
                return descr;
            }
        }
        return null;
    }

    protected PluginExtensionMatcher getExtensionMatcher() {
        return new PluginParameterExtensionMatcher();
    }

    protected PatternPlugin createInstance(IExtension extension) {
        return new PatternPlugin(extension);
    }
}
