package org.protege.federation;

import org.semanticweb.owl.model.OWLOntologyChangeListener;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import java.util.List;

import client.ChangeMonitor;

/**
 * Created by IntelliJ IDEA.
 * User: candidasa
 * Date: Feb 26, 2008
 * Time: 3:10:14 PM
 * Updates the View Component's change count every time a change is made
 */
public class ChangeCountUpdater implements OWLOntologyChangeListener {

    LabelSet labelSet;
    ChangeMonitor changeMonitor;

    public ChangeCountUpdater(LabelSet labelSet, ChangeMonitor changeMonitor) {
        this.labelSet = labelSet;
        this.changeMonitor = changeMonitor;
    }

    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
        if (labelSet.changeCount != null) {
            labelSet.changeCount.setText(Integer.toString(changeMonitor.getChanges().size()));
        }
    }
}
