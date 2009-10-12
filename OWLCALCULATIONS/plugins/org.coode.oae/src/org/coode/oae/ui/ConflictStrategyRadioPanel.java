package org.coode.oae.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;

import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.ExceptionStrategy;
import uk.ac.manchester.mae.OverriddenStrategy;
import uk.ac.manchester.mae.OverridingStrategy;

public class ConflictStrategyRadioPanel extends JPanel implements
		VerifiedInputEditor, ActionListener {
	private static final long serialVersionUID = 7084921860012912371L;
	private ButtonGroup theGroup = new ButtonGroup();
	private Map<JRadioButton, ConflictStrategy> map = new HashMap<JRadioButton, ConflictStrategy>();
	private JRadioButton overriding = new JRadioButton("Overriding");
	private JRadioButton overridden = new JRadioButton("Overridden");
	private JRadioButton exception = new JRadioButton("Exception");
	private JRadioButton none = new JRadioButton("None");

	public ConflictStrategyRadioPanel() {
		super(new GridLayout(1, 0));
		// setBackground(Color.WHITE);
		setBorder(ComponentFactory.createTitledBorder("Conflict Strategy"));
		this.map.put(this.overriding, OverridingStrategy.getInstance());
		this.map.put(this.overridden, OverriddenStrategy.getInstance());
		this.map.put(this.exception, ExceptionStrategy.getInstance());
		this.theGroup.add(this.overriding);
		this.theGroup.add(this.overridden);
		this.theGroup.add(this.exception);
		this.theGroup.add(this.none);
		this.add(this.overriding);
		this.add(this.overridden);
		this.add(this.exception);
		this.add(this.none);
		this.none.doClick();
		this.overriding.addActionListener(this);
		this.overridden.addActionListener(this);
		this.exception.addActionListener(this);
		this.none.addActionListener(this);
	}

	public ConflictStrategy getSelectedConflictStrategy() {
		JRadioButton selected = null;
		if (this.overriding.isSelected()) {
			selected = this.overriding;
		}
		if (this.overridden.isSelected()) {
			selected = this.overridden;
		}
		if (this.exception.isSelected()) {
			selected = this.exception;
		}
		if (this.none.isSelected()) {
			selected = this.none;
		}
		// returns null for none
		return this.map.get(selected);
	}

	public void setConflictStrategy(ConflictStrategy c) {
		if (c == null) {
			this.none.doClick();
		} else {
			for (Map.Entry<JRadioButton, ConflictStrategy> e : this.map
					.entrySet()) {
				if (e.getValue().equals(c)) {
					e.getKey().doClick();
				}
			}
		}
	}

	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void actionPerformed(ActionEvent e) {
		for (InputVerificationStatusChangedListener i : this.listeners) {
			i.verifiedStatusChanged(true);
		}
	}

	public static void main(String[] args) {
		ConflictStrategyRadioPanel cp = new ConflictStrategyRadioPanel();
		JFrame test = new JFrame();
		test.getContentPane().add(cp);
		test.pack();
		test.setVisible(true);
		System.out.println(cp.getWidth() + " " + cp.getHeight());
	}
}
