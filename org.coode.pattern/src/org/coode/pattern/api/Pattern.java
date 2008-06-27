package org.coode.pattern.api;

import org.semanticweb.owl.model.*;

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
public interface Pattern<P extends Pattern> {

    PatternDescriptor<P> getDescriptor();

    boolean isValid();

    OWLEntity getBase();

    List getParts();

    Set<OWLAxiom> toOWL(OWLOntology ont, OWLDataFactory df);

    List<OWLOntologyChange> addToOntology(OWLOntology activeOnt, Set<OWLOntology> activeOnts);

    List<OWLOntologyChange> deleteFromOntologies(Set<OWLOntology> onts);

    void addChangeListener(PatternListener l);

    void removeChangeListener(PatternListener l);
}
