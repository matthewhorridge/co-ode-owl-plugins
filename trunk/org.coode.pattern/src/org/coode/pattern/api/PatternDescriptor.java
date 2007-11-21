package org.coode.pattern.api;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;
import org.coode.pattern.api.Pattern;

import java.net.URL;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public interface PatternDescriptor<P extends Pattern> {

    URL getReferenceURL();

    boolean isOWLClassPattern();

    boolean isOWLPropertyPattern();

    boolean isOWLIndividualPattern();

    P getPattern(OWLObject owlObject, OWLModelManager mngr);

//    /**
//     * @param pattern set to null if creating a new pattern
//     * @return an editor component for the pattern
//     */
//    PatternEditor<P> getEditor(P pattern, OWLEditorKit eKit);
//
//    JComponent getPropertiesEditor(OWLEditorKit eKit);
//
//    PatternRenderer<P> getRenderer(OWLEditorKit eKit);
//
    String getLabel();
}
