package org.coode.pattern.api;

import org.semanticweb.owl.model.*;
import org.coode.pattern.api.PatternDescriptor;

import java.util.List;
import java.util.Set;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public interface Pattern {

    PatternDescriptor getDescriptor();

    boolean isValid();

    OWLEntity getBase();

    List getParts();

    OWLObject toOWL(OWLOntology ont, OWLDataFactory df);

//    String getLabel();

    List<OWLOntologyChange> getChanges(OWLOntologyManager mngr, OWLOntology activeOnt, Set<OWLOntology> activeOnts);

    List<OWLOntologyChange> delete(OWLOntologyManager mngr, Set<OWLOntology> onts);

    void addChangeListener(PatternListener l);

    void removeChangeListener(PatternListener l);
}
