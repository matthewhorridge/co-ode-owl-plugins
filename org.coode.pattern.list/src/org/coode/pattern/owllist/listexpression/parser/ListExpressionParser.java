package org.coode.pattern.owllist.listexpression.parser;

import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.model.OWLDescription;
import org.coode.pattern.owllist.listexpression.ListExpression;

import java.util.List;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 10, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public interface ListExpressionParser {

    boolean isWellFormed(String expression) throws OWLExpressionParserException;

    OWLDescription createOWLDescription(String expression) throws OWLExpressionParserException;

    ListExpression createOWLPattern(String expression) throws OWLExpressionParserException;

    /**
     * For testing purposes only - cannot express all OWL list constructs
     * @param expression
     * @return
     * @throws OWLExpressionParserException
     */
    List<OWLDescription> createList(String expression) throws OWLExpressionParserException;
}
