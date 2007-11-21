package org.coode.pattern.customclass;

import org.coode.pattern.impl.AbstractPattern;
import org.coode.pattern.api.PatternDescriptor;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 8, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class ToDoPattern extends AbstractPattern {

    private OWLEntity entity;
    private OWLModelManager mngr;

    public ToDoPattern(OWLEntity entity, PatternDescriptor<ToDoPattern> descr, OWLModelManager mngr) {
        super(descr);
        this.mngr = mngr;
        this.entity = entity;
    }

    public ToDoPatternDescriptor getDescriptor() {
        return (ToDoPatternDescriptor) super.getDescriptor();
    }

    public boolean isValid() {
        return true;
    }

    public OWLEntity getBase() {
        return entity;
    }

    public List getParts() {
        return Collections.EMPTY_LIST;
    }

    public OWLObject toOWL(OWLOntology ont, OWLDataFactory df) {
        return null; // default doesn't create any OWL
    }

    public List<OWLOntologyChange> getChanges(OWLOntologyManager mngr, OWLOntology ont, Set<OWLOntology> onts){
        return Collections.EMPTY_LIST;
    }

    public List<OWLOntologyChange> delete(OWLOntologyManager mngr, Set<OWLOntology> onts) {
        System.err.println("Custom Class deletion not yet implemented");
        return Collections.EMPTY_LIST;
    }
}
