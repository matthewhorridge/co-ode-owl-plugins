package org.coode.pattern.owllist;

import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.impl.PatternManagerFactory;
import org.coode.pattern.owllist.namedlist.NamedListDescriptor;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectProperty;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 6, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class ListPreferences extends OWLPreferencesPanel {

    private static final int WIDTH = 400;

    private JTextComponent superclassEditor;
    private JTextComponent nextEditor;
    private JTextComponent followedByEditor;
    private JTextComponent contentsEditor;

    private ListExpressionDescriptor descr;

    public void applyChanges() {
        if (descr != null){
            OWLModelManager mngr = getOWLModelManager();

            if (!superclassEditor.getText().equals(descr.getDefaultListClass().toString())){
                OWLClass cls = getOWLModelManager().getOWLClass(superclassEditor.getText());
                descr.setDefaultListClass(cls);
            }

            if (!nextEditor.getText().equals(descr.getDefaultNextProperty().toString())){
                OWLObjectProperty prop = getOWLModelManager().getOWLObjectProperty(superclassEditor.getText());
                descr.setDefaultNextProperty(prop);
            }

            if (!followedByEditor.getText().equals(descr.getDefaultFollowedByProperty().toString())){
                OWLObjectProperty prop = getOWLModelManager().getOWLObjectProperty(superclassEditor.getText());
                descr.setDefaultFollowedByProperty(prop);
            }

            if (!contentsEditor.getText().equals(descr.getDefaultContentsProperty().toString())){
                OWLObjectProperty prop = getOWLModelManager().getOWLObjectProperty(superclassEditor.getText());
                descr.setDefaultContentsProperty(prop);
            }
        }
    }

    public void initialise() throws Exception {
        System.out.println("ListPreferences.initialise");
        for (PatternDescriptor descr : PatternManagerFactory.getOWLPatternManager().getRegisteredPatterns()){
            if (descr instanceof NamedListDescriptor){
                System.out.println("Found descriptor");
                this.descr = ((NamedListDescriptor)descr).getListExpressionDescriptor(getOWLModelManager().getOWLDataFactory());
            }
        }

        System.out.println("SEARCHED......");
        if (descr != null){
            System.out.println("CREATING GUI FOR PREFERENCES");
            setLayout(new BorderLayout(6, 6));
            add(createGUI(), BorderLayout.CENTER);

            OWLModelManager mngr = getOWLModelManager();
            OWLModelManagerEntityRenderer ren = mngr.getOWLEntityRenderer();

            superclassEditor.setText(ren.render(descr.getDefaultListClass()));
            nextEditor.setText(ren.render(descr.getDefaultNextProperty()));
            followedByEditor.setText(ren.render(descr.getDefaultFollowedByProperty()));
            contentsEditor.setText(ren.render(descr.getDefaultContentsProperty()));
        }
    }

    public void dispose() {
        //@@TODO implement
    }

    private JComponent createGUI() {
        JPanel c = new JPanel();
        //c.setPreferredSize(new Dimension(WIDTH, 200));
        //setupBorder("Properties", c);
        c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

        c.add(new JLabel("Default List Superclass:"));
        superclassEditor = new JTextField();
        int h = superclassEditor.getFontMetrics(superclassEditor.getFont()).getHeight();
        superclassEditor.setEnabled(true);
        superclassEditor.setPreferredSize(new Dimension(WIDTH, h));
        c.add(superclassEditor);

        c.add(new JLabel("Directly following elements related by:"));
        nextEditor = new JTextField();
        nextEditor.setEnabled(true);
        nextEditor.setPreferredSize(new Dimension(WIDTH, h));
        c.add(nextEditor);

        c.add(new JLabel("Indirectly following elements are related by:"));
        followedByEditor = new JTextField();
        followedByEditor.setEnabled(true);
        followedByEditor.setPreferredSize(new Dimension(WIDTH, h));
        c.add(followedByEditor);

        c.add(new JLabel("Contents are refered to by:"));
        contentsEditor = new JTextField();
        contentsEditor.setEnabled(true);
        contentsEditor.setPreferredSize(new Dimension(WIDTH, h));
        c.add(contentsEditor);

        c.add(Box.createVerticalGlue());

        return c;
    }
}
