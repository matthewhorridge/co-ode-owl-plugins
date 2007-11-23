package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternManager;
import org.coode.pattern.impl.PatternManagerFactory;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 4, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * Wrapper for OWLViewComponent that:<ul>
 * <li>handles selection of Patterns</li>
 * <li>provides access to the PatternManager</li>
 * </ul>
 */
public abstract class AbstractPatternView extends AbstractOWLViewComponent {

    private PatternSelectionListener patternSelectionListener = new PatternSelectionListener() {
        public void selectedPatternChanged(Pattern selection, Object source) {
            if (source != AbstractPatternView.this) {
                selectionChanged(selection);
            }
        }

        public void selectedPatternDescriptorChanged(PatternDescriptor selection, Object source) {
            if (source != AbstractPatternView.this) {
                selectionChanged(selection);
            }
        }

        public void selectedPartChanged(Pattern pattern, OWLObject selection, Object source) {
            if (source != AbstractPatternView.this) {
                selectionChanged(pattern, selection);
            }
        }
    };

    public final void initialiseOWLView() throws Exception {
        getPatternManager().getPatternSelectionModel().addSelectionListener(patternSelectionListener);
        initialisePatternView();
    }

    public final void disposeOWLView() {
        getPatternManager().getPatternSelectionModel().removeSelectionListener(patternSelectionListener);
        disposePatternView();
    }

    protected boolean isOWLClassView() {
        return true;
    }

    protected boolean isOWLObjectPropertyView() {
        return true;
    }

    protected boolean isOWLDataPropertyView() {
        return true;
    }

    protected boolean isOWLIndividualView() {
        return true;
    }

//////////////////////////////////////////

    protected final PatternManager getPatternManager() {
        return PatternManagerFactory.getOWLPatternManager();
    }

    protected final PatternRenderer getPatternRenderer(PatternDescriptor descriptor) {
        PatternEditorKit f = PatternEditorKit.getPatternEditorKit(getOWLEditorKit());
        return f.getRenderer(descriptor);
    }

    /**
     * Call to let the system know that the selection has changed (for other pattern views to synchronize)
     *
     * @param pattern the pattern you have selected
     */
    protected final void changeSelection(Pattern pattern) {
        getPatternManager().getPatternSelectionModel().setSelectedPattern(pattern, this);
        if (pattern != null && pattern.getBase() != null){
            getOWLEditorKit().getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(pattern.getBase());
        }
    }

    /**
     * Call to let the system know that the selection has changed (for other pattern views to synchronize)
     *
     * @param patternDescriptor the pattern descriptor you have selected
     */
    protected final void changeSelection(PatternDescriptor patternDescriptor) {
        getPatternManager().getPatternSelectionModel().setSelectedPatternDescriptor(patternDescriptor, this);
    }

    protected final void changeSelection(Pattern pattern, OWLObject part){
        final PatternSelectionModel sModel = getPatternManager().getPatternSelectionModel();
        sModel.setSelectedPart(pattern, part, this);
        if (part instanceof OWLEntity){
            getOWLEditorKit().getOWLWorkspace().getOWLSelectionModel().setSelectedEntity((OWLEntity)part);
        }
    }

    protected abstract void initialisePatternView();

    protected abstract void disposePatternView();

    public void selectionChanged(Pattern pattern, OWLObject part){}

    /**
     * Implement to make the given pattern visible in your view.
     * No need to call changeSelection() as the caller is responsible for this
     *
     * @param pattern the pattern to display (if it is of the correct type for the view)
     */
    public abstract void selectionChanged(Pattern pattern);

    /**
     * Implement to make the given pattern visible in your view.
     * No need to call changeSelection() as the caller is responsible for this
     *
     * @param patternDescr the pattern descriptor to display (if it is of the correct type for the view)
     */
    public abstract void selectionChanged(PatternDescriptor patternDescr);
}
