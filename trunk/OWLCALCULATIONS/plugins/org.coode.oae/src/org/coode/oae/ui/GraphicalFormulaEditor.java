package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.coode.oae.ui.VariableListModel.VariableListItem;
import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLException;

import uk.ac.manchester.mae.ConflictStrategy;
import uk.ac.manchester.mae.ConflictStrategyFactory;
import uk.ac.manchester.mae.ExceptionStrategy;
import uk.ac.manchester.mae.OverriddenStrategy;
import uk.ac.manchester.mae.OverridingStrategy;
import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.evaluation.StorageModel;

public class GraphicalFormulaEditor extends JPanel implements
		RefreshableComponent, VerifiedInputEditor, DocumentListener,
		InputVerificationStatusChangedListener, ActionListener {
	public static final Dimension LIST_PREFERRED_SIZE = new Dimension(300, 200);
	private static final long serialVersionUID = 4697754251654457170L;

	protected final class MyMList extends MList {
		private static final long serialVersionUID = -6626947893443460240L;

		@Override
		protected void handleAdd() {
			GraphicalFormulaEditor.this.bindingEditor.setBindingModel(null);
			addBinding();
		}

		@Override
		@SuppressWarnings("unchecked")
		protected void handleDelete() {
			GraphicalFormulaEditor.this.bindingModels
					.remove(((VariableListItem<BindingModel>) getSelectedValue())
							.getItem());
			handleChange();
		}

		@Override
		@SuppressWarnings("unchecked")
		protected void handleEdit() {
			GraphicalFormulaEditor.this.bindingEditor
					.setBindingModel(((VariableListItem<BindingModel>) getSelectedValue())
							.getItem());
			addBinding();
		}
	}

	static ImageIcon addIcon = new ImageIcon(GraphicalFormulaEditor.class
			.getClassLoader().getResource("add.png"));
	static ImageIcon delIcon = new ImageIcon(GraphicalFormulaEditor.class
			.getClassLoader().getResource("delete.png"));
	static Dimension REGULAR_FIELD_DIMENSION = new Dimension(150, 25);
	static Dimension LONG_FIELD_DIMENSION = new Dimension(200, 25);
	static Dimension SHORT_FIELD_DIMENSION = new Dimension(75, 25);
	private static String[] strategies = new String[] { "none",
			OverridingStrategy.getInstance().toString(),
			OverriddenStrategy.getInstance().toString(),
			ExceptionStrategy.getInstance().toString() };
	private static String[] functions = new String[] { "none", "SUM" };
	private JComboBox conflictStrategy = new JComboBox(strategies);
	private JComboBox function = new JComboBox(functions);
	private JTextField expression = new JTextField();
	private StoreToEditor storeToEditor;
	private AppliesToEditor appliesToEditor;
	protected BindingEditor bindingEditor;
	protected final Set<BindingModel> bindingModels = new HashSet<BindingModel>();
	private final VariableListModel<BindingModel> bindingViewModel = new VariableListModel<BindingModel>(
			this.bindingModels, "Bindings");
	protected MList bindingView = new MyMList();
	private OWLExpressionChecker<FormulaModel> checker;
	private OWLEditorKit edKit;
	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
	private boolean initializing = false;
	private ExpressionEditor<FormulaModel> editor;

	public GraphicalFormulaEditor(OWLEditorKit kit) {
		this.edKit = kit;
		this.checker = new OWLCalculationsExpressionChecker(this.edKit);
		this.editor = new ExpressionEditor<FormulaModel>(this.edKit,
				new OWLCalculationsExpressionChecker(this.edKit));
		this.editor.setBorder(new TitledBorder("Current formula"));
		this.editor.setEditable(false);
		this.bindingEditor = new BindingEditor(this.edKit);
		this.storeToEditor = new StoreToEditor(this.edKit);
		this.appliesToEditor = new AppliesToEditor(this.edKit);
		this.storeToEditor.addStatusChangedListener(this);
		this.appliesToEditor.addStatusChangedListener(this);
		this.bindingView.setModel(this.bindingViewModel);
		this.bindingView.setCellRenderer(new RenderableObjectCellRenderer(
				this.edKit));
		this.expression.getDocument().addDocumentListener(this);
		setLayout(new BorderLayout());
		this.add(this.editor, BorderLayout.NORTH);
		this.add(leftside(), BorderLayout.CENTER);
		this.bindingViewModel.init();
	}

	private Component center() {
		JPanel center = new JPanel();
		center.setBorder(new TitledBorder("Bindings:"));
		JScrollPane scroll = new JScrollPane(this.bindingView);
		scroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		center.add(scroll);
		return center;
	}

	private JPanel leftside() {
		JPanel toReturn = new JPanel(new GridLayout(2, 2));
		JPanel square1 = new JPanel(new GridLayout(2, 1));
		JPanel p1 = new JPanel();
		p1.setBorder(new TitledBorder("Conflict Strategy"));
		this.conflictStrategy.setPreferredSize(REGULAR_FIELD_DIMENSION);
		this.conflictStrategy.addActionListener(this);
		p1.add(this.conflictStrategy);
		square1.add(p1);
		JPanel p2 = new JPanel();
		p2.setBorder(new TitledBorder("Applies to:"));
		p2.add(this.appliesToEditor);
		square1.add(p2);
		toReturn.add(square1);
		JPanel p3 = new JPanel();
		p3.setBorder(new TitledBorder("Store to:"));
		this.storeToEditor.setPreferredSize(LIST_PREFERRED_SIZE);
		this.bindingView.setPreferredSize(LIST_PREFERRED_SIZE);
		p3.add(this.storeToEditor);
		toReturn.add(p3);
		toReturn.add(center());
		toReturn.add(bottom());
		return toReturn;
	}

	private JPanel bottom() {
		JPanel p4 = new JPanel(new FlowLayout());
		p4.setBorder(new TitledBorder("Expression"));
		p4.add(this.function);
		this.function.setPreferredSize(REGULAR_FIELD_DIMENSION);
		this.function.addActionListener(this);
		this.expression.setPreferredSize(LONG_FIELD_DIMENSION);
		p4.add(this.expression);
		return p4;
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
		this.conflictStrategy.setSelectedIndex(0);
		this.appliesToEditor.clear();
		this.storeToEditor.clear();
		this.bindingModels.clear();
		this.function.setSelectedIndex(0);
		this.expression.setText("");
		handleChange();
	}

	public FormulaModel getFormulaModel() {
		FormulaModel fm = new FormulaModel();
		if (this.conflictStrategy.getSelectedIndex() > 0) {
			fm.setConflictStrategy(ConflictStrategyFactory
					.getStrategy((String) this.conflictStrategy
							.getSelectedItem()));
		}
		fm.setAppliesTo(this.appliesToEditor.getAppliesTo());
		PropertyChainModel pcm = this.storeToEditor.getPropertyChainModel();
		if (pcm != null) {
			fm.setStorageModel(new StorageModel(pcm));
		} else {
			fm.setStorageModel(null);
		}
		if (this.bindingModels.size() > 0) {
			fm.setBindings(this.bindingModels);
		}
		StringBuilder b = new StringBuilder();
		if (this.function.getSelectedIndex() > 0) {
			b.append(this.function.getSelectedItem());
			b.append("(");
			b.append(this.expression.getText());
			b.append(")");
		} else {
			b.append(this.expression.getText());
		}
		fm.setFormulaBody(b.toString());
		return fm;
	}

	public String getText() {
		return getFormulaModel().render(this.edKit.getOWLModelManager());
	}

	public void initFormula(FormulaModel fm) {
		this.initializing = true;
		ConflictStrategy cf = fm.getConflictStrategy();
		if (cf != null) {
			// select the new item
			this.conflictStrategy.setSelectedItem(cf.toString());
		} else {
			// else select the first item, which is "none" by default
			this.conflictStrategy.setSelectedIndex(0);
		}
		this.appliesToEditor.setAppliesTo((OWLClass) fm.getAppliesTo());
		StorageModel storageModel = fm.getStorageModel();
		if (storageModel != null) {
			this.storeToEditor.setStoreTo(storageModel.getPropertyChainModel());
		} else {
			this.storeToEditor.setStoreTo(null);
		}
		// XXX string bashing: a better way to check whether there is a function
		// call must be implemented
		String fbody = fm.getFormulaBody().trim();
		// get rid of the trailing semicolon ";"
		// fbody = fbody.substring(0, fbody.length() - 1);
		if (fbody.startsWith("SUM(") || fbody.startsWith("sum(")) {
			this.function.setSelectedIndex(1);
			// carve off the first four chars ("SUM(") and the last one (")")
			fbody = fbody.substring(4, fbody.length() - 1);
		} else {
			this.function.setSelectedIndex(0);
		}
		this.expression.setText(fbody);
		this.bindingModels.addAll(fm.getBindings());
		handleChange();
		handleVerification();
		this.initializing = false;
	}

	protected void handleChange() {
		this.bindingViewModel.init();
	}

	protected void handleVerification() {
		if (!this.initializing) {
			boolean status = true;
			String currentFormula = getText();
			this.editor.setText(currentFormula);
			try {
				this.checker.check(currentFormula);
			} catch (Exception e) {
				status = false;
			}
			for (InputVerificationStatusChangedListener v : this.listeners) {
				v.verifiedStatusChanged(status);
			}
		}
	}

	void addBinding() {
		BindingModel b = this.bindingEditor.getBindingModel();
		if (b != null) {
			this.bindingModels.remove(b);
			this.bindingModels.add(b);
			handleChange();
			handleVerification();
		}
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
