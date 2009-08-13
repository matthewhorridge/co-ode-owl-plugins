package org.coode.change.diff;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.editor.owl.ui.frame.AxiomListFrameSection;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 24, 2008<br><br>
 */
public class AxiomsPanel extends JPanel implements OntologySelector, Disposable {

    private OWLEditorKit eKit;

    private JComboBox ontologySelector;
    private OWLFrameList <Set<OWLAxiom>> list;
    private OWLOntology currentOntology;

    private OntologySelector diff;

    private List<OWLOntologySelectionListener> listeners = new ArrayList<OWLOntologySelectionListener>();

    private OWLOntologyChangeListener ontChangeListener = new OWLOntologyChangeListener(){
        public void ontologiesChanged(java.util.List<? extends OWLOntologyChange> changes) throws OWLException {
            handleChanges(changes);
        }
    };


    private ItemListener itemSelectorListener = new ItemListener(){
        public void itemStateChanged(ItemEvent event) {
            setOntology((OWLOntology)ontologySelector.getSelectedItem());
        }
    };


    private OWLOntologySelectionListener diffListener = new OWLOntologySelectionListener(){
        public void selectionChanged(OWLOntology selectedOntology) {
            refresh();
        }
    };

    private OWLModelManagerListener mngrListener = new OWLModelManagerListener(){

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType().equals(EventType.ONTOLOGY_LOADED)){
                reloadOntologySelector();
            }
            else if (event.getType().equals(EventType.ONTOLOGY_CREATED)){
                reloadOntologySelector();
            }
        }
    };

    private boolean updateViewRequired = true;

    private HierarchyListener componentHierarchyListener = new HierarchyListener() {
        public void hierarchyChanged(HierarchyEvent hierarchyEvent) {
            if (isShowing()) {
                if (updateViewRequired) {
                    refresh();
                }
            }
        }
    };


    private boolean showAnnotations = true;

    private boolean showDisjointClasses = true;


    AxiomsPanel(OWLEditorKit eKit) {
        setLayout(new BorderLayout());

        this.eKit = eKit;

        setOntology(eKit.getOWLModelManager().getActiveOntology());
        
        ontologySelector = new JComboBox();
        reloadOntologySelector();
        ontologySelector.setRenderer(new OWLOntologyCellRenderer(eKit));

        OWLAxiomListFrame frame = new OWLAxiomListFrame(eKit);
        list = new OWLFrameList<Set<OWLAxiom>>(eKit, frame);

        add(ontologySelector, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);

        OWLModelManager mngr = eKit.getOWLModelManager();

        mngr.addOntologyChangeListener(ontChangeListener);
        mngr.addListener(mngrListener);

        ontologySelector.addItemListener(itemSelectorListener);

        addHierarchyListener(componentHierarchyListener);
    }


    void setDiff(OntologySelector diff){
        if (this.diff != null && !this.diff.equals(diff)){
            this.diff.removeOntologySelectionListener(diffListener);
        }
        this.diff = diff;
        if (this.diff != null){
            diff.addOntologySelectionListener(diffListener);
        }
        refresh();
    }


    void setOntology(OWLOntology ontology) {
        this.currentOntology = ontology;
        for (OWLOntologySelectionListener l : listeners){
            l.selectionChanged(currentOntology);
        }
        refresh();
    }


    private void reloadOntologySelector() {
        Set<OWLOntology> ontologies = eKit.getOWLModelManager().getOntologies();
        ontologySelector.setModel(new DefaultComboBoxModel(ontologies.toArray()));
        if (ontologies.contains(currentOntology)){
            ontologySelector.setSelectedItem(currentOntology);
        }
    }


    private void refresh() {
        if (isShowing()){
            final Set<OWLAxiom> axioms = new HashSet<OWLAxiom>(isShowAnnotations() ? currentOntology.getAxioms() : currentOntology.getLogicalAxioms());
            if (!showDisjointClasses){
                axioms.removeAll(currentOntology.getAxioms(AxiomType.DISJOINT_CLASSES));
            }
            if (diff != null &&
                diff.getSelectedOntology() != null &&
                !diff.getSelectedOntology().equals(currentOntology)){
                final Set<OWLAxiom> diffAxioms = diff.getSelectedOntology().getAxioms();
                axioms.removeAll(diffAxioms);
            }
            list.setRootObject(axioms);
            updateViewRequired = false;
        }
        else{
            updateViewRequired = true;
        }
    }


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        boolean requiresReload = false;
        for (OWLOntologyChange chg : changes){
            if (chg.getOntology().equals(currentOntology)){
                // would be more efficient if we just add/remove as required
                requiresReload = true;
            }
        }
        if (requiresReload){
            refresh();
        }
    }


    public void dispose() throws Exception {
        list.dispose();
        eKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
        eKit.getOWLModelManager().removeListener(mngrListener);
        ontologySelector = null;
    }


    public OWLOntology getSelectedOntology() {
        return currentOntology;
    }


    public void addOntologySelectionListener(OWLOntologySelectionListener l) {
        listeners.add(l);
    }


    public void removeOntologySelectionListener(OWLOntologySelectionListener l) {
        listeners.remove(l);
    }


    public boolean isShowAnnotations() {
        return showAnnotations;
    }


    public void setShowAnnotations(boolean showAnnotations) {
        this.showAnnotations = showAnnotations;
        refresh();
    }


    public void setShowDisjointClasses(boolean showDisjointClasses) {
        this.showDisjointClasses = showDisjointClasses;
        refresh();
    }


    class OWLAxiomListFrame extends AbstractOWLFrame<Set<OWLAxiom>> {

        public OWLAxiomListFrame(OWLEditorKit owlEditorKit) {
            super(owlEditorKit.getModelManager().getOWLOntologyManager());
            addSection(new AxiomListFrameSection(owlEditorKit, this){
                protected Set<OWLOntology> getOntologies() {
                    return Collections.singleton(currentOntology);
                }
            });
        }
    }
}