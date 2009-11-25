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
package uk.ac.manchester.cs.lintroll.ui.preference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import org.coode.oppl.lint.protege.ProtegeOPPLLintFactory;
import org.coode.oppl.lint.syntax.OPPLLintParser;
import org.coode.oppl.protege.ui.OPPLLintListCellRederer;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.protege.ProtegeLintManager;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.lintroll.ui.JarListRenderer;
import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

/**
 * Preference panel for the Lint Roll
 * 
 * @author Luigi Iannone
 * 
 */
public class LintRollPreferencesPanel extends OWLPreferencesPanel implements
		LintRollPreferenceChangeListener {
	private class OntologyOPPLLintList extends OWLFrameList2<OWLOntology> {
		/**
		 *
		 */
		private static final long serialVersionUID = -3942212059423895646L;

		public OntologyOPPLLintList() {
			super(LintRollPreferencesPanel.this.getOWLEditorKit(),
					new OPPLLintFrame(LintRollPreferencesPanel.this
							.getOWLEditorKit()));
			this.setCellRenderer(new OPPLLintListCellRederer());
			this.setRootObject(LintRollPreferencesPanel.this.getOWLEditorKit()
					.getOWLModelManager().getActiveOntology());
		}

		@Override
		protected Border createListItemBorder(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Border border = super.createListItemBorder(list, value, index,
					isSelected, cellHasFocus);
			return BorderFactory.createCompoundBorder(border,
					new OPPLLintBorder());
		}
	}

	private static class OPPLLintBorder implements Border {
		public Insets getBorderInsets(Component c) {
			return new Insets(0, c.getFontMetrics(c.getFont()).getStringBounds(
					"OPPL", c.getGraphics()).getBounds().width + 1, 0, 0);
		}

		public boolean isBorderOpaque() {
			return false;
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			Color oldColor = g.getColor();
			g.setColor(Color.RED);
			g.drawString("OPPL", x + 4, y + g.getFontMetrics().getAscent()
					+ g.getFontMetrics().getLeading());
			g.setColor(oldColor);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 4639367272089721763L;
	private Map<JCheckBox, Lint> map = new HashMap<JCheckBox, Lint>();
	private Box box = new Box(BoxLayout.Y_AXIS);
	private JButton selectAllButton = new JButton("Select all");
	private JButton deSelectAllButton = new JButton("De-select all");
	private JButton invertSelection = new JButton("Invert selection");
	private JarList jarList;
	private JPanel holder;
	private JScrollPane boxPane;
	private OWLFrameList2<OWLOntology> ontologyOPPLLint;

	/**
	 * @see org.protege.editor.core.ui.preferences.PreferencesPanel#applyChanges()
	 */
	@Override
	public void applyChanges() {
		Set<Lint> selectedLints = LintRollPreferences.getSelectedLints();
		for (JCheckBox cb : this.map.keySet()) {
			if (cb.isSelected()) {
				selectedLints.add(this.map.get(cb));
			} else {
				selectedLints.remove(this.map.get(cb));
			}
		}
		LintRollPreferences.clearSelected();
		LintRollPreferences.addAllSelected(selectedLints);
		LintRollPreferences.savePreferences();
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#dispose()
	 */
	public void dispose() throws Exception {
		if (this.ontologyOPPLLint != null) {
			this.ontologyOPPLLint.dispose();
		}
		LintRollPreferences.stopListening();
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#initialise()
	 */
	public void initialise() throws Exception {
		// The following line must be the first thing or at least must appear
		// before the setOntologyManager method
		LintManagerFactory.setPreferredLintManager(new ProtegeLintManager(this
				.getOWLModelManager()));
		OPPLLintParser.setOPPLLintAbstractFactory(new ProtegeOPPLLintFactory(
				this.getOWLModelManager()));
		LintRollPreferences.setOWLOntologyManager(this.getOWLEditorKit()
				.getModelManager().getOWLOntologyManager());
		LintRollPreferences.startListenting();
		this.setLayout(new BorderLayout());
		Set<Lint> loadedLints = LintRollPreferences.getLoadedLints();
		Set<Lint> selectedLints = LintRollPreferences.getSelectedLints();
		JToolBar toolBar = new JToolBar();
		// toolBar.add(this.openJarButton);
		toolBar.add(this.selectAllButton);
		toolBar.add(this.deSelectAllButton);
		toolBar.add(this.invertSelection);
		toolBar.setFloatable(false);
		this.initButtons();
		for (Lint lint : loadedLints) {
			JCheckBox checkBox = new JCheckBox(lint.getName(), selectedLints
					.contains(lint));
			this.map.put(checkBox, lint);
			this.box.add(checkBox);
			this.box.add(Box.createVerticalStrut(4));
			checkBox.setOpaque(false);
		}
		this.jarList = new JarList(this.getOWLEditorKit());
		this.jarList.setCellRenderer(new JarListRenderer());
		for (String jarName : LintRollPreferences.getLoadedJars()) {
			JarLListItem item = new JarLListItem(jarName);
			((DefaultListModel) this.jarList.getModel()).addElement(item);
		}
		this.holder = new JPanel(new BorderLayout());
		this.holder.setBorder(ComponentFactory
				.createTitledBorder("Loaded Lints"));
		this.boxPane = new JScrollPane(this.box);
		this.holder.add(toolBar, BorderLayout.NORTH);
		this.holder.add(this.boxPane, BorderLayout.CENTER);
		JButton resetAllButton = new JButton("Reset Default values");
		resetAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LintRollPreferences.resetAll();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(resetAllButton);
		this.holder.add(buttonPanel, BorderLayout.EAST);
		this.add(this.holder, BorderLayout.CENTER);
		JPanel additionPanel = new JPanel(new GridLayout(0, 3));
		JScrollPane jarListPane = ComponentFactory
				.createScrollPane(this.jarList);
		JPanel jarListBorderPanel = new JPanel(new BorderLayout());
		jarListBorderPanel.add(jarListPane);
		additionPanel.add(jarListBorderPanel);
		this.ontologyOPPLLint = new OntologyOPPLLintList();
		additionPanel.add(ComponentFactory
				.createScrollPane(this.ontologyOPPLLint));
		this.add(additionPanel, BorderLayout.SOUTH);
		LintRollPreferences.addLintRollPreferenceChangeListener(this);
		this.setPreferredSize(new Dimension(800, 500));
	}

	private void initButtons() {
		this.selectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (JCheckBox cb : LintRollPreferencesPanel.this.map.keySet()) {
					cb.setSelected(true);
				}
			}
		});
		this.deSelectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JCheckBox cb : LintRollPreferencesPanel.this.map.keySet()) {
					cb.setSelected(false);
				}
			}
		});
		this.invertSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (JCheckBox cb : LintRollPreferencesPanel.this.map.keySet()) {
					cb.setSelected(!cb.isSelected());
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void handleChange(LintRollPreferenceChangeEvent e) {
		if (e.getType().equals(EventType.LOADED_LINT_CHANGE)) {
			Set<Lint> loadedLints = (Set<Lint>) e.getSource();
			Set<String> previouslySeectedLintNames = new HashSet<String>(
					this.map.keySet().size());
			for (JCheckBox c : this.map.keySet()) {
				Lint lint = this.map.get(c);
				if (c.isSelected()) {
					previouslySeectedLintNames.add(lint.getName());
				}
			}
			this.map.clear();
			this.holder.remove(this.boxPane);
			this.box = new Box(BoxLayout.Y_AXIS);
			for (Lint lint : loadedLints) {
				JCheckBox checkBox = new JCheckBox(lint.getName(),
						previouslySeectedLintNames.contains(lint.getName()));
				this.map.put(checkBox, lint);
				this.box.add(checkBox);
				this.box.add(Box.createVerticalStrut(4));
				checkBox.setOpaque(false);
			}
			this.boxPane = new JScrollPane(this.box);
			this.holder.add(this.boxPane);
			this.holder.revalidate();
		}
	}
}
