package org.coode.annotate;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.*;
import java.util.List;

/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 16, 2007<br><br>
 */
public class TemplateRow {

    private TemplateModel model;
    private OWLAnnotationAxiom annotAxiom;
    private JComponent editor;
    private URI uri;
    private OWLEntity entity;

    private FocusListener focusListener = new FocusAdapter(){
        public void focusLost(FocusEvent focusEvent) {
            updateAnnotation();
        }
    };

    private ActionListener acceptAction = new ActionListener(){
        public void actionPerformed(ActionEvent actionEvent) {
            updateAnnotation();
            editor.transferFocusUpCycle();
        }
    };


    public TemplateRow(OWLAnnotationAxiom<OWLEntity> annotAxiom, TemplateModel model) {
        this(annotAxiom.getSubject(), annotAxiom.getAnnotation().getAnnotationURI(),  model);
        this.annotAxiom = annotAxiom;
        reload(annotAxiom.getAnnotation().getAnnotationValue());
    }

    // when the editor exists without a supporting axiom
    public TemplateRow(OWLEntity entity, URI uri, TemplateModel model) {
        super();
        this.model = model;
        this.entity = entity;
        this.uri = uri;
    }

    public URI getURI(){
        return uri;
    }


    private void updateAnnotation() {

        final OWLModelManager mngr = model.getOWLModelManager();

        OWLEntityAnnotationAxiom newAxiom = null;
        OWLConstant newValue = getValue();
        if (newValue != null){
            OWLConstantAnnotation annot = mngr.getOWLDataFactory().getOWLConstantAnnotation(uri, newValue);
            newAxiom = mngr.getOWLDataFactory().getOWLEntityAnnotationAxiom(entity, annot);
        }

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        if (annotAxiom != null){
            if (!annotAxiom.getAnnotation().getAnnotationValue().equals(newValue)){
                for (OWLOntology ont : mngr.getActiveOntologies()){
                    if (ont.containsAxiom(annotAxiom)){
                        changes.add(new RemoveAxiom(ont, annotAxiom));
                        if (newAxiom != null){
                            changes.add(new AddAxiom(ont, newAxiom));
                        }
                    }
                }
            }
        }
        else{
            if (newAxiom != null){
                changes.add(new AddAxiom(mngr.getActiveOntology(), newAxiom));
            }
        }

        model.requestApplyChanges(changes);

        annotAxiom = newAxiom;
    }

    // all of this should be here or in the model
    public void setValue(OWLObject value) {
        reload(value);
        updateAnnotation();
    }


    private void reload(OWLObject value) {
        String type = model.getComponentType(uri);
        if (TemplateModel.TEXT.equals(type) || TemplateModel.MULTILINE.equals(type)){
            if (value == null){
                ((JTextComponent) getEditor()).setText("");
            }
            else{
                String rendering;
                if (value instanceof OWLConstant){
                    rendering = ((OWLConstant)value).getLiteral();
                }
                else{
                    rendering = value.toString();
                }
                ((JTextComponent) getEditor()).setText(rendering);
            }
        }
        else if (TemplateModel.ENTITY.equals(type)){
            ((JComboBox) getEditor()).setSelectedItem(value);
        }
    }


    // all of this should be here or in the model
    public OWLConstant getValue() {
        String type = model.getComponentType(uri);
        if (TemplateModel.TEXT.equals(type) || TemplateModel.MULTILINE.equals(type)){
            String text = ((JTextComponent) getEditor()).getText().trim();
            if (text != null && !text.equals("")){
                return model.getOWLModelManager().getOWLDataFactory().getOWLUntypedConstant(text);
            }
        }
        else if (TemplateModel.ENTITY.equals(type)){
            return (OWLConstant)((JComboBox) getEditor()).getSelectedItem();
        }
        return null;
    }

    public JComponent getEditor(){
        if (editor == null){
            String type = model.getComponentType(uri);

            // the order of these will determine precedence if multiple editor types are specified
            if (TemplateModel.TEXT.equals(type)){
                editor = new JTextField();
            }
            else if (TemplateModel.MULTILINE.equals(type)){
                editor = new JTextArea();
                editor.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
                ((JTextArea)editor).setColumns(40);

                ((JTextArea)editor).setWrapStyleWord(true);
                ((JTextArea)editor).setLineWrap(true);
            }
            else if (TemplateModel.ENTITY.equals(type)){
                editor = new JComboBox();
            }

            if (editor instanceof JTextComponent){
                editor.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), 0);
            }

            // change the tab key to shift focus instead of inserting a tab character (to help fast input)

            // bind our new forward focus traversal keys
            Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(1);
            newForwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK));
            editor.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                    Collections.unmodifiableSet(newForwardKeys)
            );
            // bind our new backward focus traversal keys
            Set<AWTKeyStroke> newBackwardKeys = new HashSet<AWTKeyStroke>(1);
            newBackwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK+KeyEvent.SHIFT_DOWN_MASK));
            editor.setFocusTraversalKeys(
                    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                    Collections.unmodifiableSet(newBackwardKeys)
            );

            editor.addFocusListener(focusListener);
        }
        return editor;
    }


    public OWLAxiom getAxiom(){
        return annotAxiom;
    }
}
