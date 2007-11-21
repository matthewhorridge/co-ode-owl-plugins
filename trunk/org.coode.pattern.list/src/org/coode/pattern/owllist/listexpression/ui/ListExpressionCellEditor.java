package org.coode.pattern.owllist.listexpression.ui;

import org.apache.log4j.Logger;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParser;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.OWLAutoCompleter;
import org.coode.pattern.ui.PatternEditorKit;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 9, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class ListExpressionCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JTextField editor;

    private ListExpression currentValue;

    private ListExpressionParser parser;
    private ListExpressionDescriptor descr;
    private OWLEditorKit eKit;

    public ListExpressionCellEditor(OWLEditorKit eKit, ListExpressionParser parser, ListExpressionDescriptor descr) {
        super();

        this.eKit = eKit;

        this.parser = parser;

        this.descr = descr;

        editor = new JTextField();

        //@@TODO use OWLDescriptionAutoCompleter when Matthew opened it up
        OWLAutoCompleter completer = new OWLAutoCompleter(eKit, editor){
            protected ListExpressionParser getParser() {
                return ListExpressionCellEditor.this.parser;
            }
        };
    }


    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = (ListExpression)value;
        if (value != null){
            editor.setText(PatternEditorKit.getPatternEditorKit(eKit).getRenderer(descr).render(currentValue));
        }
        else{
            editor.setText("");
        }
        return editor;
    }


    public Object getCellEditorValue() {
        if (editor.getText() != null){
            try {
                return parser.createOWLPattern(editor.getText());
            }
            catch (OWLException e) {
                Logger.getLogger(ListExpressionCellEditor.class).error(e);
            }
        }
        return currentValue;
    }

    public boolean isCellEditable(EventObject eventObject) {
        if (eventObject instanceof MouseEvent) {
            return ((MouseEvent) eventObject).getClickCount() >= 2;
        }
        else {
            return super.isCellEditable(eventObject);
        }
    }
}