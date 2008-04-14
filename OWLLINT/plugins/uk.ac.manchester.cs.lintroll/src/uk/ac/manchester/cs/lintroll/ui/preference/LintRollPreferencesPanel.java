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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.preferences.PreferencesPanel;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owl.lint.Lint;

/**
 * Preference panel for the Lint Roll
 * 
 * @author Luigi Iannone
 * 
 */
public class LintRollPreferencesPanel extends PreferencesPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4639367272089721763L;
	private Map<JCheckBox, Lint> map = new HashMap<JCheckBox, Lint>();
	private Box box = new Box(BoxLayout.Y_AXIS);

	/**
	 * @see org.protege.editor.core.ui.preferences.PreferencesPanel#applyChanges()
	 */
	@Override
	public void applyChanges() {
		for (JCheckBox cb : this.map.keySet()) {
			LintRollPreferences.getLoadedLints().add(this.map.get(cb));
			if (cb.isSelected()) {
				LintRollPreferences.getSelectedLints().add(this.map.get(cb));
			}
		}
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
		this.setLayout(new BorderLayout());
		Set<Lint> loadedLints = LintRollPreferences.getLoadedLints();
		Set<Lint> selectedLints = LintRollPreferences.getSelectedLints();
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
}
