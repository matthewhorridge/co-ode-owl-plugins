package org.coode.pattern.owllist.namedlist.ui;

import org.apache.log4j.Logger;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternEditor;
import org.coode.pattern.impl.AbstractPatternEditor;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParser;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParserImpl;
import org.coode.pattern.owllist.listexpression.ui.ListExpressionCellEditor;
import org.coode.pattern.owllist.namedlist.NamedListDescriptor;
import org.coode.pattern.owllist.namedlist.NamedOWLList;
import org.coode.pattern.ui.PatternEditorKit;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 8, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class NamedListEditor extends AbstractPatternEditor<NamedOWLList> {

    private Logger logger = Logger.getLogger(NamedListEditor.class);

    private JTextField nameField;
    private JCheckBox definedCheckbox;

    private JTable table;
    private DefaultTableModel tableModel;
    private UIHelper uiHelper;

    private Action addAction = new AbstractAction("Add condition", OWLIcons.getIcon("class.add.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                addCondition();
            }
            catch (OWLException e) {
                logger.error(e);
            }
        }
    };

    private Action removeAction = new AbstractAction("Remove condition", OWLIcons.getIcon("class.delete.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                removeCondition();
            }
            catch (OWLException e) {
                logger.error(e);
            }
        }
    };

    private ChangeListener definedListener = new ChangeListener(){
        public void stateChanged(ChangeEvent changeEvent) {
            NamedOWLList p = getPattern();
            if (p != null){
                p.setDefined(definedCheckbox.isSelected());
            }
        }
    };

    protected void initialise(OWLEditorKit eKit, PatternDescriptor<NamedOWLList> descr) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(createListPanel());
    }

    protected void refresh() {
        for (int i=0; i<tableModel.getRowCount(); i++){
            tableModel.removeRow(i);
        }

        NamedOWLList p = getPattern();

        if (p != null){
            nameField.setText(p.render());

            definedCheckbox.setSelected(p.isDefined());

            for (ListExpression r : p.getConditions()){
                tableModel.addRow(new Object[]{r});
            }
        }
        else{
            nameField.setText("");
        }
    }

    public NamedListDescriptor getPatternDescriptor() {
        return (NamedListDescriptor)super.getPatternDescriptor();
    }

    protected void disposePatternEditor() {
        definedCheckbox.removeChangeListener(definedListener);
    }

    public NamedOWLList createPattern() throws OWLException {
        NamedOWLList list = null;
        if (tableModel.getRowCount() > 0) {
            OWLModelManager mngr = getOWLEditorKit().getOWLModelManager();
            list = new NamedOWLList(nameField.getText(), getPatternDescriptor(), mngr);

            for (int i=0; i<tableModel.getRowCount(); i++){
                ListExpression r = (ListExpression)tableModel.getValueAt(i, 0);
                list.addCondition(r);
            }
            list.setDefined(definedCheckbox.isSelected());
        }
        return list;
    }

    public JComponent getFocusComponent() {
        return nameField;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        nameField.setEnabled(enabled);
    }

/////////////////////////////////    actions

    private void addCondition() throws OWLException {
        final OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
        ListExpressionDescriptor descr = getPatternDescriptor().getListExpressionDescriptor(df);
        PatternEditorKit pEditorKit = PatternEditorKit.getPatternEditorKit(getOWLEditorKit());
        PatternEditor<ListExpression> editor = pEditorKit.getEditor(descr);

        if (uiHelper == null){
            uiHelper = new UIHelper(getOWLEditorKit());
        }
        JComponent component = editor.getComponent();
        JComponent focusComponent = editor.getFocusComponent();

        if (uiHelper.showDialog("Create", component,
                                focusComponent) == JOptionPane.OK_OPTION) {
            ListExpression newCondition = editor.createPattern();
            if (newCondition != null) {
                if (isCreateMode()){
                    tableModel.addRow(new Object[]{newCondition});
                }
                else{
                    getPattern().addCondition(newCondition);
//                    applyChanges();
                }
            }
        }
    }

    private void removeCondition() throws OWLException {
        int row = table.getSelectedRow();
        if (row >= 0){
            if (isCreateMode()){
                tableModel.removeRow(row);
            }
            else{
                ListExpression r = (ListExpression) tableModel.getValueAt(row, 0);
                getPattern().removeCondition(r);
                //               applyChanges();
            }
        }
    }

//    private void applyChanges() {
//        final OWLModelManager mngr = getOWLEditorKit().getOWLModelManager();
//        final java.util.List<OWLOntologyChange> changes = getPattern().getChanges(mngr.getOWLOntologyManager(),
//                                                                                  mngr.getActiveOntology(),
//                                                                                  mngr.getActiveOntologies());
//        mngr.applyChanges(changes);
//    }

///////////////////////////   GUI setup

    private JComponent createListPanel() {
        JPanel c = new JPanel(new BorderLayout(6, 6));
        c.setPreferredSize(new Dimension(400, 300));
        c.add(createNameField(), BorderLayout.NORTH);
        c.add(createConditionsPanel(), BorderLayout.CENTER);
        return c;
    }

    private JComponent createNameField() {
        JPanel c = new JPanel(new BorderLayout(6, 6));

        nameField = new JTextField();
        FontMetrics fm = nameField.getFontMetrics(nameField.getFont());
        int height = fm.getHeight() + 6;
        nameField.setPreferredSize(new Dimension(0, height));

        definedCheckbox = new JCheckBox("Defined");
        definedCheckbox.addChangeListener(definedListener);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        p.add(new JLabel("Name:"));
        p.add(Box.createHorizontalGlue());
        p.add(definedCheckbox);

        c.add(p, BorderLayout.NORTH);
        c.add(nameField, BorderLayout.CENTER);
        return c;
    }

    private JComponent createConditionsPanel() {
        JPanel c = new JPanel(new BorderLayout(6, 6));
        JButton addButton = new JButton(addAction);
        addButton.setToolTipText(addButton.getText());
        addButton.setText("");
        JButton removeButton = new JButton(removeAction);
        removeButton.setToolTipText(removeButton.getText());
        removeButton.setText("");
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
        toolbar.add(new JLabel("List Conditions:"));
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(addButton);
        toolbar.add(removeButton);
        tableModel = new DefaultTableModel(0, 1){
            public String getColumnName(int i) {
                return null;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(table.getFontMetrics(table.getFont()).getHeight()+6);

        final OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
        final ListExpressionDescriptor expressionDescriptor = getPatternDescriptor().getListExpressionDescriptor(df);

        ListExpressionParser parser = new ListExpressionParserImpl(expressionDescriptor,
                                                                   getOWLEditorKit().getOWLModelManager());

        TableColumn col = table.getColumnModel().getColumn(0);
        col.setCellEditor(new ListExpressionCellEditor(getOWLEditorKit(),
                                                       parser,
                                                       expressionDescriptor));



        col.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable jTable, Object object, boolean b, boolean b1, int i, int i1) {
                object = getPatternEditorKit().getRenderer(expressionDescriptor).render((ListExpression)object);
                return super.getTableCellRendererComponent(jTable, object, b, b1, i, i1);
            }
        });

        c.add(toolbar, BorderLayout.NORTH);
        c.add(new JScrollPane(table), BorderLayout.CENTER);
        return c;
    }
}
