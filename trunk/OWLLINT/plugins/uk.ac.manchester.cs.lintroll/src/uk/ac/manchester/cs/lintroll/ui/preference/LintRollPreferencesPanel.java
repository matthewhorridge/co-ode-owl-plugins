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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.protege.ProtegeLintManager;

import uk.ac.manchester.cs.lintroll.utils.JarFileFilter;
import uk.ac.manchester.cs.owl.lint.LintManagerFactory;

/**
 * Preference panel for the Lint Roll
 * 
 * @author Luigi Iannone
 * 
 */
public class LintRollPreferencesPanel extends OWLPreferencesPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4639367272089721763L;
	private Map<JCheckBox, Lint> map = new HashMap<JCheckBox, Lint>();
	private Box box = new Box(BoxLayout.Y_AXIS);
	private JButton openJarButton = new JButton(new DefaultTreeCellRenderer()
			.getDefaultOpenIcon());
	private JButton selectAllButton = new JButton("Select all");
	private JButton deSelectAllButton = new JButton("De-select all");
	private JButton invertSelection = new JButton("Invert selection");
	static {
	}

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
	}

	/**
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#dispose()
	 */
	public void dispose() throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#initialise()
	 */
	public void initialise() throws Exception {
		LintRollPreferences.setOWLOntologyManager(this.getOWLEditorKit()
				.getOWLModelManager().getOWLOntologyManager());
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		LintManagerFactory.setPreferredLintManager(new ProtegeLintManager(this
				.getOWLModelManager()));
		Set<Lint> loadedLints = LintRollPreferences.getLoadedLints();
		Set<Lint> selectedLints = LintRollPreferences.getSelectedLints();
		JToolBar toolBar = new JToolBar();
		toolBar.add(this.openJarButton);
		toolBar.add(this.selectAllButton);
		toolBar.add(this.deSelectAllButton);
		toolBar.add(this.invertSelection);
		this.initButtons();
		this.add(toolBar);
		for (Lint lint : loadedLints) {
			JCheckBox checkBox = new JCheckBox(lint.getName(), selectedLints
					.contains(lint));
			this.map.put(checkBox, lint);
			this.box.add(checkBox);
			this.box.add(Box.createVerticalStrut(4));
			checkBox.setOpaque(false);
		}
		JPanel holder = new JPanel(new BorderLayout());
		holder.setBorder(ComponentFactory.createTitledBorder("Loaded Lints"));
		holder.add(new JScrollPane(this.box));
		this.add(holder);
	}

	private void initButtons() {
		this.openJarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooseAJar = new JFileChooser(System
						.getProperty("user.dir"));
				chooseAJar.setFileFilter(new JarFileFilter());
				chooseAJar.showOpenDialog(LintRollPreferencesPanel.this);
				File selectedJar = chooseAJar.getSelectedFile();
				if (selectedJar != null) {
					Set<Lint> lints = LintRollPreferences.loadLints(selectedJar
							.getAbsolutePath());
					for (Lint lint : lints) {
						if (!LintRollPreferences.getLoadedLints()
								.contains(lint)) {
							LintRollPreferences.addLoadedLint(lint);
							JCheckBox cb = new JCheckBox(lint.getName(), false);
							LintRollPreferencesPanel.this.box.add(cb);
							LintRollPreferencesPanel.this.box.add(Box
									.createVerticalStrut(4));
							cb.setOpaque(false);
							LintRollPreferencesPanel.this.map.put(cb, lint);
						}
					}
				}
			}
		});
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
}
