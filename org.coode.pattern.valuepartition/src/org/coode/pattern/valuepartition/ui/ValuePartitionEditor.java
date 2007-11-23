package org.coode.pattern.valuepartition.ui;

import org.apache.log4j.Logger;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternException;
import org.coode.pattern.impl.AbstractPatternEditor;
import org.coode.pattern.valuepartition.EntityCreator;
import org.coode.pattern.valuepartition.ValuePartition;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;

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
import java.util.HashSet;
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

    private Action addValueAction = new AbstractAction("Add Value", OWLIcons.getIcon("class.add.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleAddValue();
        }
    };

    private Action removeValueAction = new AbstractAction("Remove Value", OWLIcons.getIcon("class.delete.png")){
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

        refresh();
    }

    protected void refresh() {
        disableListeners();

        updateValuesWidget();

        if (isCreateMode()){
            Keymap keymap = nameField.getKeymap();
            keymap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

            nameField.setText("");
            propField.setText("");
            propFuncCheck.setSelected(true);
            redrawPie(Collections.EMPTY_SET);
        }
        else{
            ValuePartition p = getPattern();

            redrawPie(p.getValues());

            OWLEditorKit eKit = getOWLEditorKit();
            OWLModelManager mngr = eKit.getOWLModelManager();
            OWLEntityRenderer ren = mngr.getOWLEntityRenderer();

            nameField.setText(ren.render(p.getBaseClass()));
            propField.setText(ren.render(p.getProperty()));
            if (!propField.getText().equals("has" + nameField.getText())){
                syncPropName = false;
            }

            Keymap keymap = nameField.getKeymap();
            keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                                         updateStringValueAction);

            propFuncCheck.setSelected(p.isFunctional());

            // replace the tree
            if (tree != null){
                valuesPlaceholder.remove(tree);
            }

            OWLObjectHierarchyProvider<OWLClass> provider = new MyValuesProvider(mngr);
            tree = new OWLModelManagerTree<OWLClass>(eKit, provider);
            tree.setRootVisible(false);

            valuesPlaceholder.add(tree);
//            valuesPlaceholder.validate();
        }

        enableListeners();
    }

    private void updateValuesWidget() {

        if (isCreateMode()) {
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

    private void enableListeners() {

        propField.addKeyListener(propertyFieldTypingListener);

        if (isCreateMode()){
            nameField.getDocument().addDocumentListener(nameFieldListener);
        }
        else{

            propFuncCheck.addChangeListener(functionalBoxListener);

            propField.addFocusListener(textFieldFocusListener);
            nameField.addFocusListener(textFieldFocusListener);
        }
    }

    private void disableListeners() {

        propField.removeKeyListener(propertyFieldTypingListener);

        if (isCreateMode()){
            nameField.getDocument().removeDocumentListener(nameFieldListener);
        }
        else{
            propFuncCheck.removeChangeListener(functionalBoxListener);
            propField.removeFocusListener(textFieldFocusListener);
        }
    }

    private JComponent createLeftComponent() {
//        JComponent c = new JPanel();
//        c.setBackground(Color.GREEN);
//        c.setOpaque(true);
//        return c;
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
        ValuePartition vp = new ValuePartition(nameField.getText(),
                                               propField.getText(),
                                               createClasses(valuesField.getText()),
                                               getPatternDescriptor(),
                                               getOWLEditorKit().getOWLModelManager(),
                                               new EntityCreator(getOWLEditorKit().getOWLModelManager()));
        vp.setFunctional(propFuncCheck.isSelected());
        return vp;
    }

    public JComponent getFocusComponent() {
        return nameField;
    }

    public void disposePatternEditor() {
        if (tree != null){
            tree.dispose();
            tree = null;
        }
        pie = null;
    }

/////////////////////////////////

    private Set<OWLClass> createClasses(String s) throws PatternException {
        Set<OWLClass> classes = new HashSet<OWLClass>();
        OWLOntology ont = getOWLEditorKit().getOWLModelManager().getActiveOntology();
        OWLDataFactory df = getOWLEditorKit().getOWLModelManager().getOWLDataFactory();
        String stem = ont.getURI().toString() + "#";
        for (String line : s.split("\n")) {
            try {
                classes.add(df.getOWLClass(new URI(stem + line)));
            }
            catch (URISyntaxException e) {
                throw new PatternException("Could not create class " + stem + line, e);
            }
        }
        return classes;
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
            OWLEntityRenderer ren = getOWLEditorKit().getOWLModelManager().getOWLEntityRenderer();
            for (OWLClass value : values) {
                pie.addValue(10, ren.render(value));
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

    private void handleAddValue() {
        try {
            OWLDataFactory df = getOWLEditorKit().getModelManager().getOWLDataFactory();
            // should the class creation be done in the pattern model?
            OWLEntityCreationPanel.URIShortNamePair pair =
                    OWLEntityCreationPanel.showDialog(getOWLEditorKit(),
                                                      "Add Value",
                                                      OWLEntityCreationPanel.TYPE_CLASS);
            URI uri = new URI(pair.getUri().toString() + "#" + pair.getShortName());
            OWLClass namedClass = df.getOWLClass(uri);
            getPattern().addValue(namedClass);
        }
        catch (URISyntaxException e) {
            logger.error(e);
        }
    }

    private void handleRemoveValue(){
        OWLClass value = (OWLClass)tree.getSelectedOWLObject();
        if (value != null){
            getPattern().removeValue(value);
        }
    }

    private void handlePropertyUpdated(){
        try {
            getPattern().setProperty(propField.getText());
        }
        catch (OWLException e) {
            logger.error(e);
        }
        finally{
            propField.transferFocus();
        }
    }

    private void handlePropertyFocusLost(){
        if (getPattern() != null){
            // revert to current value
            OWLEntityRenderer ren = getOWLEditorKit().getOWLModelManager().getOWLEntityRenderer();
            propField.setText(ren.render(getPattern().getProperty()));
        }
    }

    private void handleNameFocusLost(){
        if (getPattern() != null){
            // revert to current value
            OWLEntityRenderer ren = getOWLEditorKit().getOWLModelManager().getOWLEntityRenderer();
            nameField.setText(ren.render(getPattern().getBaseClass()));
        }
    }

    private void handleNameUpdated(){
        try {
            getPattern().setName(nameField.getText());
        }
        catch (OWLException e) {
            logger.error(e);
        }
        finally{
            nameField.transferFocus();
        }
    }

    private void handleFunctionalUpdated() {
        getPattern().setFunctional(propFuncCheck.isSelected());
    }

    private void valuesChanged() {
        try {
            redrawPie(createClasses(valuesField.getText()));
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

    private class MyValuesProvider implements OWLObjectHierarchyProvider<OWLClass>{
        private OWLObjectHierarchyProvider<OWLClass> provider;

        public MyValuesProvider(OWLModelManager mngr) {
            provider = mngr.getOWLClassHierarchyProvider();
        }

        public void setOntologies(Set<OWLOntology> ontologies) {
            provider.setOntologies(ontologies);
        }

        public void dispose() {
            provider.dispose();
        }

        public Set<OWLClass> getRoots() {
            if (getPattern() != null){
                return getPattern().getValues();
            }
            else{
                return Collections.EMPTY_SET;
            }
        }

        public Set<OWLClass> getChildren(OWLClass object) {
            return provider.getChildren(object);
        }

        public Set<OWLClass> getDescendants(OWLClass object) {
            return provider.getDescendants(object);
        }

        public Set<OWLClass> getParents(OWLClass object) {
            return provider.getParents(object);
        }

        public Set<OWLClass> getAncestors(OWLClass object) {
            return provider.getAncestors(object);
        }

        public Set<OWLClass> getEquivalents(OWLClass object) {
            return provider.getEquivalents(object);
        }

        public Set<java.util.List<OWLClass>> getPathsToRoot(OWLClass object) {
            return provider.getPathsToRoot(object);
        }

        public boolean containsReference(OWLClass object) {
            return provider.containsReference(object);
        }

        public void addListener(OWLObjectHierarchyProviderListener<OWLClass> owlObjectHierarchyProviderListener) {
            provider.addListener(owlObjectHierarchyProviderListener);
        }

        public void removeListener(OWLObjectHierarchyProviderListener<OWLClass> owlObjectHierarchyProviderListener) {
            provider.removeListener(owlObjectHierarchyProviderListener);
        }
    }
}
