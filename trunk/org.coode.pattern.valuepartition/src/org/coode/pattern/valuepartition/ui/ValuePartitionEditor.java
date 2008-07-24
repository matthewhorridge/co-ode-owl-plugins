package org.coode.pattern.valuepartition.ui;

import org.apache.log4j.Logger;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.impl.AbstractPatternEditor;
import org.coode.pattern.util.PatternUtils;
import org.coode.pattern.valuepartition.ValuePartition;
import org.coode.pattern.valuepartition.ValuePartition2;
import org.coode.pattern.valuepartition.ValuePartitionDescriptor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Keymap;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class ValuePartitionEditor extends AbstractPatternEditor<ValuePartition> {

    private final Logger logger = Logger.getLogger(ValuePartitionEditor.class);

    private JTextField nameField;
    private JTextField propField;
    private JCheckBox propFuncCheck;
    private JTextArea valuesField;
    private PieChart pie;
    private OWLObjectTree tree;
    private JComponent valuesPlaceholder;

    private JPanel valuesWidget; // contents specific to whether create time

    private boolean syncPropName = true;

    private ValuePartition2.Params params = new ValuePartition2.Params();

    private Action addValueAction = new AbstractAction("Add Value", OWLIcons.getIcon("class.add.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleAddValue();
        }
    };

    private Action removeValueAction = new AbstractAction("Remove Value", OWLIcons.getIcon("class.deleteFromOntologies.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleRemoveValue();
        }
    };

    private Action updateStringValueAction = new AbstractAction("update string"){
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == nameField){
                handleNameUpdated();
                handlePropertyUpdated();
            }
            else if (actionEvent.getSource() == propField){
                handlePropertyUpdated();
            }
        }
    };

    private ChangeListener functionalBoxListener = new ChangeListener(){
        public void stateChanged(ChangeEvent changeEvent) {
            handleFunctionalUpdated();
        }
    };

    private FocusListener textFieldFocusListener = new FocusAdapter(){
        public void focusLost(FocusEvent focusEvent) {
            if (focusEvent.getSource().equals(nameField)){
                handleNameFocusLost();
            }
            else{
                handlePropertyFocusLost();
            }
        }
    };

    private KeyListener propertyFieldTypingListener = new KeyAdapter(){
        public void keyTyped(KeyEvent keyEvent) {
            syncPropName = false;
        }
    };

    private DocumentListener valuesChangedListener = new DocumentListener(){
        public void insertUpdate(DocumentEvent documentEvent) {
            valuesChanged();
        }
        public void removeUpdate(DocumentEvent documentEvent) {
            valuesChanged();
        }
        public void changedUpdate(DocumentEvent documentEvent) {
            valuesChanged();
        }
    };

    private DocumentListener nameFieldListener = new DocumentListener(){
        public void insertUpdate(DocumentEvent documentEvent) {
            syncPropName();
        }
        public void removeUpdate(DocumentEvent documentEvent) {
            syncPropName();
        }
        public void changedUpdate(DocumentEvent documentEvent) {
            syncPropName();
        }
    };

    protected void initialise(OWLEditorKit eKit, PatternDescriptor<ValuePartition> descr) {

        setBorder(new EmptyBorder(0, 0, 0, 0));

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                             createLeftComponent(),
                                             createRightComponent());
        splitter.setResizeWeight(0.6);


        add(splitter, BorderLayout.CENTER);

        setPattern(null);
    }

    public void setPattern(ValuePartition p) {
        disableListeners(p);

        updateValuesWidget(p);

        if (p == null){
            params = new ValuePartition2.Params();

            Keymap keymap = nameField.getKeymap();
            keymap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

            nameField.setText("");
            propField.setText("");
            propFuncCheck.setSelected(true);
            redrawPie(Collections.EMPTY_SET);
        }
        else{
            params = null;//@@TODO p.getParams();

            OWLEditorKit eKit = getOWLEditorKit();
            OWLModelManager mngr = eKit.getModelManager();

            redrawPie(params.values);
            nameField.setText(mngr.getRendering(params.base));
            propField.setText(mngr.getRendering(params.property));
            if (!propField.getText().equals("has" + nameField.getText())){
                syncPropName = false;
            }

            Keymap keymap = nameField.getKeymap();
            keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                                         updateStringValueAction);

            propFuncCheck.setSelected(params.functional);

            // replace the tree
            if (tree != null){
                valuesPlaceholder.remove(tree);
            }

            OWLObjectHierarchyProvider<OWLClass> provider =
                    new OWLObjectHierarchyProviderAdapter(mngr.getOWLClassHierarchyProvider(), params.values);
            tree = new OWLModelManagerTree<OWLClass>(eKit, provider);
            tree.setRootVisible(false);

            valuesPlaceholder.add(tree);
//            valuesPlaceholder.validate();
        }

        enableListeners(p);
    }

    private void updateValuesWidget(ValuePartition p) {

        if (p == null) {
            if (valuesField == null){
                valuesWidget.removeAll();
                valuesField = new JTextArea();
                valuesField.getDocument().addDocumentListener(valuesChangedListener);

                valuesWidget.add(new JLabel("Values"), BorderLayout.NORTH);
                valuesWidget.add(new JScrollPane(valuesField), BorderLayout.CENTER);
            }
            valuesPlaceholder = null;
        }
        else {
            if (valuesPlaceholder == null){
                valuesWidget.removeAll();

                JPanel actionsPanel = createActionsPanel();
                JScrollPane scroller = new JScrollPane();
                valuesPlaceholder = scroller.getViewport(); // this is where the tree will go

                valuesWidget.add(actionsPanel, BorderLayout.NORTH);
                valuesWidget.add(scroller, BorderLayout.CENTER);
            }

            if (valuesField != null){
                valuesField.getDocument().removeDocumentListener(valuesChangedListener);
                valuesField = null;
            }
        }
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel();

        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.LINE_AXIS));

        JButton addSub = new JButton(addValueAction);
        addSub.setToolTipText(addSub.getText());
        addSub.setText("");

        JButton removeSub = new JButton(removeValueAction);
        removeSub.setToolTipText(removeSub.getText());
        removeSub.setText("");

        actionsPanel.add(new JLabel("Values"));
        actionsPanel.add(Box.createHorizontalGlue());
        actionsPanel.add(addSub);
        actionsPanel.add(removeSub);

        return actionsPanel;
    }

    private void enableListeners(ValuePartition p) {

        propField.addKeyListener(propertyFieldTypingListener);

        if (p == null){
            nameField.getDocument().addDocumentListener(nameFieldListener);
        }
        else{
            propFuncCheck.addChangeListener(functionalBoxListener);

            propField.addFocusListener(textFieldFocusListener);
            nameField.addFocusListener(textFieldFocusListener);
        }
    }

    private void disableListeners(ValuePartition p) {

        propField.removeKeyListener(propertyFieldTypingListener);

        if (p == null){
            nameField.getDocument().removeDocumentListener(nameFieldListener);
        }
        else{
            propFuncCheck.removeChangeListener(functionalBoxListener);
            propField.removeFocusListener(textFieldFocusListener);
        }
    }

    private JComponent createLeftComponent() {
        pie = new PieChart();

        JPanel leftComponent = new JPanel(new BorderLayout(6, 6));
        leftComponent.setPreferredSize(new Dimension(200, 200));
        leftComponent.setBackground(Color.WHITE);
        leftComponent.add(pie, BorderLayout.CENTER);

        return leftComponent;
    }

    private JComponent createRightComponent() {

        JComponent host = new JPanel(new BorderLayout());

        JPanel component = new JPanel();
        component.setLayout(new BoxLayout(component, BoxLayout.PAGE_AXIS));
        component.setBorder(new EmptyBorder(new Insets(6, 6, 6, 6)));

        valuesWidget = new JPanel(new BorderLayout(6, 6));
        valuesWidget.setPreferredSize(new Dimension(valuesWidget.getPreferredSize().width, 200));

        component.add(createNameComponent());
        component.add(Box.createVerticalGlue());
        component.add(createPropertyComponent());
        component.add(Box.createVerticalGlue());
        component.add(valuesWidget);

        host.add(component, BorderLayout.NORTH);

        return host;
    }

    public ValuePartition createPattern() throws OWLException {
        final OWLDataFactory df = getOWLEditorKit().getModelManager().getOWLDataFactory();
        return null; // @@TODO new ValuePartition(params, df, getPatternDescriptor());
    }

    public JComponent getFocusComponent() {
        return nameField;
    }

    public ValuePartitionDescriptor getPatternDescriptor() {
        return (ValuePartitionDescriptor)super.getPatternDescriptor();
    }

    public void disposePatternEditor() {
        if (tree != null){
            tree.dispose();
            tree = null;
        }
        pie = null;
    }

