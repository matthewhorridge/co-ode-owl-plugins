package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLException;

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
		edKit = kit;
		nameTextField.getDocument().addDocumentListener(this);
		checker = new OWLCalculationsExpressionChecker(edKit);
		storeToEditor = new StoreToEditor(edKit);
		storeToEditor.addStatusChangedListener(this);
		appliesToEditor = new AppliesToEditor_ExpressionEditor(edKit);
		storeToEditor.addStatusChangedListener(this);
		appliesToEditor.addStatusChangedListener(this);
		bindingviewer = new BindingViewer(edKit);
		bindingviewer.addStatusChangedListener(this);
		expression.setWrapStyleWord(true);
		expression.setLineWrap(true);
		expression.getDocument().addDocumentListener(this);
		conflictStrategyEditor.addStatusChangedListener(this);
		JPanel namePanel = new JPanel(new BorderLayout(0, 0));
		namePanel.setBorder(ComponentFactory.createTitledBorder("Name"));
		namePanel.add(nameTextField, BorderLayout.NORTH);
		this.add(namePanel, BorderLayout.NORTH);
		// expression
		mainPanel.add(StretchingPanelsFactory.getStretchyPanelWithBorder(
				expression, "Expression"), BorderLayout.NORTH);
		// bindings
		mainPanel.add(bindingviewer, BorderLayout.CENTER);
		more.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				GraphicalEditor.this.showExtendedView();
			}
		});
		less.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				GraphicalEditor.this.hideExtendedView();
			}
		});
		lowerPanel.add(more, BorderLayout.NORTH);
		extendedArea.add(StretchingPanelsFactory
				.getStretchyPanelWithBorder(conflictStrategyEditor,
						"Conflict Strategy"));
		extendedArea
				.add(StretchingPanelsFactory.getStretchyPanelWithBorder(
						appliesToEditor, "Applies to"));
		extendedArea.add(StretchingPanelsFactory
				.getStretchyPanelWithBorder(storeToEditor, "Copy to"));
		mainPanel.add(lowerPanel, BorderLayout.SOUTH);
		// optional bits
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(StretchingPanelsFactory.getStretchyPanelWithBorder(
				report, "Error report"), BorderLayout.SOUTH);
		// handleVerification();
	}

	protected void showExtendedView() {
		lowerPanel.remove(more);
		lowerPanel.add(less, BorderLayout.NORTH);
		lowerPanel.add(extendedArea, BorderLayout.CENTER);
		validate();
	}

	protected void hideExtendedView() {
		lowerPanel.remove(less);
		lowerPanel.remove(extendedArea);
		lowerPanel.add(more, BorderLayout.NORTH);
		validate();
	}

	@Override
    public void refreshComponent() {
		// copied from ExpressionEditor
		setFont(OWLRendererPreferences.getInstance().getFont());
	}

	@Override
    public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.add(listener);
	}

	@Override
    public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.remove(listener);
	}

	public FormulaModel createObject() throws OWLException {
		return checker.createObject(getText());
	}

	public void clear() {
		nameTextField.setText("");
		conflictStrategyEditor.setConflictStrategy(null);
		appliesToEditor.clear();
		storeToEditor.clear();
		bindingviewer.clear();
		expression.setText("");
		report.clearReport();
		// handleVerification();
	}

	public FormulaModel getFormulaModel() {
		FormulaModel fm = new FormulaModel();
		fm.setFormulaURI(getURI());
		ConflictStrategy selectedConflictStrategy = conflictStrategyEditor
				.getSelectedConflictStrategy();
		fm.setConflictStrategy(selectedConflictStrategy);
		fm.setAppliesTo(appliesToEditor.getAppliesTo());
		PropertyChainModel pcm = storeToEditor.getPropertyChainModel();
		if (pcm != null) {
			fm.setStorageModel(new StorageModel(pcm));
		} else {
			fm.setStorageModel(null);
		}
		if (bindingviewer.getBindingModels().size() > 0) {
			fm.setBindings(bindingviewer.getBindingModels());
		}
		String b = expression.getText();
		if (!b.endsWith(";")) {
			b += ";";
		}
		fm.setFormulaBody(b);
		return fm;
	}

    private IRI getURI() {
        IRI anURI = null;
		if (nameTextField.getText().length() > 0) {
            anURI = IRI.create(Constants.FORMULA_NAMESPACE_URI_STRING
						+ nameTextField.getText());
		} else {
			report.addReport("A name must be specified");
		}
		return anURI;
	}

	public String getText() {
		return getFormulaModel().render(edKit.getOWLModelManager());
	}

	public void initFormula(FormulaModel fm) {
		initializing = true;
		String localName = fm.getFormulaURI().getFragment();
		if (localName == null) {
			localName = "";
		}
		nameTextField.setText(localName);
		conflictStrategyEditor.setConflictStrategy(fm
				.getConflictStrategy());
		appliesToEditor.setAppliesTo(fm.getAppliesTo());
		StorageModel storageModel = fm.getStorageModel();
		if (storageModel != null) {
			storeToEditor.setStoreTo(storageModel.getPropertyChainModel());
		} else {
			storeToEditor.setStoreTo(null);
		}
		String formula = fm.getFormulaBody().trim();
		if (formula.endsWith(";")) {
			formula = formula.substring(0, formula.length() - 1);
		}
		expression.setText(formula);
		bindingviewer.addBindingModels(fm.getBindings());
		initializing = false;
		handleVerification();
	}

	protected void handleVerification() {
		if (!initializing) {
			report.clearReport();
			// if the uri is not assigned, it will be false
			boolean status = getURI() != null;
			String currentFormula = getText();
			updateIdentifiers();
			try {
				checker.check(currentFormula);
			} catch (Throwable e) {
				report.addReport(e.getMessage().replace(
						"uk.ac.manchester.mae.parser.ParseException: ", ""));
				status = false;
			}
			for (InputVerificationStatusChangedListener v : listeners) {
				v.verifiedStatusChanged(status);
			}
		}
	}

	private void updateIdentifiers() {
		/*
		 * then there is one or more unbound symbols: take the expression, pull
		 * out the symbols, add a binding for each one
		 */
		List<String> identifiers = getIdentifiers(expression
				.getText());
		List<String> currentIdentifiers = bindingviewer.getBindingNames();
		// any identifier in the viewer and not in the expression
		// should be removed; this ought to take into account typing
		// errors, so it will only remove bindings whose chain is
		// empty; those who have a chain should be marked somehow to
		// call for attention, but not deleted
		for (String id : currentIdentifiers) {
			if (!identifiers.contains(id)) {
				bindingviewer.removeBinding(id);
			}
		}
		for (String id : identifiers) {
			bindingviewer.addBinding(id);
		}
	}

	private static Pattern variableNames = Pattern
			.compile("[_a-zA-Z]([_a-zA-Z]|[0-9])*");

	private List<String> getIdentifiers(String _text) {
		String text = _text;
		// get rid of functions
		text = text.replace("SUM(", "").replace("sum(", "");
		// get rid of parentheses
		// text = text.replace("(", " ").replace(")", " ");
		Matcher m = variableNames.matcher(text);
		List<String> toReturn = new ArrayList<String>();
		while (m.find()) {
			toReturn.add(m.group());
		}
		// get rid of math symbols
		// text = text.replace("+", " ").replace("-", " ");
		// text = text.replace("*", " ").replace("/", " ").replace("%", " ");
		// text = text.replace("^", " ");
		//
		// // in case someone gets cute with spaces and empty lines
		// text = text.replace("\t", " ").replace("\n", " ");
		//
		// // and a final trim() just in case
		// for (String s : text.split(" ")) {
		// if (s.trim().length() > 0) {
		// toReturn.add(s.trim());
		// }
		// }
		return toReturn;
	}

	@Override
    public void changedUpdate(DocumentEvent e) {
		handleVerification();
	}

	@Override
    public void insertUpdate(DocumentEvent e) {
		handleVerification();
	}

	@Override
    public void removeUpdate(DocumentEvent e) {
		handleVerification();
	}

	@Override
    public void verifiedStatusChanged(boolean newState) {
		handleVerification();
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		handleVerification();
	}
}
