package org.coode.pattern.owllist.listexpression.ui;

import org.apache.log4j.Logger;
import org.coode.pattern.impl.AbstractPatternEditor;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParserImpl;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParser;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionVocabulary;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.namedlist.ui.NamedListEditor;
import org.coode.pattern.owllist.OWLAutoCompleter;
import org.coode.pattern.api.PatternDescriptor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLDescription;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <listParser/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 15, 2006<br><br>
 * <listParser/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class ListExpressionEditor extends AbstractPatternEditor<ListExpression> {

    private static final Logger logger = Logger.getLogger(NamedListEditor.class);

    private ListExpressionParser listParser;

    private JTextArea expressionField;
    private JTextArea warningField;

    private JCheckBox negatedCheckbox;
    private JCheckBox startOpenCheckbox;
    private JCheckBox endOpenCheckbox;
    private JComponent tableComponent;
    private OWLObjectList elementList;

    private int newIndex;

    private JTextComponent nextEditor;
    private JTextComponent followedByEditor;
    private JTextComponent contentsEditor;

    private ListCellRendererAdapter cellRendererAdapter;

    private boolean currentlyValid = false;

    // listen for changes in the expression
    private DocumentListener l = new DocumentListener() {
        public void insertUpdate(DocumentEvent documentEvent) {
            validateContent();
        }
        public void removeUpdate(DocumentEvent documentEvent) {
            validateContent();
        }
        public void changedUpdate(DocumentEvent documentEvent) {
            validateContent();
        }
    };

    private AbstractAction delete = new AbstractAction("delete"){
        public void actionPerformed(ActionEvent actionEvent) {
            handleDeleteElement();
        }
    };

    private AbstractAction moveUp = new AbstractAction("up"){
        public void actionPerformed(ActionEvent actionEvent) {
            handleMoveElement(-1);
        }
    };

    private AbstractAction moveDown = new AbstractAction("down"){
        public void actionPerformed(ActionEvent actionEvent) {
            handleMoveElement(1);
        }
    };

    protected void initialise(OWLEditorKit eKit, PatternDescriptor<ListExpression> descr) {
        repaintComponents();
    }

    protected void refresh() {

        repaintComponents();

        if (isCreateMode()){
            OWLModelManager mngr = getOWLEditorKit().getModelManager();
            OWLModelManagerEntityRenderer ren = mngr.getOWLEntityRenderer();
            nextEditor.setText(ren.render(getPatternDescriptor().getDefaultNextProperty()));
            followedByEditor.setText(ren.render(getPatternDescriptor().getDefaultFollowedByProperty()));
            contentsEditor.setText(ren.render(getPatternDescriptor().getDefaultContentsProperty()));
        }
        else{
            ListExpression p = getPattern();
            startOpenCheckbox.setSelected(p.isStartOpen());
            endOpenCheckbox.setSelected(p.isEndOpen());
            negatedCheckbox.setSelected(p.isNegated());

            elementList.setListData(p.toArray());

            OWLModelManagerEntityRenderer ren = getOWLEditorKit().getModelManager().getOWLEntityRenderer();
            nextEditor.setText(ren.render(p.getNextProperty()));
            followedByEditor.setText(ren.render(p.getFollowedByProperty()));
            contentsEditor.setText(ren.render(p.getContentsProperty()));
        }
    }

    private void repaintComponents() {
        removeAll();

        if (isCreateMode()) {
            listParser = new ListExpressionParserImpl(getPatternDescriptor(),
                                                      getOWLEditorKit().getOWLModelManager());

            add(createExpressionPanel(), BorderLayout.CENTER);
            add(createFeaturesPanel(), BorderLayout.SOUTH);
        }
        else{
            startOpenCheckbox = new JCheckBox("Start of list open");
            startOpenCheckbox.setEnabled(false);

            endOpenCheckbox = new JCheckBox("End of list open");
            endOpenCheckbox.setEnabled(false);

            negatedCheckbox = new JCheckBox("Negated");
            negatedCheckbox.setEnabled(false);

            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.LINE_AXIS));
            optionsPanel.add(startOpenCheckbox);
            optionsPanel.add(endOpenCheckbox);
            optionsPanel.add(negatedCheckbox);

            cellRendererAdapter = new ListCellRendererAdapter();

            tableComponent = createListTable();

            add(optionsPanel, BorderLayout.NORTH);
            add(new JScrollPane(tableComponent), BorderLayout.CENTER);
            add(createFeaturesPanel(), BorderLayout.SOUTH);
        }

        validate();
        repaint();
    }

    public ListExpression createPattern() throws OWLException {
        if (currentlyValid) {
            return listParser.createOWLPattern(expressionField.getText());
        }
        return null;
    }

    public JComponent getFocusComponent() {
        return expressionField;
    }

    public ListExpressionDescriptor getPatternDescriptor() {
        return (ListExpressionDescriptor)super.getPatternDescriptor();
    }

    private JComponent createListTable() {
        JPanel listComponent = new JPanel(new BorderLayout());

        elementList = new OWLObjectList(getOWLEditorKit()){
            public ListCellRenderer getCellRenderer() {
                cellRendererAdapter.setCellRenderer(super.getCellRenderer());
                return cellRendererAdapter;
            }
        };

        JPanel toolbar = new JPanel();
        toolbar.add(new JButton(moveUp));
        toolbar.add(new JButton(moveDown));

        listComponent.add(new JScrollPane(elementList), BorderLayout.CENTER);
        listComponent.add(toolbar, BorderLayout.EAST);
//        elementList.setListData(getPattern().toArray());
        return listComponent;
    }


    private JComponent createFeaturesPanel() {
        JPanel c = new JPanel();
        c.setPreferredSize(new Dimension(400, 200));
        setupBorder("Properties", c);

        c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
        c.add(new JLabel("Directly following elements related by:"));
        nextEditor = new JTextField();
        nextEditor.setEnabled(false);
        c.add(nextEditor);

        c.add(new JLabel("Indirectly following elements are related by:"));
        followedByEditor = new JTextField();
        followedByEditor.setEnabled(false);
        c.add(followedByEditor);

        c.add(new JLabel("Contents are refered to by:"));
        contentsEditor = new JTextField();
        contentsEditor.setEnabled(false);
        c.add(contentsEditor);

        return c;
    }


