package uk.ac.manchester.gong.opl.protege;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;
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

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.NamespaceUtil;

import uk.ac.manchester.gong.opl.OPLInstructionsProcessor;
import uk.ac.manchester.gong.opl.ReasonerFactory;
import uk.ac.manchester.gong.opl.io.OPLReader;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 9, 2007<br><br>
 */
public class OPLComponent extends JComponent {
    private static final long serialVersionUID = 1L;
    private JTextArea textPanel;
    private OWLEditorKit eKit;

    private DisposableAction processAction = new DisposableAction("Process", null){
        private static final long serialVersionUID = 1L;
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            handleProcessAction();
        }
        @Override
        public void dispose() {
        }
    };

    public OPLComponent(OWLEditorKit eKit) {
        setLayout(new BorderLayout());

        this.eKit = eKit;

        textPanel = new JTextArea();
        add(new JScrollPane(textPanel), BorderLayout.CENTER);

        init();

        ReasonerFactory.setOWLModelManager(eKit.getModelManager());

    }

    public DisposableAction getRunAction(){
        return processAction;
    }

    private void init() {

        NamespaceUtil nsUtil = new NamespaceUtil();

        OWLOntology activeOnt = eKit.getModelManager().getActiveOntology();
        String nsText = nsUtil.getPrefix(activeOnt.getOntologyID()
                .getOntologyIRI().get().toString())
                + " " + activeOnt.getOntologyID().getOntologyIRI().get() + "\n";
        for (OWLOntology ont : eKit.getModelManager().getActiveOntologies()){
            if (!ont.equals(activeOnt)){
                nsText += nsUtil.getPrefix(activeOnt.getOntologyID()
                        .getOntologyIRI().get().toString())
                        + " "
                        + ont.getOntologyID().getOntologyIRI().get()
                        + "\n";
            }
        }

        textPanel.setText(nsText);
    }


    protected void handleProcessAction() {
        if (eKit.getModelManager().getReasoner() instanceof NoOpReasoner){
            JOptionPane.showMessageDialog(eKit.getWorkspace(), "Please select a reasoner first");
        }
        else{
            process();
        }
    }

    private void process(){
        try {
            OWLOntologyManager mngr = eKit.getModelManager().getOWLOntologyManager();

            BufferedReader in = new BufferedReader(new StringReader(textPanel.getText()));
            OPLReader r = new OPLReader(in);

            Map<String, IRI> ns2uri = r.getNSMappings();

            // Parse the instructions
            OPLInstructionsProcessor processor = new OPLInstructionsProcessor(ns2uri, mngr);
            processor.processOPLInstructions(r.getInstructions());
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }
}
