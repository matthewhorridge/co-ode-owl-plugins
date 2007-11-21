package org.coode.pattern.ui;

import org.apache.log4j.Logger;
import org.coode.pattern.api.Pattern;
import org.coode.pattern.api.PatternDescriptor;
import org.coode.pattern.api.PatternEditor;
import org.coode.pattern.impl.PatternManagerFactory;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 1, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class PatternBrowserView extends AbstractPatternView {

    private final Logger logger = Logger.getLogger(PatternBrowserView.class);

    private static final String CREATE_NEW_PATTERN = "Create New Pattern";
    private static final String DELETE_PATTERN = "Delete Pattern";
    private static final String SCAN = "Scan Active Ontologies for Patterns";

    private PatternTreeModel patternTreeModel;

    private JTree patternTree;

    private UIHelper uiHelper;

    private OWLModelManagerListener owlModelListener = new OWLModelManagerListener() {
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                refresh();
            }
        }
    };

    private TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
            Object treeSelection = treeSelectionEvent.getPath().getLastPathComponent();
            treeSelectionChanged(treeSelection);
        }
    };

    private DisposableAction refreshAction =
            new DisposableAction(SCAN, Icons.getIcon("object.search.gif")) {

                public void dispose() {
                }

                public void actionPerformed(ActionEvent actionEvent) {
                    refresh();
                }
            };

    private DisposableAction createPatternAction =
            new DisposableAction(CREATE_NEW_PATTERN, Icons.getIcon("object.add.gif")) {

                public void dispose() {
                }

                public void actionPerformed(ActionEvent actionEvent) {
                    createPattern();
                }
            };

    private DisposableAction deletePatternAction =
            new DisposableAction(DELETE_PATTERN, Icons.getIcon("object.remove.gif")) {
                public void dispose() {
                }
                public void actionPerformed(ActionEvent actionEvent) {
                    deletePattern();
                }
            };

    protected void initialisePatternView() {

        uiHelper = new UIHelper(getOWLEditorKit());

        OWLModelManager mngr = getOWLModelManager();

        // build the GUI
        setLayout(new BorderLayout(6, 6));
        patternTreeModel = new PatternTreeModel(getPatternManager(), mngr);
        patternTree = new JTree(patternTreeModel);
        patternTree.setCellRenderer(new PatternCellRenderer(getOWLEditorKit()));
        patternTree.getSelectionModel().addTreeSelectionListener(treeSelectionListener);

        add(new JScrollPane(patternTree), BorderLayout.CENTER);

        getOWLModelManager().addListener(owlModelListener);

        addAction(refreshAction, "A", "A");
        addAction(createPatternAction, "B", "A");
        addAction(deletePatternAction, "B", "B");
    }

    public void refresh() {
        patternTreeModel.reload();
    }

    protected void disposePatternView() {
        getOWLModelManager().removeListener(owlModelListener);
        patternTree.getSelectionModel().removeTreeSelectionListener(treeSelectionListener);
    }

    public void selectionChanged(Pattern pattern) {
        if (pattern != null){
            TreePath path = new TreePath(new Object[]{patternTreeModel.getRoot(), pattern.getDescriptor(), pattern});
            patternTree.getSelectionModel().setSelectionPath(path);
            patternTree.expandPath(path);
        }
    }

    public void selectionChanged(PatternDescriptor patternDescr) {
        //@@TODO implement
    }

    private void createPattern() {
        PatternDescriptor selection = getSelectedPatternType();
        PatternEditor editor = getPatternEditor(selection, null);

        JComponent c = editor.getComponent();

        if (EditorPane.showDialog(CREATE_NEW_PATTERN, c,
                                  editor.getFocusComponent(), getOWLWorkspace()) == JOptionPane.OK_OPTION) {
            try {
                Pattern newPattern = editor.createPattern();
                final OWLModelManager mngr = getOWLModelManager();
                List<OWLOntologyChange> changes = newPattern.getChanges(mngr.getOWLOntologyManager(),
                                                                        mngr.getActiveOntology(),
                                                                        mngr.getActiveOntologies());
                mngr.applyChanges(changes);
                PatternManagerFactory.getOWLPatternManager().notifyPatternChanged(newPattern);

                patternTreeModel.addPattern(newPattern); // shortcut to having to do a whole rescan
                selectionChanged(newPattern);
            }
            catch (OWLException e) {
                logger.error(e);
            }
        }
    }

    private void deletePattern() {
        Pattern selectedPattern = getSelectedPattern();
        if (selectedPattern != null){
            System.out.println("DELETING: " + selectedPattern);
            final OWLModelManager mngr = getOWLModelManager();
            mngr.applyChanges(selectedPattern.delete(mngr.getOWLOntologyManager(),
                                                     mngr.getActiveOntologies()));
            patternTree.getSelectionModel().removeTreeSelectionListener(treeSelectionListener);
            patternTreeModel.removePattern(selectedPattern);
            patternTree.getSelectionModel().addTreeSelectionListener(treeSelectionListener);
        }
        else{
            System.out.println("NOT DELETING");
        }
    }


///////////////////////////////////////////////////////////////////////

    private Pattern getSelectedPattern() {
        Object selection = patternTree.getSelectionPath().getLastPathComponent();
        if (selection instanceof Pattern) {
            return (Pattern) selection;
        }
        return null;
    }

    private PatternDescriptor getSelectedPatternType() {
        Object selection = patternTree.getSelectionPath().getLastPathComponent();
        if (selection instanceof PatternDescriptor) {
            return (PatternDescriptor) selection;
        }
        else if (selection instanceof Pattern) {
            return ((Pattern) selection).getDescriptor();
        }
        return null;
    }

    private void treeSelectionChanged(Object treeSelection) {
        // @@TODO buttons should be disabled if no editor available

        if (treeSelection instanceof Pattern) {
            Pattern pattern = (Pattern) treeSelection;
            changeSelection(pattern);
            createPatternAction.setEnabled(true);
            deletePatternAction.setEnabled(true);
        }
        else if (treeSelection instanceof PatternDescriptor) {
            PatternDescriptor patternDescriptor = (PatternDescriptor) treeSelection;
            changeSelection(patternDescriptor);
            createPatternAction.setEnabled(true);
            deletePatternAction.setEnabled(false);
        }
        else if (treeSelection instanceof OWLObject && !(treeSelection instanceof OWLOntology)){
            OWLObject owlEntity = (OWLObject) treeSelection;
            Pattern pattern = (Pattern)patternTree.getSelectionPath().getParentPath().getLastPathComponent();
            changeSelection(pattern, owlEntity);
            createPatternAction.setEnabled(false);
            deletePatternAction.setEnabled(false);
        }
        else {
            changeSelection((Pattern) null);
            changeSelection((PatternDescriptor) null);
            createPatternAction.setEnabled(false);
            deletePatternAction.setEnabled(false);
        }
    }

    class PatternCellRenderer extends OWLCellRenderer {
        DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        public PatternCellRenderer(OWLEditorKit owlEditorKit) {
            super(owlEditorKit);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (value instanceof OWLObject){
                return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);    //@@TODO replace call to super
            }
            else if (value instanceof Pattern){
                final PatternDescriptor descriptor = ((Pattern) value).getDescriptor();
                PatternRenderer ren = PatternEditorKit.getPatternEditorKit(getOWLEditorKit()).getRenderer(descriptor);
                value = ren.render((Pattern)value);
            }
            defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            return defaultRenderer;
        }
    }
}
