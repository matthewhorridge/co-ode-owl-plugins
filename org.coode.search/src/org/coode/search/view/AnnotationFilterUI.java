package org.coode.search.view;

import org.coode.search.model.AnnotationFinder;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;
import java.util.*;
import java.util.List;/*
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
 * Date: Jun 26, 2008<br><br>
 */
public class AnnotationFilterUI extends JComponent {

    private static final String ALL_ANNOTATIONS = "All annotations";

    // implement a wait after each keypress
    private static final int SEARCH_PAUSE_MILLIS = 1000;

    private Timer timer;

    private Thread currentSearch;

    private Set<OWLAxiom> results = new HashSet<OWLAxiom>();

    private Runnable searcher = new Runnable(){
        public void run() {
            handleSearch();
        }
    };

    private Set<URI> allURIs = new HashSet<URI>();

    private OWLModelManager mngr;

    private ResultsView resultsView;

    private JCheckBox regexpCheckbox;
    private JComboBox annotURISelector;
    private JTextField searchField;

    private URI filterUri;

    private ActionListener actionListener = new ActionListener(){

        public void actionPerformed(ActionEvent actionEvent) {
            try {
                startSearch();
            }
            catch (Exception e) {
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


    private ItemListener annotURISelectionListener = new ItemListener(){
        public void itemStateChanged(ItemEvent itemEvent) {
            try {
                final Object item = itemEvent.getItem();
                if (item instanceof URI){
                    filterUri = (URI)item;
                }
                else{
                    filterUri = null;
                }
                startSearch();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) throws OWLException {
            handleOntologyChanges(owlOntologyChanges);
        }
    };


    public AnnotationFilterUI(ResultsView resultsView, OWLModelManager mngr) {

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        this.resultsView = resultsView;
        this.mngr = mngr;

        annotURISelector = new JComboBox();
        annotURISelector.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList jList, Object object, int i, boolean b, boolean b1) {
                if (object != null){
                    object = new SimpleURIShortFormProvider().getShortForm((URI)object);
                }
                else{
                    object = ALL_ANNOTATIONS;
                }
                return super.getListCellRendererComponent(jList, object, i, b, b1);
            }
        });

        add(annotURISelector);

        searchField = new JTextField();
        add(searchField);

        regexpCheckbox = new JCheckBox("regexp");
        add(regexpCheckbox);

        loadCombo();

        annotURISelector.addItemListener(annotURISelectionListener);
        searchField.getDocument().addDocumentListener(searchFieldChangeListener);
        regexpCheckbox.addActionListener(actionListener);

        // listen for additional annotations to allow the annotation filter list to be updated
        mngr.addOntologyChangeListener(ontChangeListener);

        timer = new Timer(SEARCH_PAUSE_MILLIS, actionListener);
    }


    public void startSearch(){
        timer.stop();

        if (currentSearch != null && currentSearch.isAlive()){
            currentSearch.interrupt();
        }

        currentSearch = new Thread(searcher);

        currentSearch.run();
    }


    private void handleSearch() {
        String str = searchField.getText();

        if (str != null && str.length() > 0){
            if (!regexpCheckbox.isSelected()){
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

        if (filterUri != null){
            results = new AnnotationFinder().getAnnotationAxioms(filterUri, str, mngr.getActiveOntologies());
        }
        else {
            if (str != null){
                results = new AnnotationFinder().getAnnotationAxioms(allURIs, str, mngr.getActiveOntologies());
            }
            else{
                results = Collections.emptySet(); // don't bother searching - we don't want to return all annotations
            }
        }

        resultsView.resultsChanged(this);

        currentSearch = null;
    }


    public Set<OWLAxiom> getResults(){
        return Collections.unmodifiableSet(results);
    }


    private void loadCombo() {
        allURIs.clear();
        annotURISelector.removeAllItems();
        annotURISelector.addItem(null);
        for (OWLOntology ont : mngr.getActiveOntologies()){
            allURIs.addAll(ont.getAnnotationURIs());
        }
        List<URI> orderedURIs = new ArrayList<URI>(allURIs);
        Collections.sort(orderedURIs, new Comparator<URI>() {
            SimpleURIShortFormProvider sfp = new SimpleURIShortFormProvider();
            public int compare(URI o1, URI o2) {
                return sfp.getShortForm(o1).compareToIgnoreCase(sfp.getShortForm(o2));
            }
        });
        for (URI uri : orderedURIs){
            annotURISelector.addItem(uri);
        }
        annotURISelector.setSelectedItem(filterUri);
    }


    private void handleOntologyChanges(java.util.List<? extends OWLOntologyChange> owlOntologyChanges) {
        for (OWLOntologyChange c : owlOntologyChanges){
            if (c.getAxiom().getAxiomType().equals(AxiomType.ENTITY_ANNOTATION)){
                if (c instanceof AddAxiom){
                    URI uri = ((OWLEntityAnnotationAxiom) c.getAxiom()).getAnnotation().getAnnotationURI();

                    if (!uriSelectorContains(uri)){
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


    private boolean uriSelectorContains(URI uri) {
        for (int i=0; i<annotURISelector.getModel().getSize(); i++){
            if (uri.equals(annotURISelector.getModel().getElementAt(i))){
                return true;
            }
        }
        return false;
    }


    public void dispose() {
        mngr.removeOntologyChangeListener(ontChangeListener);
        annotURISelector.removeItemListener(annotURISelectionListener);
        searchField.getDocument().removeDocumentListener(searchFieldChangeListener);
        regexpCheckbox.removeActionListener(actionListener);
    }
}
