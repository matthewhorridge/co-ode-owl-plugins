package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.coode.oae.ui.utils.StretchingPanelsFactory;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owl.model.OWLException;

import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.Constants;
import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.evaluation.StorageModel;

public class GraphicalEditor extends JPanel implements RefreshableComponent,
		VerifiedInputEditor, DocumentListener,
		InputVerificationStatusChangedListener, ActionListener {
	private static final long serialVersionUID = 4697754251654457170L;
	protected JTextField nameTextField = ComponentFactory.createTextField();
	private JTextArea expression = new JTextArea();
	private StoreToEditor storeToEditor;
	private AppliesToEditor_ExpressionEditor appliesToEditor;
	protected ConflictStrategyRadioPanel conflictStrategyEditor = new ConflictStrategyRadioPanel();
	protected ErrorReport report = new ErrorReport();
	protected BindingViewer bindingviewer;
	private transient OWLExpressionChecker<FormulaModel> checker;
	private OWLEditorKit edKit;
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	boolean initializing = false;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private final JPanel extendedArea = new JPanel(new GridLayout(3, 1));
	private JPanel lowerPanel = StretchingPanelsFactory
			.getStretchyPanelWithBorder(null, null);
	private final JButton more = new JButton("More...");
	private final JButton less = new JButton("Less...");

	public GraphicalEditor(OWLEditorKit kit) {
		super(new BorderLayout());
		this.edKit = kit;
		this.nameTextField.getDocument().addDocumentListener(this);
		this.checker = new OWLCalculationsExpressionChecker(this.edKit);
		this.storeToEditor = new StoreToEditor(this.edKit);
		this.storeToEditor.addStatusChangedListener(this);
		this.appliesToEditor = new AppliesToEditor_ExpressionEditor(this.edKit);
		this.storeToEditor.addStatusChangedListener(this);
		this.appliesToEditor.addStatusChangedListener(this);
		this.bindingviewer = new BindingViewer(this.edKit);
		this.bindingviewer.addStatusChangedListener(this);
		this.expression.setWrapStyleWord(true);
		this.expression.setLineWrap(true);
		this.expression.getDocument().addDocumentListener(this);
		this.conflictStrategyEditor.addStatusChangedListener(this);
		JPanel namePanel = new JPanel(new BorderLayout(0, 0));
		namePanel.setBorder(ComponentFactory.createTitledBorder("Name"));
		namePanel.add(this.nameTextField, BorderLayout.NORTH);
		this.add(namePanel, BorderLayout.NORTH);
		// expression
		this.mainPanel.add(StretchingPanelsFactory.getStretchyPanelWithBorder(
				this.expression, "Expression"), BorderLayout.NORTH);
		// bindings
		this.mainPanel.add(this.bindingviewer, BorderLayout.CENTER);
		this.more.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showExtendedView();
			}
		});
		this.less.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideExtendedView();
			}
		});
		this.lowerPanel.add(this.more, BorderLayout.NORTH);
		this.extendedArea.add(StretchingPanelsFactory
				.getStretchyPanelWithBorder(this.conflictStrategyEditor,
						"Conflict Strategy"));
		this.extendedArea
				.add(StretchingPanelsFactory.getStretchyPanelWithBorder(
						this.appliesToEditor, "Applies to"));
		this.extendedArea.add(StretchingPanelsFactory
				.getStretchyPanelWithBorder(this.storeToEditor, "Copy to"));
		this.mainPanel.add(this.lowerPanel, BorderLayout.SOUTH);
		// optional bits
		this.add(this.mainPanel, BorderLayout.CENTER);
		this.add(StretchingPanelsFactory.getStretchyPanelWithBorder(
				this.report, "Error report"), BorderLayout.SOUTH);
		// handleVerification();
	}

	protected void showExtendedView() {
		this.lowerPanel.remove(this.more);
		this.lowerPanel.add(this.less, BorderLayout.NORTH);
		this.lowerPanel.add(this.extendedArea, BorderLayout.CENTER);
		validate();
	}

	protected void hideExtendedView() {
		this.lowerPanel.remove(this.less);
		this.lowerPanel.remove(this.extendedArea);
		this.lowerPanel.add(this.more, BorderLayout.NORTH);
		validate();
	}

	public void refreshComponent() {
		// copied from ExpressionEditor
		setFont(OWLRendererPreferences.getInstance().getFont());
	}

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}

	public FormulaModel createObject() throws OWLException {
		return this.checker.createObject(getText());
	}

	public void clear() {
		this.nameTextField.setText("");
		this.conflictStrategyEditor.setConflictStrategy(null);
		this.appliesToEditor.clear();
		this.storeToEditor.clear();
		this.bindingviewer.clear();
		this.expression.setText("");
		this.report.clearReport();
		// handleVerification();
	}

	public FormulaModel getFormulaModel() {
		FormulaModel fm = new FormulaModel();
		fm.setFormulaURI(getURI());
		ConflictStrategy selectedConflictStrategy = this.conflictStrategyEditor
				.getSelectedConflictStrategy();
		fm.setConflictStrategy(selectedConflictStrategy);
		fm.setAppliesTo(this.appliesToEditor.getAppliesTo());
		PropertyChainModel pcm = this.storeToEditor.getPropertyChainModel();
		if (pcm != null) {
			fm.setStorageModel(new StorageModel(pcm));
		} else {
			fm.setStorageModel(null);
		}
		if (this.bindingviewer.getBindingModels().size() > 0) {
			fm.setBindings(this.bindingviewer.getBindingModels());
		}
		String b = this.expression.getText();
		if (!b.endsWith(";")) {
			b += ";";
		}
		fm.setFormulaBody(b);
		return fm;
	}

	private URI getURI() {
		URI anURI = null;
		if (this.nameTextField.getText().length() > 0) {
			try {
				anURI = new URI(Constants.FORMULA_NAMESPACE_URI_STRING
						+ this.nameTextField.getText());
			} catch (URISyntaxException e) {
				this.report
						.addReport("A valid name must be specified: current name causes URISyntaxException: "
								+ e.getMessage());
			}
		} else {
			this.report.addReport("A name must be specified");
		}
		return anURI;
	}

	public String getText() {
		return getFormulaModel().render(this.edKit.getOWLModelManager());
	}

	public void initFormula(FormulaModel fm) {
		this.initializing = true;
		String localName = fm.getFormulaURI().getFragment();
		if (localName == null) {
			localName = "";
		}
		this.nameTextField.setText(localName);
		this.conflictStrategyEditor.setConflictStrategy(fm
				.getConflictStrategy());
		this.appliesToEditor.setAppliesTo(fm.getAppliesTo());
		StorageModel storageModel = fm.getStorageModel();
		if (storageModel != null) {
			this.storeToEditor.setStoreTo(storageModel.getPropertyChainModel());
		} else {
			this.storeToEditor.setStoreTo(null);
		}
		String formula = fm.getFormulaBody().trim();
		if (formula.endsWith(";")) {
			formula = formula.substring(0, formula.length() - 1);
		}
		this.expression.setText(formula);
		this.bindingviewer.addBindingModels(fm.getBindings());
		this.initializing = false;
		handleVerification();
	}

	protected void handleVerification() {
		if (!this.initializing) {
			this.report.clearReport();
			// if the uri is not assigned, it will be false
			boolean status = getURI() != null;
			String currentFormula = getText();
			System.out.println("GraphicalEditor.handleVerification() "
					+ currentFormula);
			updateIdentifiers();
			try {
				this.checker.check(currentFormula);
			} catch (Throwable e) {
				// e.printStackTrace(System.out);
				this.report.addReport(e.getMessage().replace(
						"uk.ac.manchester.mae.parser.ParseException: ", ""));
				status = false;
			}
			for (InputVerificationStatusChangedListener v : this.listeners) {
				v.verifiedStatusChanged(status);
			}
		}
	}

	private void updateIdentifiers() {
		/*
		 * then there is one or more unbound symbols: take the expression, pull
		 * out the symbols, add a binding for each one
		 */
		List<String> identifiers = getIdentifiers(this.expression.getText());
		List<String> currentIdentifiers = this.bindingviewer.getBindingNames();
		// any identifier in the viewer and not in the expression
		// should be removed; this ought to take into account typing
		// errors, so it will only remove bindings whose chain is
		// empty; those who have a chain should be marked somehow to
		// call for attention, but not deleted
		for (String id : currentIdentifiers) {
			if (!identifiers.contains(id)) {
				this.bindingviewer.removeBinding(id);
			}
		}
		for (String id : identifiers) {
			this.bindingviewer.addBinding(id);
		}
	}

	private List<String> getIdentifiers(String _text) {
		String text = _text;
		// get rid of functions
		text = text.replace("SUM(", "").replace("sum(", "");
		// get rid of math symbols
		text = text.replace("+", " ").replace("-", " ");
		text = text.replace("*", " ").replace("/", " ").replace("%", " ");
		text = text.replace("^", " ");
		// get rid of parentheses
		text = text.replace("(", " ").replace(")", " ");
		// in case someone gets cute with spaces and empty lines
		text = text.replace("\t", " ").replace("\n", " ");
		List<String> toReturn = new ArrayList<String>();
		// and a final trim() just in case
		for (String s : text.split(" ")) {
			if (s.trim().length() > 0) {
				toReturn.add(s.trim());
			}
		}
		return toReturn;
	}

	public void changedUpdate(DocumentEvent e) {
		handleVerification();
	}

	public void insertUpdate(DocumentEvent e) {
		handleVerification();
	}

	public void removeUpdate(DocumentEvent e) {
		handleVerification();
	}

	public void verifiedStatusChanged(boolean newState) {
		handleVerification();
	}

	public void actionPerformed(ActionEvent e) {
		handleVerification();
	}
}
