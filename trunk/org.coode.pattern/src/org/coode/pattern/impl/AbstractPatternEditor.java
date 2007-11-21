package org.coode.pattern.impl;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternEditor;
import org.coode.pattern.api.PatternListener;
import org.coode.pattern.ui.PatternRenderer;
import org.coode.pattern.ui.PatternEditorKit;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 *
 * @@TODO should have 2 modes - 1 for creating and 1 for editing
 * first one builds a Pattern, with no interface constraints and then can be validated at commit time
 * second one edits an exising pattern and constrains the interface such that you can't make it invalid
 */
public abstract class AbstractPatternEditor<P extends Pattern> extends JPanel implements PatternEditor<P> {

    private OWLEditorKit eKit;
    private PatternDescriptor<P> descr;
    private P pattern;

    private PatternListener l = new PatternListener(){
        public void patternChanged(Pattern pattern) {
            handleChanges(pattern);
        }
    };

    public void init(OWLEditorKit eKit, PatternDescriptor<P> descr) {
        setLayout(new BorderLayout(6, 6));
        setVisible(true);
        this.eKit = eKit;
        this.descr = descr;

        initialise(eKit, descr);
    }

    protected abstract void initialise(OWLEditorKit eKit, PatternDescriptor<P> descr);

    /**
     * Only gets called when the editor is not in create mode (ie when it is updating an existing pattern)
     */
    protected abstract void refresh();

    public void setPattern(P pattern){
        if (pattern != this.pattern){
            if (this.pattern != null){
                this.pattern.removeChangeListener(l);
            }

            this.pattern = pattern;

            if (pattern != null){
                pattern.addChangeListener(l);
            }

            refresh();
        }
    }

    public final P getPattern() {
        return pattern;
    }

    public abstract P createPattern() throws OWLException;

    protected final OWLEditorKit getOWLEditorKit() {
        return eKit;
    }

    public PatternDescriptor<P> getPatternDescriptor() {
        return descr;
    }

    protected PatternRenderer<P> getPatternRenderer() {
        return getPatternEditorKit().getRenderer(descr);
    }

    protected PatternEditorKit getPatternEditorKit() {
        return PatternEditorKit.getPatternEditorKit(getOWLEditorKit());
    }

    public final boolean isCreateMode() {
        return pattern == null;
    }

    public final JComponent getComponent() {
        return this;
    }
    
    private void handleChanges(Pattern pattern) {
        final OWLModelManager mngr = eKit.getOWLModelManager();
        List<OWLOntologyChange> changes = pattern.getChanges(mngr.getOWLOntologyManager(),
                                                             mngr.getActiveOntology(),
                                                             mngr.getActiveOntologies());
        if (!changes.isEmpty()){
            mngr.applyChanges(changes);
        }
        refresh();
    }

    protected void finalize() throws Throwable {
        PatternManagerImpl.getInstance().removePatternListener(l);
        super.finalize();
    }
}
