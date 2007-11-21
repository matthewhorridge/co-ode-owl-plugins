package org.coode.pattern.api;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Set;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 18, 2007<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public interface PatternScanner {
    
    Set<Pattern> scanForPatterns(OWLModelManager mngr,
                                    Set<OWLOntology> ontologies,
                                    Set<PatternDescriptor> patternDescrs);
}
