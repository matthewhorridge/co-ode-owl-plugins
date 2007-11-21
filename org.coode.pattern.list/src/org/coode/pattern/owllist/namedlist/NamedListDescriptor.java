package org.coode.pattern.owllist.namedlist;

import org.coode.pattern.impl.AbstractPatternDescriptor;
import org.coode.pattern.impl.PatternManagerFactory;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.api.PatternManager;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLDataFactory;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class NamedListDescriptor extends AbstractPatternDescriptor<NamedOWLList> {

    private ListExpressionDescriptor listDescriptor;
    
    private NamedOWLListBuilder namedListBuilder;

    public boolean isOWLClassPattern() {
        return true;
    }

    public NamedOWLList getPattern(OWLObject owlObject, OWLModelManager mngr) {
        if (namedListBuilder == null){
            namedListBuilder = new NamedOWLListBuilder(this, mngr);
        }
        namedListBuilder.reset();
        owlObject.accept(namedListBuilder);
        return namedListBuilder.getList();
    }

//////////////////////////////////////////////////////////

    public ListExpressionDescriptor getListExpressionDescriptor(OWLDataFactory df) {
        if (listDescriptor == null){
            final PatternManager pManager = PatternManagerFactory.getOWLPatternManager();
            listDescriptor = (ListExpressionDescriptor) pManager.getRegisteredPattern("OWL List Expression");
            listDescriptor.init(df);
        }
        return listDescriptor;
    }
}