////////////////////////////////

    private void redrawPie(Set<OWLClass> values) {
        pie.clearValues();
        drawPie(values);
    }

    private void drawPie(Set<OWLClass> values) {
        if (values == null) {
            pie.clearValues();
        }
        else {
            for (OWLClass value : values) {
                pie.addValue(10, getOWLEditorKit().getModelManager().getRendering(value));
            }
        }
        pie.repaint();
    }

    private JComponent createNameComponent() {
        JComponent c = new JPanel(new BorderLayout(6, 6));

        nameField = new JTextField();

        c.add(new JLabel("Name"), BorderLayout.NORTH);
        c.add(nameField, BorderLayout.CENTER);

        return c;
    }

    private JComponent createPropertyComponent() {
        JPanel c = new JPanel(new BorderLayout(6, 6));

        propFuncCheck = new JCheckBox();
        propField = new JTextField();

        JComponent header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
        header.add(new JLabel("Property"));
        header.add(Box.createHorizontalGlue());
        header.add(new JLabel("Functional"));
        header.add(propFuncCheck);

        c.add(header, BorderLayout.NORTH);
        c.add(propField, BorderLayout.CENTER);

        return c;
    }

////////////////////////////////////// user input handling

    private void handleChanges() {
        if (getPattern() != null){
            final OWLModelManager mngr = getOWLEditorKit().getModelManager();
            OWLDataFactory df = mngr.getOWLDataFactory();
//            ValuePartition newVP = new ValuePartition(params, df, getPatternDescriptor());
//            java.util.List<OWLOntologyChange> changes = ValuePartition.replace(getPattern(), newVP,
//                                                                                mngr.getActiveOntology(),
//                                                                                mngr.getActiveOntologies());
//            setPattern(newVP);
//            mngr.applyChanges(changes);
        }
    }

    private void handleAddValue() {
        try {
            final OWLModelManager mngr = getOWLEditorKit().getModelManager();
            OWLDataFactory df = mngr.getOWLDataFactory();
            // should the class creation be done in the pattern model?
            OWLEntityCreationPanel.URIShortNamePair pair =
                    OWLEntityCreationPanel.showDialog(getOWLEditorKit(),
                                                      "Add Value",
                                                      OWLEntityCreationPanel.TYPE_CLASS);
            URI uri = new URI(pair.getUri().toString() + "#" + pair.getShortName());
            params.base = df.getOWLClass(uri);
            handleChanges();
        }
        catch (URISyntaxException e) {
            logger.error(e);
        }
    }

    private void handleRemoveValue(){
        OWLClass value = (OWLClass)tree.getSelectedOWLObject();
        if (value != null){
            params.values.remove(value);
            handleChanges();
        }
    }

    private void handlePropertyUpdated(){
        final OWLModelManager mngr = getOWLEditorKit().getModelManager();
        params.property = PatternUtils.getNamedObjectProperty(propField.getText(),
                                                              mngr.getActiveOntology(),
                                                              mngr.getOWLDataFactory());
        handleChanges();
        propField.transferFocus();
    }

    private void handleNameUpdated(){
        final OWLModelManager mngr = getOWLEditorKit().getModelManager();
        params.base = PatternUtils.getNamedClass(nameField.getText(),
                                                 mngr.getActiveOntology(),
                                                 mngr.getOWLDataFactory());
        handleChanges();
        nameField.transferFocus();
    }

    private void handleFunctionalUpdated() {
        params.functional = propFuncCheck.isSelected();
        handleChanges();
    }

    private void handlePropertyFocusLost(){
        if (params.property != null){
            // revert to current value
            propField.setText(getOWLEditorKit().getModelManager().getRendering(params.property));
        }
    }

    private void handleNameFocusLost(){
        if (params.base != null){
            // revert to current value
            nameField.setText(getOWLEditorKit().getModelManager().getRendering(params.base));
        }
    }

    private void valuesChanged() {
        try {
            final OWLModelManager mngr = getOWLEditorKit().getModelManager();
            redrawPie(PatternUtils.createClasses(valuesField.getText(),
                                                 mngr.getActiveOntology(),
                                                 mngr.getOWLDataFactory()));
        }
        catch (OWLException e) {
            logger.error(e);
        }
    }

    private void syncPropName() {
        if (syncPropName){
            propField.setText("has" + nameField.getText());
        }
    }

//    private ValuePartition2.Params getParams(OWLOntology ont, OWLDataFactory df) throws OWLException {
//        ValuePartition2.Params params = new ValuePartition2.Params();
//
//        params.superCls = getPatternDescriptor().getDefaultRoot();
//        params.base = getNamedClass(nameField.getText(), ont, df);
//        params.roots = createClasses(valuesField.getText());
//        params.property = getNamedObjectProperty(propField.getText(), ont, df);
//        params.functional = propFuncCheck.isSelected();
//
//        return params;
//    }
}
