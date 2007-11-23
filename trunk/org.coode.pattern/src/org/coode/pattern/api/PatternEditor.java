package org.coode.pattern.api;

import org.semanticweb.owl.model.OWLException;

import javax.swing.*;

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
public interface PatternEditor<P extends Pattern> {

    void setPattern(P pattern);

    P getPattern();

    P createPattern() throws OWLException;

    PatternDescriptor<P> getPatternDescriptor();

    boolean isCreateMode();

    JComponent getComponent();

    JComponent getFocusComponent();

    void dispose();
}
