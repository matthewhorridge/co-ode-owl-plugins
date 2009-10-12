package org.coode.oae.ui;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextArea;

import org.protege.editor.core.ui.util.ComponentFactory;

public class ErrorReport extends JTextArea {
	private static final long serialVersionUID = -3526147243215403368L;
	private final Set<String> model = new HashSet<String>();

	public ErrorReport() {
		setBorder(ComponentFactory.createTitledBorder("Error report"));
		setAutoscrolls(true);
		setLineWrap(true);
		setText("");
	}

	public void addReport(String s) {
		String toAdd = s.replace("uk.ac.manchester.mae.ParseException: ", "");
		if (toAdd.contains("Encountered \";\" at line 1, column 1.")) {
			// the formula is still empty, ignore it
			toAdd = "";
		}
		toAdd = toAdd.replace("\n", " ");
		if (toAdd.length() > 0) {
			this.model.add(toAdd);
		}
		setText("");
		for (String st : this.model) {
			append(st);
			append("\n");
		}
	}

	public void clearReport() {
		this.model.clear();
		setText("");
	}
}
