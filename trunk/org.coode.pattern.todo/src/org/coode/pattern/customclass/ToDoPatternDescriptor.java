package org.coode.pattern.customclass;

import org.coode.pattern.impl.AbstractPatternDescriptor;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import java.net.URI;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 8, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class ToDoPatternDescriptor extends AbstractPatternDescriptor<ToDoPattern> {

    private static final URI DEFAULT_ANNOTATION = OWLRDFVocabulary.OWL_VERSION_INFO.getURI();

    public boolean isOWLClassPattern() {
        return true;
    }

    public boolean isOWLPropertyPattern() {
        return true;
    }

    public boolean isOWLIndividualPattern() {
        return true;
    }

    public ToDoPattern getPattern(OWLObject owlObject, OWLModelManager mngr) {
        ToDoBuilder toDoBuilder = new ToDoBuilder(this, mngr);
        owlObject.accept(toDoBuilder);
        return toDoBuilder.getCustomClass();
    }
    
/////////////////////////////////////////////

    public URI getMarkerAnnotation(OWLModelManager mngr) {
        return DEFAULT_ANNOTATION;
    }
}
