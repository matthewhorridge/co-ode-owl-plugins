package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
		RefreshableComponent, VerifiedInputEditor, DocumentListener {
	protected final class MyMList extends MList {
		@Override
		protected void handleAdd() {
			GraphicalFormulaEditor.this.bindingEditor.setBindingModel(null);
			GraphicalFormulaEditor.this.bindingEditor.setVisible(true);
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
			GraphicalFormulaEditor.this.bindingEditor.setVisible(true);
		}
	}

	static ImageIcon addIcon = new ImageIcon(GraphicalFormulaEditor.class
			.getClassLoader().getResource("add.png"));
	static ImageIcon delIcon = new ImageIcon(GraphicalFormulaEditor.class
			.getClassLoader().getResource("delete.png"));
	static Dimension REGULAR_FIELD_DIMENSION = new Dimension(150, 25);
	static Dimension LONG_FIELD_DIMENSION = new Dimension(300, 25);
	static Dimension SHORT_FIELD_DIMENSION = new Dimension(75, 25);
	private static String[] strategies = new String[] { "none",
			OverridingStrategy.getInstance().toString(),
			OverriddenStrategy.getInstance().toString(),
			ExceptionStrategy.getInstance().toString() };
	private static String[] functions = new String[] { "none", "SUM" };
	private JComboBox conflictStrategy = new JComboBox(strategies);
	private JComboBox function = new JComboBox(functions);
	private JTextField expression = new JTextField();
	private JTextField appliesTo = new JTextField();
	private PropertyChainModel storeTo = null;
	private StoreToEditor storeToEditor;
	private OWLClass appliesToValue = null;
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
		this.bindingEditor = new BindingEditor(this.edKit, this);
		this.storeToEditor = new StoreToEditor(this.edKit, this);
		this.appliesToEditor = new AppliesToEditor(this.edKit, this);
		this.bindingEditor.setVisible(false);
		this.bindingView.setPreferredSize(new Dimension(360, 100));
		this.bindingView.setModel(this.bindingViewModel);
		this.bindingView.setCellRenderer(new RenderableObjectCellRenderer(
				this.edKit));
		this.expression.getDocument().addDocumentListener(this);
		this.conflictStrategy.setPreferredSize(REGULAR_FIELD_DIMENSION);
		this.appliesTo.setPreferredSize(REGULAR_FIELD_DIMENSION);
		setLayout(new BorderLayout());
		this.add(this.editor, BorderLayout.NORTH);
		this.add(leftside(), BorderLayout.WEST);
		this.add(center(), BorderLayout.CENTER);
		this.add(bottom(), BorderLayout.SOUTH);
		this.bindingViewModel.init();
	}

	private Component center() {
		JPanel toReturn = new JPanel(new BorderLayout());
		toReturn.setBorder(new TitledBorder("Bindings:"));
		JScrollPane center = new JScrollPane(this.bindingView);
		// center.setPreferredSize(new Dimension(250, 300));
		center
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane east = new JScrollPane(this.bindingEditor);
		// east.setPreferredSize(new Dimension(250, 300));
		east
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		toReturn.add(center, BorderLayout.NORTH);
		toReturn.add(east, BorderLayout.CENTER);
		return toReturn;
	}

	private JPanel leftside() {
		JPanel toReturn = new JPanel(new BorderLayout());
		// mainleft.setPreferredSize(new Dimension(180, 500));
		JPanel p1 = new JPanel();
		p1.setBorder(new TitledBorder("Conflict Strategy"));
		p1.add(this.conflictStrategy);
		toReturn.add(p1, BorderLayout.NORTH);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setBorder(new TitledBorder("Applies to:"));
		p2.add(this.appliesTo, BorderLayout.NORTH);
		p2.add(this.appliesToEditor, BorderLayout.CENTER);
		toReturn.add(p2, BorderLayout.CENTER);
		JPanel p3 = new JPanel();
		p3.setBorder(new TitledBorder("Store to:"));
		p3.add(this.storeToEditor);
		toReturn.add(p3, BorderLayout.SOUTH);
		return toReturn;
	}

	private JPanel bottom() {
		JPanel p4 = new JPanel(new FlowLayout());
		p4.setBorder(new TitledBorder("Expression"));
		p4.add(this.function);
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
		this.appliesToValue = null;
		this.appliesTo.setText("");
		this.storeTo = null;
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
		fm.setAppliesTo(this.appliesToValue);
		if (this.storeTo != null) {
			fm.setStorageModel(new StorageModel(this.storeTo));
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
		this.appliesToValue = (OWLClass) fm.getAppliesTo();
		updateAppliesTo();
		StorageModel storageModel = fm.getStorageModel();
		if (storageModel != null) {
			this.storeTo = storageModel.getPropertyChainModel();
		} else {
			this.storeTo = null;
		}
		this.storeToEditor.setStoreTo(this.storeTo);
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

	void handleCommit() {
		BindingModel b = this.bindingEditor.getBindingModel();
		this.bindingModels.remove(b);
		this.bindingModels.add(b);
		this.bindingEditor.setVisible(false);
		handleChange();
		handleVerification();
	}

	void handleStoreToCommit() {
		this.storeTo = this.storeToEditor.getPropertyChainModel();
		handleVerification();
	}

	void handleAppliesToCommit() {
		this.appliesToValue = this.appliesToEditor.getAppliesTo();
		updateAppliesTo();
		handleVerification();
	}

	private void updateAppliesTo() {
		if (this.appliesToValue != null) {
			this.appliesTo.setText(this.edKit.getOWLModelManager()
					.getRendering(this.appliesToValue));
		} else {
			this.appliesTo.setText("");
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
}
