package org.coode.pattern.valuepartition;

import org.coode.pattern.impl.AbstractPatternDescriptor;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class ValuePartitionDescriptor extends AbstractPatternDescriptor<ValuePartition> {

    public boolean isOWLClassPattern() {
        return true;
    }

    public ValuePartition getPattern(OWLObject owlObject, OWLModelManager mngr) {
        ValuePartitionBuilder vpBuilder = new ValuePartitionBuilder(mngr, this);
        owlObject.accept(vpBuilder);
        return vpBuilder.getValuePartition();
    }
}
