package org.coode.pattern.ui;

import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternEditor;
import org.protege.editor.core.ui.view.ViewsPane;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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

    private Map<PatternDescriptor, PatternEditor> editorMap = new HashMap<PatternDescriptor, PatternEditor>();

    private PatternEditor currentPatternEditor;

    private OWLEntityViewFactory viewFactory;

    protected void initialisePatternView() {
        setLayout(new BorderLayout(6, 6));

        viewFactory = new OWLEntityViewFactory(getOWLWorkspace());
    }

    public void selectionChanged(Pattern pattern, OWLObject object) {
        System.out.println("PATTERN, OBJECT");
        currentPatternEditor = null;
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
        System.out.println("PATTERN");
        boolean requiresRepaint = false;

        if (pattern != null){
            final PatternEditorKit patternEditorKit = PatternEditorKit.getPatternEditorKit(getOWLEditorKit());
            final PatternDescriptor descriptor = pattern.getDescriptor();

            PatternEditor oldEditor = currentPatternEditor;

            // get the editor for this pattern type (reuse one if it already exists)
            currentPatternEditor = editorMap.get(descriptor);
            if (currentPatternEditor == null){
                currentPatternEditor = patternEditorKit.getEditor(descriptor);
                editorMap.put(descriptor, currentPatternEditor);
            }

            if (currentPatternEditor != null){
                currentPatternEditor.setPattern(pattern);
            }

            // if an editor is showing and is for a different type of pattern, remove it
            if (!currentPatternEditor.equals(oldEditor)){
                removeAll();
                add(currentPatternEditor.getComponent(), BorderLayout.CENTER);
                requiresRepaint = true;
            }

            setHeaderText(patternEditorKit.getRenderer(descriptor).render(pattern));
        }
        else{
            if (currentPatternEditor != null){
                currentPatternEditor = null;
                removeAll();
                requiresRepaint = true;
            }
            setHeaderText("");
        }

        if (requiresRepaint){
            revalidate();
            repaint();
        }
    }

    public void selectionChanged(PatternDescriptor patternDescr) {
        System.out.println("TYPE");
        currentPatternEditor = null;
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


    protected void disposePatternView() {
        for (PatternEditor editor : editorMap.values()){
            editor.dispose();
        }
        editorMap.clear();
    }
}
