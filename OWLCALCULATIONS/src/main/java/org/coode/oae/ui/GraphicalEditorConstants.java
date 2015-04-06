package org.coode.oae.ui;

import java.awt.Dimension;

import javax.swing.ImageIcon;

import org.protege.editor.owl.ui.OWLIcons;

public class GraphicalEditorConstants {
	static final ImageIcon addIcon = new ImageIcon(
			GraphicalEditorConstants.class.getClassLoader().getResource(
					"add.png"));
	static final ImageIcon delIcon = new ImageIcon(
			GraphicalEditorConstants.class.getClassLoader().getResource(
					"delete.png"));
	static final ImageIcon objectPropertyIcon = (ImageIcon) OWLIcons
			.getIcon("property.object.png");
	static final ImageIcon dataPropertyIcon = (ImageIcon) OWLIcons
			.getIcon("property.data.png");
	static Dimension LONG_FIELD_DIMENSION = new Dimension(250, 25);
	public static final Dimension LIST_PREFERRED_SIZE = new Dimension(250, 200);
	public static final Dimension REPORT_SIZE = new Dimension(450, 150);
}
