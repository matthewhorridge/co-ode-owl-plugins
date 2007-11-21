package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.semanticweb.owl.model.OWLObject;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 4, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public interface PatternSelectionListener {

    void selectedPatternChanged(Pattern pattern, Object source);

    void selectedPatternDescriptorChanged(PatternDescriptor patternDescriptor, Object source);

    void selectedPartChanged(Pattern pattern, OWLObject part, Object source);
}
