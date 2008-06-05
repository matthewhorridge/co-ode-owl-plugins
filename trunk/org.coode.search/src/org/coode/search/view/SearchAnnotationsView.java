package org.coode.search.view;

import org.coode.search.model.AnnotationFinder;
import org.protege.editor.owl.ui.frame.AxiomListFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.framelist.OWLFrameListRenderer;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Mar 18, 2008<br><br>
 */
public class SearchAnnotationsView extends AbstractActiveOntologyViewComponent {

    private URI filterUri = OWLRDFVocabulary.RDFS_LABEL.getURI();
    private Set<URI> usedURIs = new HashSet<URI>();

    private OWLFrameList2<Set<OWLAxiom>> list;
    private JCheckBox filterCheckbox;
    private JCheckBox regexpCheckbox;
    private JComboBox annotURISelector;
    private JTextField searchField;

    private ActionListener actionListener = new ActionListener(){

        public void actionPerformed(ActionEvent actionEvent) {
            try {
                updateView(getOWLModelManager().getActiveOntology());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private ItemListener annotURISelectionListener = new ItemListener(){
        public void itemStateChanged(ItemEvent itemEvent) {
            try {
                filterUri = (URI)itemEvent.getItem();

                if (filterCheckbox.isSelected()){
                    updateView(getOWLModelManager().getActiveOntology());
                }
                else{
                    filterCheckbox.setSelected(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private DocumentListener searchFieldChangeListener = new DocumentListener(){

        public void insertUpdate(DocumentEvent documentEvent) {
            timer.restart();
        }

        public void removeUpdate(DocumentEvent documentEvent) {
            timer.restart();
        }

        public void changedUpdate(DocumentEvent documentEvent) {
            timer.restart();
        }
    };

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) throws OWLException {
            handleOntologyChanges(owlOntologyChanges);
        }
    };

    private Runnable searcher = new Runnable(){
        public void run() {
            String str = searchField.getText();

            if (str != null && str.length() > 0){
                if (!regexpCheckbox.isSelected()){
//                    str = str.toLowerCase();
                    str = "(?s)(?i)" + str; // ignore newlines and case
                    str = str.replaceAll("\\*", ".*");
                    if (!str.endsWith(".*")){
                        str += ".*";
                    }
                    if (!str.startsWith(".*")){
                        str = ".*" + str;
                    }
                }
            }
            else{
                str = null;
            }

            final boolean filter = filterCheckbox.isSelected();
            annotURISelector.setEnabled(filter);

            Set<OWLAxiom> axioms = null;

            if (filter){
                axioms = new AnnotationFinder().getAnnotationAxioms(filterUri, str, getOWLModelManager().getActiveOntologies());
            }
            else{
                axioms = new AnnotationFinder().getAnnotationAxioms(usedURIs, str, getOWLModelManager().getActiveOntologies());
            }
            list.setRootObject(axioms);
            currentSearch = null;
        }
    };

    // implement a wait after each keypress
    private static final int SEARCH_PAUSE_MILLIS = 1000;
    private Timer timer;

    private Thread currentSearch;


    protected void initialiseOntologyView() throws Exception {
        setLayout(new BorderLayout());

        list = new OWLFrameList2<Set<OWLAxiom>>(getOWLEditorKit(), new AxiomListFrame(getOWLEditorKit()));
        final OWLFrameListRenderer listRenderer = new OWLFrameListRenderer(getOWLEditorKit());
        listRenderer.setHighlightKeywords(false);
        listRenderer.setAnnotationRendererEnabled(false);
        list.setCellRenderer(listRenderer);

        add(new JScrollPane(list), BorderLayout.CENTER);

        JComponent searchPane = new JPanel();
        searchPane.setLayout(new BoxLayout(searchPane, BoxLayout.LINE_AXIS));

        filterCheckbox = new JCheckBox("filter");
        searchPane.add(filterCheckbox);

        annotURISelector = new JComboBox();
        annotURISelector.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList jList, Object object, int i, boolean b, boolean b1) {
                if (object != null){
                    object = new SimpleURIShortFormProvider().getShortForm((URI)object);
                }
                return super.getListCellRendererComponent(jList, object, i, b, b1);
            }
        });

        searchPane.add(annotURISelector);

        searchField = new JTextField();
        searchPane.add(searchField);

        regexpCheckbox = new JCheckBox("regexp");
        searchPane.add(regexpCheckbox);

        add(searchPane, BorderLayout.NORTH);

        loadCombo();

        annotURISelector.addItemListener(annotURISelectionListener);
        searchField.getDocument().addDocumentListener(searchFieldChangeListener);
        filterCheckbox.addActionListener(actionListener);
        regexpCheckbox.addActionListener(actionListener);

        // listen for additional annotations to allow the annotation filter list to be updated
        getOWLModelManager().addOntologyChangeListener(ontChangeListener);

        timer = new Timer(SEARCH_PAUSE_MILLIS, actionListener);
    }


    private void loadCombo() {
        usedURIs.clear();
        annotURISelector.removeAllItems();
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()){
            usedURIs.addAll(ont.getAnnotationURIs());
        }
        for (URI uri : usedURIs){
            annotURISelector.addItem(uri);
        }
        annotURISelector.setSelectedItem(filterUri);
    }

    protected void disposeOntologyView() {
        list.dispose();
        annotURISelector.removeItemListener(annotURISelectionListener);
        getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
        searchField = null;
        annotURISelector = null;
    }

    protected synchronized void updateView(OWLOntology activeOntology) throws Exception {
        timer.stop();

        if (currentSearch != null && currentSearch.isAlive()){
            currentSearch.interrupt();
        }

        currentSearch = new Thread(searcher);

        currentSearch.run();
    }

    private void handleOntologyChanges(List<? extends OWLOntologyChange> owlOntologyChanges) {
        for (OWLOntologyChange c : owlOntologyChanges){
            if (c.getAxiom().getAxiomType().equals(AxiomType.ENTITY_ANNOTATION)){
                if (c instanceof AddAxiom){
                    URI uri = ((OWLEntityAnnotationAxiom) c.getAxiom()).getAnnotation().getAnnotationURI();

                    if (!uriSelectorContain(uri)){
                        annotURISelector.addItem(uri);
                    }
                }
                else if (c instanceof RemoveAxiom){
                    loadCombo();
                    return;
                }
            }
        }
    }


    private boolean uriSelectorContain(URI uri) {
        for (int i=0; i<annotURISelector.getModel().getSize(); i++){
            if (uri.equals(annotURISelector.getModel().getElementAt(i))){
                return true;
            }
        }
        return false;
    }
}
