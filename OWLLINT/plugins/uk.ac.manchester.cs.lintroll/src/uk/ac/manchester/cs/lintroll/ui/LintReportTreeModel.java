package uk.ac.manchester.cs.lintroll.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.coode.lint.protege.ProtegeLintManager;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.lint.LintReport;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeEvent;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationChangeListener;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

public final class LintReportTreeModel extends DefaultTreeModel implements
		TreeModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1209595848554589051L;
	private static final DefaultMutableTreeNode LINT_ROLL_READY_DEFAULT_MUTABLE_TREE_NODE = new DefaultMutableTreeNode(
			"Press the button above for detecting lint");
	private final Set<LintReport<?>> reports = new HashSet<LintReport<?>>();
	private final OWLEditorKit owlEditorKit;
	private boolean isRunning = false;
	private boolean selectionChanged = false;
	private boolean isDirty = false;
	private final Comparator<OWLObject> lexicographicRenderingComparator = new Comparator<OWLObject>() {
		public int compare(OWLObject o1, OWLObject o2) {
			int toReturn;
			if (o1 == o2) {
				toReturn = 0;
			} else if (o1 == null) {
				toReturn = -1;
			} else if (o2 == null) {
				toReturn = 1;
			} else {
				String o1Rendering = LintReportTreeModel.this.owlEditorKit
						.getOWLModelManager().getRendering(o1);
				String o2Rendering = LintReportTreeModel.this.owlEditorKit
						.getOWLModelManager().getRendering(o2);
				toReturn = o1Rendering.compareTo(o2Rendering);
			}
			return toReturn;
		}
	};
	private LintConfigurationChangeListener lodedLintConfigurationChangeListener = new LintConfigurationChangeListener() {
		public void configurationChanged(LintConfigurationChangeEvent event) {
			LintReportTreeModel.this.setDirty(true);
		}
	};
	private static final DefaultMutableTreeNode EXECUTING_LINT_CHECKS_PLEASE_WAIT_DEFAULT_MUTABLE_TREE_NODE = new DefaultMutableTreeNode(
			"Executing lint checks please wait...");
	private static final DefaultMutableTreeNode NO_LINT_DETECTED_DEFAULT_MUTABLE_TREE_NODE = new DefaultMutableTreeNode(
			"No Lint detected");
	private static final DefaultMutableTreeNode NO_LINT_SELECTED_DEFAULT_MUTABLE_TREE_NODE = new DefaultMutableTreeNode(
			"No Lint selected");
	private static final DefaultMutableTreeNode DIRTY_REPORTS_DEFAULT_MUTABLE_TREE_NODE = new DefaultMutableTreeNode(
			"Some changes occured in the ontology/lint configuration. The reports below may be affected please recompute");

	public LintReportTreeModel(OWLEditorKit owlEditorKit) {
		super(NO_LINT_SELECTED_DEFAULT_MUTABLE_TREE_NODE);
		if (owlEditorKit == null) {
			throw new NullPointerException("The editor kit cannot be null");
		}
		this.owlEditorKit = owlEditorKit;
		this.setupListeners();
	}

	public void setReports(Collection<? extends LintReport<?>> reports) {
		if (reports == null) {
			throw new NullPointerException(
					"The report collection cannot be null");
		}
		this.reports.clear();
		this.reports.addAll(reports);
		this.setIsRunning(false);
	}

	private void reset() {
		DefaultMutableTreeNode newRoot = this.isRunning ? EXECUTING_LINT_CHECKS_PLEASE_WAIT_DEFAULT_MUTABLE_TREE_NODE
				: this.getNonRunningRoot();
		for (LintReport<?> lintReport : this.reports) {
			DefaultMutableTreeNode lintNode = new DefaultMutableTreeNode(
					lintReport);
			for (OWLOntology ontology : lintReport.getAffectedOntologies()) {
				DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode(
						ontology);
				// Invert the order as they get loaded in reverse order.
				Set<OWLObject> owlObjects = new TreeSet<OWLObject>(Collections
						.reverseOrder(this.lexicographicRenderingComparator));
				owlObjects.addAll(lintReport.getAffectedOWLObjects(ontology));
				for (OWLObject owlObject : owlObjects) {
					DefaultMutableTreeNode owlObjectNode = new DefaultMutableTreeNode(
							owlObject);
					this.insertNodeInto(owlObjectNode, ontologyNode, 0);
				}
				this.insertNodeInto(ontologyNode, lintNode, 0);
			}
			this.insertNodeInto(lintNode, newRoot, 0);
		}
		this.setRoot(newRoot);
		this.setupListeners();
	}

	private DefaultMutableTreeNode getNonRunningRoot() {
		DefaultMutableTreeNode toReturn = this.isDirty ? DIRTY_REPORTS_DEFAULT_MUTABLE_TREE_NODE
				: this.getCleanRoot();
		return toReturn;
	}

	private DefaultMutableTreeNode getCleanRoot() {
		boolean emptySelectedLints = ProtegeLintManager.getInstance(
				this.owlEditorKit).getSelectedLints().isEmpty();
		DefaultMutableTreeNode toReturn = emptySelectedLints ? NO_LINT_SELECTED_DEFAULT_MUTABLE_TREE_NODE
				: this.reports.isEmpty() ? this.selectionChanged ? LINT_ROLL_READY_DEFAULT_MUTABLE_TREE_NODE
						: NO_LINT_DETECTED_DEFAULT_MUTABLE_TREE_NODE
						: new DefaultMutableTreeNode("Lint Reports");
		return toReturn;
	}

	public void setIsRunning(boolean running) {
		this.isRunning = running;
		if (this.isRunning) {
			this.reports.clear();
		}
		this.reset();
	}

	/**
	 * @return the isDirty
	 */
	public boolean isDirty() {
		return this.isDirty;
	}

	/**
	 * @param isDirty
	 *            the isDirty to set
	 */
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		this.setIsRunning(false);
	}

	private void setupListeners() {
		Set<Lint<?>> selectedLints = ProtegeLintManager.getInstance(
				this.owlEditorKit).getSelectedLints();
		for (Lint<?> lint : selectedLints) {
			lint.getLintConfiguration().addLintConfigurationChangeListener(
					this.lodedLintConfigurationChangeListener);
		}
	}

	public void clear() {
		this.reports.clear();
		this.setDirty(false);
		this.selectionChanged = false;
	}

	public void dispose() {
		Set<Lint<?>> selectedLints = ProtegeLintManager.getInstance(
				this.owlEditorKit).getSelectedLints();
		for (Lint<?> lint : selectedLints) {
			lint.getLintConfiguration().removeLintConfigurationChangeListener(
					this.lodedLintConfigurationChangeListener);
		}
	}

	/**
	 * @return the selectionChanged
	 */
	public boolean isSelectionChanged() {
		return this.selectionChanged;
	}

	/**
	 * @param selectionChanged
	 *            the selectionChanged to set
	 */
	public void setSelectionChanged(boolean selectionChanged) {
		this.selectionChanged = selectionChanged;
		if (selectionChanged) {
			this.clear();
		}
	}
}
