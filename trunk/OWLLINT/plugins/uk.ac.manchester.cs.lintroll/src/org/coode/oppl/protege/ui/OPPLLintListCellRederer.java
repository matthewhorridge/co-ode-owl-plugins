/**
 * 
 */
package org.coode.oppl.protege.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;

import org.coode.oppl.lint.OPPLLintScript;

import uk.ac.manchester.cs.lintroll.ui.preference.OPPLLintFrameSectionRow;

/**
 * @author Luigi Iannone
 * 
 */
public class OPPLLintListCellRederer implements ListCellRenderer {
	private DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();

	/**
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component toReturn = this.defaultListCellRenderer
				.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
		if (OPPLLintFrameSectionRow.class.isAssignableFrom(value.getClass())) {
			JPanel panel = new JPanel();
			OPPLLintFrameSectionRow row = (OPPLLintFrameSectionRow) value;
			OPPLLintScript lint = row.getLint();
			JTextPane textPane = new JTextPane();
			textPane.setOpaque(false);
			textPane.setText(lint.getName());
			panel.add(textPane);
			panel.setOpaque(false);
			panel.revalidate();
			toReturn = panel;
		} else if (OPPLLintMListItem.class.isAssignableFrom(value.getClass())) {
			JPanel panel = new JPanel();
			OPPLLintMListItem row = (OPPLLintMListItem) value;
			OPPLLintScript lint = row.getLint();
			JTextPane textPane = new JTextPane();
			textPane.setOpaque(false);
			textPane.setText(lint.getName());
			panel.add(textPane);
			panel.setOpaque(false);
			panel.revalidate();
			toReturn = panel;
		}
		return toReturn;
	}
}
