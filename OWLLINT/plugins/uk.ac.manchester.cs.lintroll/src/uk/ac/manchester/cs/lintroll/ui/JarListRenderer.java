/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import uk.ac.manchester.cs.lintroll.ui.preference.JarLListItem;
import uk.ac.manchester.cs.lintroll.ui.preference.LintRollPreferences;

/**
 * @author Luigi Iannone
 * 
 */
public class JarListRenderer implements ListCellRenderer {
	private DefaultListCellRenderer renderer = new DefaultListCellRenderer();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component toReturn = this.renderer.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		if (value instanceof JarLListItem
				&& LintRollPreferences.isInvalid(((JarLListItem) value)
						.getJarName())) {
			toReturn.setForeground(Color.RED);
		}
		return toReturn;
	}
}
