package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternManager;
import org.coode.pattern.api.PatternListener;
import org.coode.pattern.impl.PatternManagerFactory;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 *
 * Creates a tree based on a snapshot of the ontology at a given time
 */
public class PatternTreeModel implements TreeModel {

    private static final String PATTERNS_FOUND = "Patterns";

    private PatternManager patternManagerImpl;
    private List<PatternDescriptor> reg;
    private Set<Pattern> patterns;
    private OWLModelManager mngr;

    private Set<TreeModelListener> listeners = new HashSet<TreeModelListener>();

    private PatternListener patternListener = new PatternListener(){
        public void patternChanged(Pattern pattern) {
            // just reload the current pattern
            for (TreeModelListener l : listeners){
                l.treeStructureChanged(new TreeModelEvent(this, new Object[]{PATTERNS_FOUND, pattern.getDescriptor(), pattern}));
            }
        }
    };

    public PatternTreeModel(PatternManager patternScannerImpl, OWLModelManager mngr) {

        this.mngr = mngr;
        this.patternManagerImpl = patternScannerImpl;

        reload();

        PatternManagerFactory.getOWLPatternManager().addPatternListener(patternListener);
    }

    public void reload() {
        this.reg = new ArrayList<PatternDescriptor>(patternManagerImpl.getRegisteredPatterns());
        Set<OWLOntology> ontologies = mngr.getActiveOntologies();
        this.patterns = patternManagerImpl.getPatternScanner().scanForPatterns(mngr, ontologies,
                                                                               patternManagerImpl.getRegisteredPatterns());

        // force regeneration of the entire tree
        for (TreeModelListener l : listeners){
            l.treeStructureChanged(new TreeModelEvent(this, new Object[]{PATTERNS_FOUND}));
        }
    }

    public Object getRoot() {
        return PATTERNS_FOUND;
    }

    public Object getChild(Object object, int i) {
        if (object.equals(PATTERNS_FOUND)){
            return reg.get(i);
        }
        else if (object instanceof PatternDescriptor){
            return getMatchingPatterns((PatternDescriptor)object).get(i);
        }
        else if (object instanceof Pattern){
            return ((Pattern)object).getParts().get(i);
        }
        return null;
    }

    public int getChildCount(Object object) {
        if (object.equals(PATTERNS_FOUND)){
            return reg.size();
        }
        else if (object instanceof PatternDescriptor){
            return getMatchingPatterns((PatternDescriptor)object).size();
        }
        else if (object instanceof Pattern){
            return ((Pattern)object).getParts().size();
        }
        return 0;
    }

    public boolean isLeaf(Object object) {
        return getChildCount(object) == 0;
    }

    public void valueForPathChanged(TreePath treePath, Object object) {
        //@@TODO implement
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent.equals(PATTERNS_FOUND)){
            return reg.indexOf(child);
        }
        else if (parent instanceof PatternDescriptor){
            return getMatchingPatterns((PatternDescriptor)parent).indexOf(child);
        }
        else if (parent instanceof Pattern){
            return ((Pattern)parent).getParts().indexOf(child);
        }
        return 0;
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
        listeners.add(treeModelListener);
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        listeners.remove(treeModelListener);
    }

/////////////////////////////////////////////

    private List<Pattern> getMatchingPatterns(PatternDescriptor object) {
        List<Pattern> matchingPatterns = new ArrayList<Pattern>();
        for (Pattern pattern : patterns){
            if (pattern.getDescriptor() == object){
                matchingPatterns.add(pattern);
            }
        }
        return matchingPatterns;
    }

    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
        // just reload the current pattern type
        for (TreeModelListener l : listeners){
            l.treeStructureChanged(new TreeModelEvent(this, new Object[]{PATTERNS_FOUND, pattern.getDescriptor()}));
        }
    }

    public void removePattern(Pattern pattern) {
        patterns.remove(pattern);
        // just reload the current pattern type
        for (TreeModelListener l : listeners){
            l.treeStructureChanged(new TreeModelEvent(this, new Object[]{PATTERNS_FOUND, pattern.getDescriptor()}));
        }
    }
}
