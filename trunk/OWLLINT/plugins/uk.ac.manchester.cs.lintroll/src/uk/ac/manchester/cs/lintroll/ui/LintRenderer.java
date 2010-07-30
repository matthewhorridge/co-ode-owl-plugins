/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.lint.ActingLint;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class LintRenderer extends OWLCellRenderer implements TreeCellRenderer,
		ListCellRenderer {
	private OWLCellRenderer owlCellRenderer;
	private DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
	private DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
	public static final Color SELECTION_BACKGROUND = UIManager.getDefaults()
			.getColor("List.selectionBackground");
	public static final Color SELECTION_FOREGROUND = UIManager.getDefaults()
			.getColor("List.selectionForeground");

	public LintRenderer(OWLEditorKit owlEditorKit) {
		super(owlEditorKit);
		this.owlCellRenderer = new OWLCellRenderer(owlEditorKit);
		this.owlCellRenderer.setHighlightKeywords(true);
		this.owlCellRenderer.setWrap(true);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component toReturn;
		DefaultMutableTreeNode valueNode = (DefaultMutableTreeNode) value;
		if (valueNode.isRoot()) {
			URL url = this.getClass().getClassLoader().getResource(
					"lintroll.jpg");
			ImageIcon newIcon = new ImageIcon(url);
			if (valueNode.isLeaf()) {
				this.defaultTreeCellRenderer.setLeafIcon(newIcon);
				toReturn = this.defaultTreeCellRenderer
						.getTreeCellRendererComponent(tree, value, selected,
								expanded, leaf, row, hasFocus);
				this.defaultTreeCellRenderer
						.setLeafIcon(this.defaultTreeCellRenderer
								.getDefaultLeafIcon());
			} else {
				this.defaultTreeCellRenderer.setOpenIcon(newIcon);
				this.defaultTreeCellRenderer.setClosedIcon(newIcon);
				toReturn = this.defaultTreeCellRenderer
						.getTreeCellRendererComponent(tree, "", selected,
								expanded, leaf, row, hasFocus);
				this.defaultTreeCellRenderer
						.setOpenIcon(this.defaultTreeCellRenderer
								.getDefaultOpenIcon());
				this.defaultTreeCellRenderer
						.setClosedIcon(this.defaultTreeCellRenderer
								.getDefaultClosedIcon());
			}
		} else {
			Object nodeUserObject = valueNode.getUserObject();
			if (nodeUserObject instanceof OWLObject) {
				toReturn = this.owlCellRenderer.getTreeCellRendererComponent(
						tree, nodeUserObject, selected, expanded, leaf, row,
						hasFocus);
			} else if (nodeUserObject instanceof LintReport<?>) {
				toReturn = new JPanel(new BorderLayout());
				JTextPane textPane = new JTextPane();
				textPane.setOpaque(false);
				((JPanel) toReturn).add(textPane, BorderLayout.CENTER);
				LintReport<?> report = (LintReport<?>) nodeUserObject;
				Lint<?> lint = report.getLint();
				if (lint instanceof ActingLint<?>) {
					URL url = this.getClass().getClassLoader().getResource(
							"hammer.jpg");
					ImageIcon icon = new ImageIcon(url);
					((JPanel) toReturn)
							.add(new JLabel(icon), BorderLayout.EAST);
				}
				textPane.setText(lint.getName() + "{"
						+ report.getAffectedOntologies().size() + "}");
				this.render(tree, toReturn, selected);
			} else {
				toReturn = this.defaultTreeCellRenderer
						.getTreeCellRendererComponent(tree, value, selected,
								expanded, leaf, row, hasFocus);
			}
		}
		return toReturn;
	}

	private void render(Component renderingComponent, Component toReturn,
			boolean selected) {
		if (selected) {
			toReturn.setBackground(SELECTION_BACKGROUND);
			toReturn.setForeground(SELECTION_FOREGROUND);
		} else {
			toReturn.setBackground(renderingComponent.getBackground());
			toReturn.setForeground(renderingComponent.getForeground());
		}
		renderingComponent.validate();
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component toReturn;
		if (value instanceof Lint<?>) {
			toReturn = new JTextPane();
			((JTextPane) toReturn).setText(((Lint<?>) value).getName());
		} else if (value instanceof LintPattern<?>) {
			toReturn = new JTextPane();
			((JTextPane) toReturn).setText(((LintPattern<?>) value).getClass()
					.getSimpleName());
		} else {
			toReturn = this.defaultListCellRenderer
					.getListCellRendererComponent(list, value, index,
							isSelected, cellHasFocus);
		}
		this.render(list, toReturn, isSelected);
		return toReturn;
	}
}
