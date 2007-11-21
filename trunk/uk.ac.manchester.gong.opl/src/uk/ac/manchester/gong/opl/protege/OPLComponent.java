package uk.ac.manchester.gong.opl.protege;

import uk.ac.manchester.gong.opl.ReasonerFactory;
import uk.ac.manchester.gong.opl.OPLInstructionsProcessor;
import uk.ac.manchester.gong.opl.io.OPLReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

import org.protege.editor.owl.model.inference.NoOpReasoner;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.core.ui.view.DisposableAction;
import org.semanticweb.owl.util.NamespaceUtil;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
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
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 9, 2007<br><br>
 */
public class OPLComponent extends JComponent {

    private JTextArea textPanel;
    private OWLEditorKit eKit;

    private DisposableAction processAction = new DisposableAction("Process", null){
        public void actionPerformed(ActionEvent actionEvent) {
            handleProcessAction();
        }
        public void dispose() {
        }
    };

    public OPLComponent(OWLEditorKit eKit) {
        setLayout(new BorderLayout());

        this.eKit = eKit;

        textPanel = new JTextArea();
        add(new JScrollPane(textPanel), BorderLayout.CENTER);

        init();

        ReasonerFactory.setOWLModelManager(eKit.getOWLModelManager());

    }

    public DisposableAction getRunAction(){
        return processAction;
    }

    private void init() {

        NamespaceUtil nsUtil = new NamespaceUtil();

        OWLOntology activeOnt = eKit.getOWLModelManager().getActiveOntology();
        String nsText = nsUtil.generatePrefix(activeOnt.getURI().toString()) + " " + activeOnt.getURI() + "\n";
        for (OWLOntology ont : eKit.getOWLModelManager().getActiveOntologies()){
            if (!ont.equals(activeOnt)){
                nsText += nsUtil.generatePrefix(activeOnt.getURI().toString()) + " " + ont.getURI() + "\n";
            }
        }

        textPanel.setText(nsText);
    }


    private void handleProcessAction() {
        if (eKit.getOWLModelManager().getReasoner() instanceof NoOpReasoner){
            JOptionPane.showMessageDialog(eKit.getOWLWorkspace(), "Please select a reasoner first");
        }
        else{
            process();
        }
    }

    private void process(){
        try {
            OWLOntologyManager mngr = eKit.getOWLModelManager().getOWLOntologyManager();

            BufferedReader in = new BufferedReader(new StringReader(textPanel.getText()));
            OPLReader r = new OPLReader(in);

            Map<String, URI> ns2uri = r.getNSMappings();

            // Parse the instructions
            OPLInstructionsProcessor processor = new OPLInstructionsProcessor(ns2uri, mngr);
            processor.processOPLInstructions(r.getInstructions());
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }
}
