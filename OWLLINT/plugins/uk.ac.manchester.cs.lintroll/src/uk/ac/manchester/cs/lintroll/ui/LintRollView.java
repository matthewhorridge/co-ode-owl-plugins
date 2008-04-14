/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLClassViewComponent;
import org.semanticweb.owl.lint.InferenceLintPatter;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.LintException;
import org.semanticweb.owl.lint.LintManager;
import org.semanticweb.owl.lint.LintPattern;
import org.semanticweb.owl.lint.LintReport;
import org.semanticweb.owl.lint.PatternBasedLint;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;

import uk.ac.manchester.cs.lintroll.utils.JarClassLoader;
import uk.ac.manchester.cs.lintroll.utils.JarFileFilter;
import uk.ac.manchester.cs.owl.lint.LintManagerFactory;
import uk.ac.manchester.cs.owl.lint.PatternBasedLintImpl;
import uk.ac.manchester.cs.owl.lint.commons.OntologyWiseLintPattern;

// TODO Manage the possibility of applying only to the active ontology
/**
 * @author Luigi Iannone
 * 
 * 
 * http://www.cs.man.ac.uk/~iannonel
 * 
 * The University Of Manchester Bio Health Informatics Group Date: February 11,
 * 2008
 * 
 */
public class LintRollView extends AbstractOWLClassViewComponent implements
		PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2527582629024593024L;
	private JButton loadLintButton = new JButton(UIConstants.LOAD_LINTS_CAPTION);
	private JButton loadPatternButton = new JButton(
			UIConstants.LOAD_PATTERNS_CAPTION);
	private JButton createLintFromPatternsButton = new JButton(
			UIConstants.CREATE_LINT_FROM_PATTERNS_CAPTION);
	private JButton runButton = new JButton(UIConstants.RUN_LINTS_CAPTION);
	private JTextField newLintNameField = new JTextField(11);
	SelectLists<Lint> lintSelector;
	JTree lintReportTree = null;
	TreeModel lintReportTreeModel;
	private SelectLists<LintPattern> patternSelector;
	private JPanel patternSelectionPanel;
	private JPanel lintSelectorPanel;
	private JScrollPane treePane = null;
	private LabelledTextArea lintDescriptionLabelledTextArea;
	static Set<String> wellKnownLintClassNames, wellKnownPatternNames;
	static {
		wellKnownLintClassNames = new HashSet<String>();
		wellKnownLintClassNames.add(Lint.class.getName());
		wellKnownLintClassNames.add(PatternBasedLint.class.getName());
		wellKnownLintClassNames.add(PatternBasedLintImpl.class.getName());
		wellKnownPatternNames = new HashSet<String>();
		wellKnownPatternNames.add(LintPattern.class.getName());
		wellKnownPatternNames.add(InferenceLintPatter.class.getName());
		wellKnownPatternNames.add(OntologyWiseLintPattern.class.getName());
	}

	@Override
	public void initialiseClassView() throws Exception {
		this.setLayout(new GridLayout(4, 0));
		this.setupLints();
		this.initLintSelectorPanel();
		this.initPatternSelectorPane();
		this.add(this.lintSelectorPanel);
		this.add(this.patternSelectionPanel);
		this.initLoadLintsButton();
		this.initRunButton();
		this.add(this.runButton);
	}

	/**
	 * 
	 */
	private void initLintSelectorPanel() {
		this.lintSelectorPanel = new JPanel(new GridLayout(2, 1));
		this.lintDescriptionLabelledTextArea = new LabelledTextArea(
				"Lint Description");
		this.lintDescriptionLabelledTextArea.setLayout(new GridLayout(2, 0));
		PropertyChangeListener lintSelectorSelectionChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource().equals(LintRollView.this.lintSelector)
						&& evt.getPropertyName().equals(
								SelectLists.AVAILABLE_SELECTION_CHANGE_EVENT)) {
					if (LintRollView.this.lintSelector.getAvailableSelected().length == 1) {
						Lint selectedLint = (Lint) LintRollView.this.lintSelector
								.getAvailableSelected()[0];
						String description = selectedLint.getDescription();
						LintRollView.this.lintDescriptionLabelledTextArea
								.getTextArea().setText(
										description == null ? "" : description);
					} else {
						LintRollView.this.lintDescriptionLabelledTextArea
								.getTextArea().setText("");
					}
				}
			}
		};
		this.lintDescriptionLabelledTextArea
				.addPropertyChangeListener(lintSelectorSelectionChangeListener);
		this.lintSelector
				.addPropertyChangeListener(lintSelectorSelectionChangeListener);
		this.lintSelectorPanel.add(this.lintSelector);
		this.lintSelectorPanel.add(this.lintDescriptionLabelledTextArea);
		this.lintSelectorPanel.add(this.loadLintButton);
	}

	private void setupLints() {
		Set<Lint> options = new HashSet<Lint>();
		this.lintSelector = new SelectLists<Lint>(
				UIConstants.AVAILABLE_LINTS_LABEL,
				UIConstants.SELECTED_LINTS_LABEL, options);
		this.lintSelector.addPropertyChangeListener(
				SelectLists.SELECTED_NOT_EMPTY, this);
		this.lintSelector.setCellRenderer(new LintRenderer(this
				.getOWLEditorKit()));
	}

	private void initPatternSelectorPane() {
		this.patternSelectionPanel = new JPanel(new GridLayout(2, 0));
		this.patternSelector = new SelectLists<LintPattern>(
				UIConstants.AVAILABLE_PATTERNS_LABEL,
				UIConstants.SELECTED_PATTERNS_LABEL, new HashSet<LintPattern>());
		this.patternSelector.addPropertyChangeListener(
				SelectLists.SELECTED_NOT_EMPTY, this);
		this.patternSelector.setCellRenderer(new LintRenderer(this
				.getOWLEditorKit()));
		this.initLoadPatternsButton();
		this.createLintFromPatternsButton.setEnabled(false);
		JLabel createdLintNameLabel = new JLabel(
				UIConstants.CREATE_LINT_FROM_PATTERN_NAME_LABEL,
				SwingConstants.RIGHT);
		this.newLintNameField.setEnabled(false);
		this.patternSelectionPanel.add(this.patternSelector);
		JPanel panel = new JPanel(new GridLayout(0, 4));
		panel.add(this.loadPatternButton);
		panel.add(createdLintNameLabel);
		panel.add(this.newLintNameField);
		panel.add(this.createLintFromPatternsButton);
		this.initCreateLintButton();
		this.patternSelectionPanel.add(panel);
	}

	private void initCreateLintButton() {
		this.createLintFromPatternsButton
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String newName = LintRollView.this.newLintNameField
								.getText().trim();
						newName = newName.compareTo("") == 0 ? null : newName;
						Set<LintPattern> patterns = LintRollView.this.patternSelector
								.getSelectedItems();
						Lint newLint = LintManagerFactory
								.getLintManager()
								.getLintFactory()
								.createLint(
										patterns
												.toArray(new LintPattern[patterns
														.size()]));
						newLint.setName(newName);
						LintRollView.this.lintSelector.addAvailable(newLint);
						LintRollView.this.newLintNameField.setText("");
					}
				});
	}

	private void initRunButton() {
		this.runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LintManager lintManager = LintManagerFactory.getLintManager();
				Set<Lint> selectedLints = LintRollView.this.lintSelector
						.getSelectedItems();
				try {
					Set<LintReport> lintReports = lintManager.run(
							selectedLints, LintRollView.this
									.getOWLModelManager().getOntologies());
					LintRollView.this.displayReport(lintReports);
				} catch (LintException e) {
					Logger.getLogger(this.getClass()).error(
							"Error in running lint manager", e);
				}
			}
		});
		this.runButton.setEnabled(false);
	}

	private void displayReport(Set<LintReport> lintReports) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				UIConstants.TREE_REPORT_CAPTION);
		this.lintReportTreeModel = new DefaultTreeModel(root);
		for (LintReport lintReport : lintReports) {
			DefaultMutableTreeNode lintNode = new DefaultMutableTreeNode(
					lintReport);
			for (OWLOntology ontology : lintReport.getAffectedOntologies()) {
				DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode(
						ontology);
				for (OWLObject owlObject : lintReport
						.getAffectedOWLObjects(ontology)) {
					DefaultMutableTreeNode owlObjectNode = new DefaultMutableTreeNode(
							owlObject);
					ontologyNode.add(owlObjectNode);
				}
				lintNode.add(ontologyNode);
			}
			root.add(lintNode);
		}
		if (this.treePane != null) {
			this.remove(this.treePane);
		}
		this.lintReportTree = new JTree(this.lintReportTreeModel);
		this.lintReportTree.setCellRenderer(new LintRenderer(this
				.getOWLEditorKit()));
		this.lintReportTree.revalidate();
		this.treePane = new JScrollPane(this.lintReportTree);
		this.add(this.treePane);
		this.revalidate();
	}

	private void initLoadLintsButton() {
		this.loadLintButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooseAJar = new JFileChooser(System
						.getProperty("user.dir"));
				chooseAJar.setFileFilter(new JarFileFilter());
				chooseAJar.showOpenDialog(LintRollView.this);
				File selectedJar = chooseAJar.getSelectedFile();
				if (selectedJar != null) {
					Set<Lint> lints = LintRollView.this.loadLints(selectedJar
							.getAbsolutePath());
					LintRollView.this.lintSelector.addAllAvailable(lints);
				}
			}
		});
	}

	private void initLoadPatternsButton() {
		this.loadPatternButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooseAJar = new JFileChooser(System
						.getProperty("user.dir"));
				chooseAJar.setFileFilter(new JarFileFilter());
				chooseAJar.showOpenDialog(LintRollView.this);
				File selectedJar = chooseAJar.getSelectedFile();
				if (selectedJar != null) {
					Set<LintPattern> patterns = LintRollView.this
							.loadPatterns(selectedJar.getAbsolutePath());
					LintRollView.this.patternSelector.addAllAvailable(patterns);
				}
			}
		});
	}

	private Set<LintPattern> loadPatterns(String jarName) {
		Set<String> classNames = new HashSet<String>();
		Set<LintPattern> toReturn = new HashSet<LintPattern>();
		try {
			this.getClass().getClassLoader().loadClass(
					LintPattern.class.getName());
			JarClassLoader cl = new JarClassLoader(jarName);
			classNames = cl.getClassNames();
			for (String string : new HashSet<String>(classNames)) {
				Class<? extends Object> clazz = this.getClass()
						.getClassLoader().loadClass(string);
				if (!LintRollView.wellKnownPatternNames.contains(string)
						&& LintPattern.class.isAssignableFrom(clazz)) {
					toReturn.add((LintPattern) clazz.newInstance());
				} else {
					classNames.remove(string);
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(this.getClass()).error("Unable to load class", e);
		} catch (InstantiationException e) {
			Logger.getLogger(this.getClass()).error(
					"Unable to instantiate pattern class", e);
		} catch (IllegalAccessException e) {
			Logger.getLogger(this.getClass()).error(
					"Unable to instantiate pattern class", e);
		}
		return toReturn;
	}

	private Set<Lint> loadLints(String jarName) {
		Set<String> classNames = new HashSet<String>();
		Set<Lint> toReturn = new HashSet<Lint>();
		try {
			this.getClass().getClassLoader().loadClass(Lint.class.getName());
			JarClassLoader cl = new JarClassLoader(jarName);
			classNames = cl.getClassNames();
			for (String string : new HashSet<String>(classNames)) {
				Class<? extends Object> clazz = this.getClass()
						.getClassLoader().loadClass(string);
				if (!LintRollView.wellKnownLintClassNames.contains(string)
						&& Lint.class.isAssignableFrom(clazz)) {
					toReturn.add((Lint) clazz.newInstance());
				} else {
					classNames.remove(string);
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(this.getClass()).error("Unable to load class", e);
		} catch (InstantiationException e) {
			Logger.getLogger(this.getClass()).error(
					"Unable to instantiate lint class", e);
		} catch (IllegalAccessException e) {
			Logger.getLogger(this.getClass()).error(
					"Unable to instantiate lint class", e);
		}
		return toReturn;
	}

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disposeView() {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		JarClassLoader cl = new JarClassLoader(
				"/Users/luigi/Documents/workspace/protege-standalone/plugins/uk.ac.manchester.cs.lintroll/lib/lint.jar");
		Set<String> classNames = cl.getClassNames();
		System.out.println(classNames);
		for (String string : new HashSet<String>(classNames)) {
			try {
				Class<? extends Object> clazz = cl.loadClass(string);
				if (!LintRollView.wellKnownLintClassNames.contains(string)
						&& Lint.class.isAssignableFrom(clazz)) {
				} else {
					classNames.remove(string);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println(classNames);
	}

	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getSource().equals(this.lintSelector)
				&& pce.getPropertyName().equals(SelectLists.SELECTED_NOT_EMPTY)) {
			this.runButton.setEnabled(pce.getNewValue().equals(true));
		}
		if (pce.getSource().equals(this.patternSelector)
				&& pce.getPropertyName().equals(SelectLists.SELECTED_NOT_EMPTY)) {
			this.createLintFromPatternsButton.setEnabled(pce.getNewValue()
					.equals(true));
			this.newLintNameField.setEnabled(pce.getNewValue().equals(true));
		}
	}
}
