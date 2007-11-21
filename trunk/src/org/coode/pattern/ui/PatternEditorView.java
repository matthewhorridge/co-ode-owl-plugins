package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternEditor;
import org.semanticweb.owl.model.*;
import org.protege.editor.core.ui.view.ViewsPane;

import javax.swing.*;
import java.awt.*;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class PatternEditorView extends AbstractPatternView {

    private OWLEntityViewFactory viewFactory;

    protected void initialisePatternView() {
        setLayout(new BorderLayout(6, 6));

        viewFactory = new OWLEntityViewFactory(getOWLWorkspace());
    }

    protected void disposePatternView() {
    }

    public void selectionChanged(Pattern pattern, OWLObject object) {
        viewFactory.reset();
        object.accept(viewFactory);
        ViewsPane selectedViewsPane = viewFactory.getViewsPane();

        if (selectedViewsPane != null){
            removeAll();
            add(selectedViewsPane, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    public void selectionChanged(Pattern pattern) {
        removeAll();
        setHeaderText("");

        if (pattern != null){
            final PatternEditorKit patternEditorKit = PatternEditorKit.getPatternEditorKit(getOWLEditorKit());
            PatternEditor editor = patternEditorKit.getEditor(pattern.getDescriptor(), pattern);
            if (editor != null) {
                add(editor.getComponent(), BorderLayout.CENTER);
            }

            setHeaderText(patternEditorKit.getRenderer(pattern.getDescriptor()).render(pattern));
        }
        revalidate();
        repaint();
    }

    public void selectionChanged(PatternDescriptor patternDescr) {
        removeAll();
        setHeaderText("");
        if (patternDescr != null){
            setHeaderText(patternDescr.getLabel());
            final PatternEditorKit patternEditorKit = PatternEditorKit.getPatternEditorKit(getOWLEditorKit());
            JComponent patternProps = patternEditorKit.getPropertiesEditor(patternDescr);
            if (patternProps != null){
                add(patternProps, BorderLayout.CENTER);
                revalidate();
            }
        }
        repaint();
    }
}
