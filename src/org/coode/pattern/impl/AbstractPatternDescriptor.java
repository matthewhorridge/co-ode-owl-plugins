package org.coode.pattern.impl;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.ui.AbstractPatternRenderer;
import org.semanticweb.owl.model.OWLObject;

import java.net.URL;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 15, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public abstract class AbstractPatternDescriptor<P extends Pattern>
        implements PatternDescriptor<P>, ProtegePluginInstance {

    private String label;
    private URL referenceURL;
    private AbstractPatternRenderer<P> renderer;
    private AbstractPatternEditor<P> editor;

    public final void initialise(){
    }

    public final void dispose(){    
    }

    public boolean isOWLClassPattern() {
        return false;
    }

    public boolean isOWLPropertyPattern() {
        return false;
    }

    public boolean isOWLIndividualPattern() {
        return false;
    }

    public P getPattern(OWLObject owlObject, OWLModelManager mngr) {
        return null;  //@@TODO implement
    }

    public URL getReferenceURL(){
        return referenceURL;
    }

    public final String toString() {
        return getLabel();
    }

    public final String getLabel(){
        return label;
    }

    // configuration - only to be used by plugin instantiation and PatternEditorKit

    public void setLabel(String label) {
        this.label = label;
    }

    public void setReferenceURL(URL referenceURL) {
        this.referenceURL = referenceURL;
    }

    public void setEditor(AbstractPatternEditor<P> editor) {
        this.editor = editor;
    }

    public AbstractPatternEditor<P> getEditor(){
        return editor;
    }

    public void setRenderer(AbstractPatternRenderer<P> renderer) {
        this.renderer = renderer;
    }

    public AbstractPatternRenderer<P> getRenderer(){
        return renderer;
    }
}
