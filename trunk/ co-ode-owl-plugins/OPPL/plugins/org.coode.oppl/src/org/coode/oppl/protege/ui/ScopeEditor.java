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
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.coode.oppl.variablemansyntax.VariableScope;
import org.coode.oppl.variablemansyntax.VariableScopes;
import org.coode.oppl.variablemansyntax.VariableType;
import org.coode.oppl.variablemansyntax.VariableScopes.Direction;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionChecker;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeCellRenderer;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLProperty;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class ScopeEditor extends JPanel implements VerifiedInputEditor {
	static class ClassScopeEditor extends ScopeEditor implements
			InputVerificationStatusChangedListener, ChangeListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1748983757452740791L;
		private ExpressionEditor<OWLDescription> editor = new ExpressionEditor<OWLDescription>(
				this.owlEditorKit, new OWLDescriptionChecker(this.owlEditorKit
						.getModelManager()));
		private ButtonGroup directionButtonGroup = new ButtonGroup();
		private Map<JRadioButton, Direction> radioButtonDirectionMap = new HashMap<JRadioButton, Direction>();
		private static final String CLASS_TITLE = "Class Variable Scope";

		public ClassScopeEditor(OWLEditorKit owlEditorKit) {
			super(CLASS_TITLE, owlEditorKit);
			this.setLayout(new BorderLayout());
			JRadioButton superClassRadioButton = new JRadioButton(
					Direction.SUPERCLASSOF.toString());
			this.radioButtonDirectionMap.put(superClassRadioButton,
					Direction.SUPERCLASSOF);
			this.directionButtonGroup.add(superClassRadioButton);
			superClassRadioButton.getModel().addChangeListener(this);
			JRadioButton subClassRadioButton = new JRadioButton(
					Direction.SUBCLASSOF.toString());
			this.radioButtonDirectionMap.put(subClassRadioButton,
					Direction.SUBCLASSOF);
			this.directionButtonGroup.add(subClassRadioButton);
			JPanel directionPanel = new JPanel(new GridLayout(0, 2));
			directionPanel.setBorder(ComponentFactory
					.createTitledBorder("Direction"));
			directionPanel.add(superClassRadioButton);
			directionPanel.add(subClassRadioButton);
			this.editor.addStatusChangedListener(this);
			this.editor.setSize(new Dimension(100, 50));
			this.add(directionPanel, BorderLayout.NORTH);
			JScrollPane editorPane = ComponentFactory
					.createScrollPane(this.editor);
			editorPane.setBorder(ComponentFactory
					.createTitledBorder("Scoping Class Description"));
			this.add(editorPane, BorderLayout.CENTER);
		}

		private JRadioButton findSelectedButton() {
			JRadioButton button = null;
			Enumeration<AbstractButton> directions = this.directionButtonGroup
					.getElements();
			boolean found = false;
			while (!found && directions.hasMoreElements()) {
				button = (JRadioButton) directions.nextElement();
				found = button.isSelected();
			}
			return found ? button : null;
		}

		public void verifiedStatusChanged(boolean newState) {
			this.setVariableScope(null);
			if (newState) {
				JRadioButton button = this.findSelectedButton();
				if (button != null) {
					Direction direction = this.radioButtonDirectionMap
							.get(button);
					try {
						switch (direction) {
						case SUPERCLASSOF:
							this.setVariableScope(VariableScopes
									.buildSuperClassVariableScope(this.editor
											.createObject()));
							break;
						case SUBCLASSOF:
							this.setVariableScope(VariableScopes
									.buildSubClassVariableScope(this.editor
											.createObject()));
						default:
							break;
						}
					} catch (OWLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		public void stateChanged(ChangeEvent e) {
			JRadioButton selectedButton = this.findSelectedButton();
			if (selectedButton != null) {
				Direction direction = this.radioButtonDirectionMap
						.get(selectedButton);
				try {
					OWLDescription description = this.editor.createObject();
					switch (direction) {
					case SUPERCLASSOF:
						this.setVariableScope(VariableScopes
								.buildSuperClassVariableScope(description));
						break;
					case SUBCLASSOF:
						this.setVariableScope(VariableScopes
								.buildSubClassVariableScope(description));
					default:
						break;
					}
				} catch (OWLException owlException) {
					this.setVariableScope(null);
				}
			}
		}
	}

	static class IndividualScopeEditor extends ScopeEditor implements
			InputVerificationStatusChangedListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1748983757452740791L;
		private ExpressionEditor<OWLDescription> editor = new ExpressionEditor<OWLDescription>(
				this.owlEditorKit, new OWLDescriptionChecker(this.owlEditorKit
						.getModelManager()));
		private static final String INDIVIDUAL_TITLE = "Individual Variable Scope";

		public IndividualScopeEditor(OWLEditorKit owlEditorKit) {
			super(INDIVIDUAL_TITLE, owlEditorKit);
			this.setLayout(new BorderLayout());
			this.editor.addStatusChangedListener(this);
			this.editor.setSize(new Dimension(100, 50));
			JScrollPane editorPane = ComponentFactory
					.createScrollPane(this.editor);
			editorPane.setBorder(ComponentFactory
					.createTitledBorder("Scoping Class Description"));
			this.add(editorPane, BorderLayout.CENTER);
		}

		public void verifiedStatusChanged(boolean newState) {
			this.setVariableScope(null);
			if (newState) {
				try {
					this.setVariableScope(VariableScopes
							.buildIndividualVariableScope(this.editor
									.createObject()));
				} catch (OWLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	static class PropertyScopeEditor extends ScopeEditor implements
			TreeSelectionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1014596361426722995L;
		private ButtonGroup directionButtonGroup = new ButtonGroup();
		private Map<JRadioButton, Direction> radioButtonDirectionMap = new HashMap<JRadioButton, Direction>();
		private final static String PROPERTY_TITLE = "Property Variable Scope";

		public PropertyScopeEditor(OWLEditorKit owlEditorKit,
				boolean isDataProperty) {
			super(PROPERTY_TITLE, owlEditorKit);
			this.setLayout(new BorderLayout());
			JRadioButton superClassRadioButton = new JRadioButton(
					Direction.SUBPROPERTYOF.toString());
			this.radioButtonDirectionMap.put(superClassRadioButton,
					Direction.SUBPROPERTYOF);
			this.directionButtonGroup.add(superClassRadioButton);
			JRadioButton subClassRadioButton = new JRadioButton(
					Direction.SUPERPROPERTYOF.toString());
			this.radioButtonDirectionMap.put(subClassRadioButton,
					Direction.SUPERPROPERTYOF);
			this.directionButtonGroup.add(subClassRadioButton);
			JPanel directionPanel = new JPanel(new GridLayout(0, 2));
			directionPanel.setBorder(ComponentFactory
					.createTitledBorder("Direction"));
			directionPanel.add(superClassRadioButton);
			directionPanel.add(subClassRadioButton);
			this.add(directionPanel, BorderLayout.NORTH);
			OWLObjectTree<?> propertyTree = isDataProperty ? new OWLObjectTree<OWLDataProperty>(
					owlEditorKit, owlEditorKit.getModelManager()
							.getOWLDataPropertyHierarchyProvider())
					: new OWLObjectTree<OWLObjectProperty>(owlEditorKit,
							owlEditorKit.getModelManager()
									.getOWLObjectPropertyHierarchyProvider());
			propertyTree.setCellRenderer(new OWLObjectTreeCellRenderer(
					owlEditorKit));
			propertyTree.addTreeSelectionListener(this);
			JScrollPane propertyPane = ComponentFactory
					.createScrollPane(propertyTree);
			propertyPane.setBorder(ComponentFactory
					.createTitledBorder("Scoping property"));
			propertyPane.setSize(new Dimension(100, 50));
			this.add(propertyPane, BorderLayout.SOUTH);
		}

		public void valueChanged(TreeSelectionEvent e) {
			OWLProperty<?, ?> lastPathComponent = (OWLProperty<?, ?>) ((OWLObjectTreeNode<?>) e
					.getPath().getLastPathComponent()).getOWLObject();
			JRadioButton selectedButton = this.findSelectedButton();
			if (selectedButton != null) {
				Direction direction = this.radioButtonDirectionMap
						.get(selectedButton);
				this.setVariableScope(null);
				if (lastPathComponent != null && direction != null) {
					switch (direction) {
					case SUBPROPERTYOF:
						if (lastPathComponent instanceof OWLDataProperty) {
							this
									.setVariableScope(VariableScopes
											.buildSubPropertyVariableScope((OWLDataProperty) lastPathComponent));
						} else if (lastPathComponent instanceof OWLObjectProperty) {
							this
									.setVariableScope(VariableScopes
											.buildSubPropertyVariableScope((OWLObjectProperty) lastPathComponent));
						}
						break;
					case SUPERPROPERTYOF:
						if (lastPathComponent instanceof OWLDataProperty) {
							this
									.setVariableScope(VariableScopes
											.buildSuperPropertyVariableScope((OWLDataProperty) lastPathComponent));
						} else if (lastPathComponent instanceof OWLObjectProperty) {
							this
									.setVariableScope(VariableScopes
											.buildSuperPropertyVariableScope((OWLObjectProperty) lastPathComponent));
						}
						break;
					default:
						break;
					}
				}
			}
		}

		private JRadioButton findSelectedButton() {
			JRadioButton button = null;
			Enumeration<AbstractButton> directions = this.directionButtonGroup
					.getElements();
			boolean found = false;
			while (!found && directions.hasMoreElements()) {
				button = (JRadioButton) directions.nextElement();
				found = button.isSelected();
			}
			return found ? button : null;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2459124639459085302L;
	protected final OWLEditorKit owlEditorKit;
	private List<InputVerificationStatusChangedListener> listeners = new ArrayList<InputVerificationStatusChangedListener>();
	private VariableScope variableScope = null;
	private final String title;

	private ScopeEditor(String title, OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		this.title = title;
	}

	/**
	 * @return the VariableScope set by this ScopeEditor. It can be
	 *         <code>null</code>
	 */
	public VariableScope getVariableScope() {
		return this.variableScope;
	}

	protected final void setVariableScope(VariableScope variableScope) {
		this.variableScope = variableScope;
		this.notifyListeners();
	}

	private void notifyListeners() {
		for (InputVerificationStatusChangedListener listener : this.listeners) {
			listener.verifiedStatusChanged(this.variableScope != null);
		}
	}

	public static ScopeEditor getTypeScopeEditor(VariableType variableType,
			OWLEditorKit owlEditorKit) {
		ScopeEditor toReturn = null;
		switch (variableType) {
		case CLASS:
			toReturn = new ClassScopeEditor(owlEditorKit);
			break;
		case DATAPROPERTY:
			toReturn = new PropertyScopeEditor(owlEditorKit, true);
			break;
		case OBJECTPROPERTY:
			toReturn = new PropertyScopeEditor(owlEditorKit, false);
			break;
		case INDIVIDUAL:
			toReturn = new IndividualScopeEditor(owlEditorKit);
			break;
		default:
			break;
		}
		return toReturn;
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
		this.notifyListeners();
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
}
