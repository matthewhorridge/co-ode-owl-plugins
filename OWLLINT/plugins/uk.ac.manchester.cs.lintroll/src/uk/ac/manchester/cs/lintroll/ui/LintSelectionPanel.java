/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owl.lint.Lint;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class LintSelectionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2302989184108579631L;
	private final Set<Lint<?>> availableLints = new HashSet<Lint<?>>();
	private final JPanel mainPanel = new JPanel(new BorderLayout());
	private final Set<JCheckBox> checkboxes = new HashSet<JCheckBox>();

	public LintSelectionPanel(Collection<? extends Lint<?>> availableLints) {
		this.availableLints.addAll(availableLints);
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.mainPanel.setBorder(ComponentFactory.createTitledBorder("Available lints:"));
		this.resetGUI();
		this.add(this.mainPanel, BorderLayout.CENTER);
	}

	/**
	 * 
	 */
	private void resetGUI() {
		this.mainPanel.removeAll();
		this.checkboxes.clear();
		Box availableLintBox = new Box(BoxLayout.Y_AXIS);
		for (final Lint<?> lint : this.getAvailableLints()) {
			final JCheckBox checkBox = new JCheckBox(lint.getName());
			checkBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (checkBox.isSelected()) {
						LintSelectionPanel.this.lintSelected(lint);
					} else {
						LintSelectionPanel.this.lintDeSelected(lint);
					}
				}
			});
			availableLintBox.add(checkBox);
			this.checkboxes.add(checkBox);
		}
		JScrollPane availableLintBoxScrollPane = ComponentFactory.createScrollPane(availableLintBox);
		this.mainPanel.add(availableLintBoxScrollPane, BorderLayout.CENTER);
	}

	/**
	 * @return the availableLints
	 */
	public Set<Lint<?>> getAvailableLints() {
		return new HashSet<Lint<?>>(this.availableLints);
	}

	public void setAvailableLints(Collection<? extends Lint<?>> availableLints) {
		this.availableLints.clear();
		this.availableLints.addAll(availableLints);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (JCheckBox c : this.checkboxes) {
			c.setEnabled(enabled);
		}
	}

	protected abstract void lintSelected(Lint<?> lint);

	protected abstract void lintDeSelected(Lint<?> lint);
}
