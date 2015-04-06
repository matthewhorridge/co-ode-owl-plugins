package org.coode.oae.ui.utils;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.protege.editor.core.ui.util.ComponentFactory;

public class StretchingPanelsFactory {
	public static JPanel getStretchyPanelWithBorder(JComponent component,
			String border) {
		JPanel toReturn = new JPanel(new BorderLayout());
		if (border != null) {
			toReturn.setBorder(ComponentFactory.createTitledBorder(border));
		}
		if (component != null) {
			toReturn.add(component, BorderLayout.CENTER);
		}
		return toReturn;
	}
}
