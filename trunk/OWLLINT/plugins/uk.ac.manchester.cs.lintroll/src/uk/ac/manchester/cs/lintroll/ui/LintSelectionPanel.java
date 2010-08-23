/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.coode.lint.protege.ProtegeLintManager;
import org.coode.lint.protege.ProtegeLintManager.LintLoadListener;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.lint.Lint;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class LintSelectionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2302989184108579631L;
	private final JPanel mainPanel = new JPanel(new BorderLayout());
	private final Set<JCheckBox> checkboxes = new HashSet<JCheckBox>();
	private static final Comparator<Lint<?>> lintLexicographicComparator = new Comparator<Lint<?>>() {
		public int compare(Lint<?> o1, Lint<?> o2) {
			int toReturn;
			if (o1 == o2) {
				toReturn = 0;
			} else if (o1 == null) {
				toReturn = -1;
			} else if (o2 == null) {
				toReturn = 1;
			} else {
				toReturn = o1.getName().compareTo(o2.getName());
			}
			return toReturn;
		}
	};
	private final Set<Lint<?>> availableLints = new TreeSet<Lint<?>>(
			lintLexicographicComparator);
	private final OWLEditorKit owlEditorKit;
	private final LintLoadListener lintLoadListener = new LintLoadListener() {
		public void loadChanged() {
			LintSelectionPanel.this.availableLints.clear();
			LintSelectionPanel.this.availableLints.addAll(ProtegeLintManager
					.getInstance(LintSelectionPanel.this.getOWLEditorKit())
					.getLoadedLints());
			LintSelectionPanel.this.resetGUI();
		}
	};

	public LintSelectionPanel(OWLEditorKit owlEditorKit) {
		if (owlEditorKit == null) {
			throw new NullPointerException("The OWL editor Kit cannot be null");
		}
		this.owlEditorKit = owlEditorKit;
		Set<Lint<?>> loadedLints = ProtegeLintManager.getInstance(
				this.getOWLEditorKit()).getLoadedLints();
		this.availableLints.addAll(loadedLints);
		ProtegeLintManager.getInstance(this.getOWLEditorKit())
				.addLintLoadListener(this.lintLoadListener);
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.mainPanel.setBorder(ComponentFactory
				.createTitledBorder("Available lints:"));
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
		JScrollPane availableLintBoxScrollPane = ComponentFactory
				.createScrollPane(availableLintBox);
		this.mainPanel.add(availableLintBoxScrollPane, BorderLayout.CENTER);
	}

	/**
	 * @return the availableLints
	 */
	public Set<Lint<?>> getAvailableLints() {
		return new LinkedHashSet<Lint<?>>(this.availableLints);
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

	/**
	 * @return the owlEditorKit
	 */
	public OWLEditorKit getOWLEditorKit() {
		return this.owlEditorKit;
	}
}