///////////////////////////////////////////////////

    private void validateContent() {
        currentlyValid = false;
        try {
            listParser.isWellFormed(expressionField.getText());
            warningField.setText("");
            currentlyValid = true;
        }
        catch (OWLException e) {
            warningField.setText(e.getMessage());
        }
    }

    private JComponent createExpressionPanel() {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

        expressionField = new JTextArea();

        OWLAutoCompleter completer =
                new OWLAutoCompleter(getOWLEditorKit(), expressionField){
                    protected ListExpressionParser getParser() {
                        return listParser;
                    }
                };

        expressionField.getDocument().addDocumentListener(l);
        FontMetrics fm = expressionField.getFontMetrics(expressionField.getFont());
        int height = fm.getHeight() + 6;
        JScrollPane scroller = new JScrollPane(expressionField);
        scroller.setPreferredSize(new Dimension(0, height * 3));

        warningField = new JTextArea();
        warningField.setEnabled(false);
        JScrollPane scroller2 = new JScrollPane(warningField);
        scroller2.setPreferredSize(new Dimension(0, height * 5));

        c.add(new JLabel("List Expression:"));
        c.add(scroller);
        c.add(new JLabel("Info:"));
        c.add(scroller2);
        return c;
    }

    private void handleMoveElement(int amount) {
        int index = elementList.getSelectedIndex();
        if (index >= 0){
            newIndex = index + amount;
            if (newIndex >= 0 && newIndex < getPattern().size()){
                OWLDescription element = getPattern().remove(index);
                getPattern().add(newIndex, element);
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        elementList.setSelectedIndex(newIndex);
                    }
                });
            }
        }
    }

    private void handleDeleteElement(){
        int index = elementList.getSelectedIndex();
        if (index >= 0){
            getPattern().remove(index);
            newIndex = Math.max(index-1, 0);
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    elementList.setSelectedIndex(newIndex);
                }
            });
        }
    }

    private void setupBorder(String name, JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                                BorderFactory.createEmptyBorder(7, 7, 7, 7)
                        ),
                        name
                )
        ));
    }

    class ListCellRendererAdapter implements ListCellRenderer {

        private ListCellRenderer realRenderer;

        public Component getListCellRendererComponent(JList jList, Object object, int i, boolean b, boolean b1) {
            if (object == null){
                return new JLabel(ListExpressionVocabulary.ANYTHING);
            }
            else{
                return realRenderer.getListCellRendererComponent(jList, object, i, b, b1);
            }
        }

        public void setCellRenderer(ListCellRenderer cellRenderer) {
            realRenderer = cellRenderer;
        }
    }
}
