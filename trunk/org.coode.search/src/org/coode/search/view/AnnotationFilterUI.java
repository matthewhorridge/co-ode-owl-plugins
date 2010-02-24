package org.coode.search.view;

import org.coode.search.model.AnnotationFinder;
import org.coode.search.ui.FlatButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;/*
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

    private Set<OWLAnnotationAssertionAxiom> results = new HashSet<OWLAnnotationAssertionAxiom>();

    private Runnable searcher = new Runnable(){
        public void run() {
            handleSearch();
        }
    };

    private OWLEditorKit eKit;

    private ResultsView resultsView;

    private JCheckBox regexpCheckbox;
    private JTextField searchField;

    private JButton propertySelectButton;

    private OWLAnnotationProperty filterByProperty;

    private ActionListener actionListener = new ActionListener(){

        public void actionPerformed(ActionEvent actionEvent) {
            startSearch();
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


    private Action propertySelectAction = new AbstractAction(ALL_ANNOTATIONS, OWLIcons.getIcon("property.annotation.png")){

        public void actionPerformed(ActionEvent event) {
            filterByProperty = new UIHelper(eKit).pickAnnotationProperty();
            if (filterByProperty != null){
                propertySelectButton.setText(eKit.getOWLModelManager().getRendering(filterByProperty));
            }
            else{
                propertySelectButton.setText(ALL_ANNOTATIONS);
            }
            startSearch();
        }
    };


    public AnnotationFilterUI(ResultsView resultsView, OWLEditorKit eKit) {

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        this.resultsView = resultsView;
        this.eKit = eKit;

        propertySelectButton = new FlatButton(propertySelectAction);

        add(propertySelectButton);

        searchField = new JTextField();
        add(searchField);

        regexpCheckbox = new JCheckBox("regexp");
        add(regexpCheckbox);

        searchField.getDocument().addDocumentListener(searchFieldChangeListener);
        regexpCheckbox.addActionListener(actionListener);

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


    public String getSearchText(){
        return searchField.getText();
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

        if (filterByProperty != null){
            results = new AnnotationFinder().getAnnotationAxioms(filterByProperty, str, eKit.getOWLModelManager().getActiveOntologies());
        }
        else {
            if (str != null){
                results = new AnnotationFinder().getAnnotationAxioms(getAllAnnotationProperties(), str, eKit.getOWLModelManager().getActiveOntologies());
            }
            else{
                results = Collections.emptySet(); // don't bother searching - we don't want to return all annotations
            }
        }

        resultsView.resultsChanged(this);

        currentSearch = null;
    }


    public Set<OWLAnnotationAssertionAxiom> getResults(){
        return Collections.unmodifiableSet(results);
    }


    public void dispose() {
        searchField.getDocument().removeDocumentListener(searchFieldChangeListener);
        regexpCheckbox.removeActionListener(actionListener);
    }


    public Set<OWLAnnotationProperty> getAllAnnotationProperties() {
        Set<OWLAnnotationProperty> allAnnotationProperties = new HashSet<OWLAnnotationProperty>();
        for (OWLOntology ont : eKit.getOWLModelManager().getActiveOntologies()){
            allAnnotationProperties.addAll(ont.getAnnotationPropertiesInSignature());
        }
        return allAnnotationProperties;
    }
}
