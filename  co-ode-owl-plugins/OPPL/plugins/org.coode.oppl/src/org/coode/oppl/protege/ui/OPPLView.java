/**
 * Copyright (C) 2008, University of Manchester
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
package org.coode.oppl.protege.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.coode.oppl.ChangeExtractor;
import org.coode.oppl.protege.model.OPPLStatementModelChangeListener;
import org.coode.oppl.protege.model.ProtegeOPPLStatementModel;
import org.coode.oppl.syntax.OPPLParser;
import org.coode.oppl.variablemansyntax.ConstraintSystem;
import org.coode.oppl.variablemansyntax.VariableManchesterSyntaxExpressionChecker;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLView extends AbstractOWLViewComponent implements
		OPPLStatementModelChangeListener,
		InputVerificationStatusChangedListener, OWLOntologyChangeListener,
		OWLModelManagerListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1897093057453176659L;
	private ExpressionEditor<ProtegeOPPLStatementModel> opplStatementExpressionEditor;
	private JButton evaluate = new JButton("Evaluate");
	private JButton execute = new JButton("Execute");
	private ProtegeOPPLStatementModel statementModel;
	private ActionList affectedAxioms;
	private MList instantiatedAxiomsList;

	public void handleChange() {
		this.evaluate.setEnabled(this.statementModel.isValid());
	}

	@Override
	protected void disposeOWLView() {
		this.getOWLEditorKit().getModelManager().removeListener(this);
		this.opplStatementExpressionEditor.removeStatusChangedListener(this);
	}

	@Override
	protected void initialiseOWLView() throws Exception {
		this.setLayout(new BorderLayout());
		JPanel statementPanel = new JPanel(new BorderLayout());
		this.affectedAxioms = new ActionList(this.getOWLEditorKit(),
				new ConstraintSystem(this.getOWLEditorKit().getModelManager()
						.getActiveOntology(), this.getOWLEditorKit()
						.getModelManager().getOWLOntologyManager()));
		this.instantiatedAxiomsList = new MList();
		OWLCellRenderer cellRenderer = new OWLCellRenderer(this
				.getOWLEditorKit());
		cellRenderer.setWrap(true);
		cellRenderer.setHighlightKeywords(true);
		this.instantiatedAxiomsList.setCellRenderer(cellRenderer);
		this.getOWLEditorKit().getModelManager().addListener(this);
		this.getOWLModelManager().getOWLOntologyManager()
				.addOntologyChangeListener(this);
		this.opplStatementExpressionEditor = new ExpressionEditor<ProtegeOPPLStatementModel>(
				this.getOWLEditorKit(),
				new VariableManchesterSyntaxExpressionChecker(this
						.getOWLEditorKit()));
		this.opplStatementExpressionEditor.setPreferredSize(new Dimension(50,
				200));
		this.removeKeyListeners();
		this.opplStatementExpressionEditor.addStatusChangedListener(this);
		new OPPLCompleter(this.getOWLEditorKit(),
				this.opplStatementExpressionEditor,
				this.opplStatementExpressionEditor.getExpressionChecker());
		statementPanel.add(new JScrollPane(this.opplStatementExpressionEditor),
				BorderLayout.NORTH);
		statementPanel.add(this.evaluate, BorderLayout.SOUTH);
		this.add(statementPanel, BorderLayout.NORTH);
		// Effects GUI portion
		JSplitPane effects = new JSplitPane();
		JScrollPane affectedScrollPane = ComponentFactory
				.createScrollPane(this.affectedAxioms);
		JScrollPane instantiatedScrollPane = ComponentFactory
				.createScrollPane(this.instantiatedAxiomsList);
		instantiatedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Instantiated axioms: "));
		affectedScrollPane.setBorder(ComponentFactory
				.createTitledBorder("Affected axioms:"));
		effects.add(affectedScrollPane, JSplitPane.LEFT);
		effects.add(instantiatedScrollPane, JSplitPane.RIGHT);
		this.add(effects, BorderLayout.CENTER);
		this.add(this.execute, BorderLayout.SOUTH);
		this.statementModel = new ProtegeOPPLStatementModel(this
				.getOWLEditorKit().getModelManager());
		this.statementModel.addChangeListener(this);
		this.evaluate.setEnabled(false);
		this.execute.setEnabled(false);
		this.evaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OPPLView.this.updateEffects();
				OPPLView.this.evaluate.setEnabled(false);
			}
		});
		this.affectedAxioms.getModel().addListDataListener(
				new ListDataListener() {
					public void contentsChanged(ListDataEvent e) {
						OPPLView.this.execute
								.setEnabled(OPPLView.this.affectedAxioms
										.getModel().getSize() > 0);
					}

					public void intervalAdded(ListDataEvent e) {
						OPPLView.this.execute
								.setEnabled(OPPLView.this.affectedAxioms
										.getModel().getSize() > 0);
					}

					public void intervalRemoved(ListDataEvent e) {
						OPPLView.this.execute
								.setEnabled(OPPLView.this.affectedAxioms
										.getModel().getSize() > 0);
					}
				});
		this.execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
						.getModel();
				List<OWLAxiomChange> changes = model.getOWLAxiomChanges();
				OPPLView.this.getOWLEditorKit().getModelManager().applyChanges(
						changes);
				model.clear();
			}
		});
	}

	public void verifiedStatusChanged(boolean newState) {
		this.evaluate.setEnabled(newState);
		ListModel model = this.affectedAxioms.getModel();
		((DefaultListModel) model).clear();
		if (newState) {
			this.instantiatedAxiomsList.setModel(new DefaultListModel());
			this.statementModel
					.setOPPLStatement(this.opplStatementExpressionEditor
							.getText());
			ConstraintSystem constraintSystem = this.statementModel
					.getOpplStatement().getConstraintSystem();
			ListCellRenderer cellRenderer = this.affectedAxioms
					.getCellRenderer();
			if (cellRenderer instanceof VariableAxiomRenderer) {
				((VariableAxiomRenderer) cellRenderer)
						.setConstraintSystem(constraintSystem);
			}
		}
	}

	public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
			throws OWLException {
		this.updateEffects();
		ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
				.getModel();
		model.clear();
	}

	/**
	 * 
	 */
	private void updateEffects() {
		ChangeExtractor changeExtractor = new ChangeExtractor(OPPLView.this
				.getOWLEditorKit().getModelManager().getActiveOntology(), this
				.getOWLEditorKit().getModelManager().getOWLOntologyManager());
		if (this.statementModel != null
				&& this.statementModel.getOpplStatement() != null) {
			this.statementModel.getOpplStatement().jjtAccept(changeExtractor,
					null);
			List<OWLAxiomChange> changes = changeExtractor.getChanges();
			ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
					.getModel();
			model.clear();
			for (OWLAxiomChange axiomChange : changes) {
				model.addAction(axiomChange);
			}
			this.revalidate();
			System.out.println("Size: "
					+ this.affectedAxioms.getModel().getSize());
			this.updateInstantiatedAxioms();
		}
	}

	private void updateInstantiatedAxioms() {
		if (this.statementModel != null
				&& this.statementModel.getOpplStatement() != null) {
			Set<OWLAxiom> instantiatedAxioms = this.statementModel
					.getOpplStatement().getConstraintSystem()
					.getInstantiatedAxioms();
			DefaultListModel model = new DefaultListModel();
			for (OWLAxiom axiom : instantiatedAxioms) {
				model.addElement(axiom);
			}
			this.instantiatedAxiomsList.setModel(model);
			this.revalidate();
		}
	}

	public void handleChange(OWLModelManagerChangeEvent event) {
		this.statementModel.setOPPLStatement(this.opplStatementExpressionEditor
				.getText());
		if (event.getType().equals(EventType.REASONER_CHANGED)
				&& this.statementModel.isValid()) {
			OPPLParser.setReasoner(this.getOWLEditorKit().getModelManager()
					.getReasoner());
			ActionListModel model = (ActionListModel) OPPLView.this.affectedAxioms
					.getModel();
			model.clear();
			this.evaluate.setEnabled(this.statementModel.isValid());
		}
	}

	/**
	 * 
	 */
	private void removeKeyListeners() {
		KeyListener[] keyListeners = this.opplStatementExpressionEditor
				.getKeyListeners();
		for (KeyListener keyListener : keyListeners) {
			this.opplStatementExpressionEditor.removeKeyListener(keyListener);
		}
	}
}
