package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.semanticweb.owl.model.OWLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 4, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class PatternSelectionModel {

    private PatternDescriptor selectedPatternDescriptor;

    private Pattern selectedPattern;

    private OWLObject selectedPart;

    private List<PatternSelectionListener> listeners = new ArrayList<PatternSelectionListener>();

    public void setSelectedPart(Pattern pattern, OWLObject part, Object source) {
//        if (selectedPattern != pattern){
//            selectedPattern = pattern;
//            for (PatternSelectionListener l : listeners) {
//                l.selectedPatternChanged(pattern, source);
//            }
//        }
       selectedPart = part;
        for (PatternSelectionListener l : listeners){
            l.selectedPartChanged(pattern, part, source);
        }
    }

    public void setSelectedPattern(Pattern pattern, Object source) {
        selectedPattern = pattern;
        for (PatternSelectionListener l : listeners) {
            l.selectedPatternChanged(pattern, source);
        }
    }

    public void setSelectedPatternDescriptor(PatternDescriptor patternDescr, Object source) {
        selectedPatternDescriptor = patternDescr;
        for (PatternSelectionListener l : listeners) {
            l.selectedPatternDescriptorChanged(patternDescr, source);
        }
    }

    public Pattern getSelectedPattern() {
        return selectedPattern;
    }

    public PatternDescriptor getSelectedPatternDescriptor() {
        return selectedPatternDescriptor;
    }

    public void addSelectionListener(PatternSelectionListener l) {
        listeners.add(l);
    }

    public void removeSelectionListener(PatternSelectionListener l) {
        listeners.remove(l);
    }
}
