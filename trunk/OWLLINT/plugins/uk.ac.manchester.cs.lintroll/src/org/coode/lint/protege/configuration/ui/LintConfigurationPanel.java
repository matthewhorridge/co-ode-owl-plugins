/**
 * 
 */
package org.coode.lint.protege.configuration.ui;

import java.awt.Component;
import java.io.IOException;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.coode.lint.protege.ProtegeLintManager;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.configuration.LintConfigurationVisitorAdapter;

import uk.ac.manchester.cs.owl.lint.commons.AbstractPropertiesBasedLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public class LintConfigurationPanel extends OWLPreferencesPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7566787120062925525L;
	private LoadedConfigurableLintListModel listModel;
	private JList loadedConfigurableLintList = new JList();
	private final JPanel mainPanel = new JPanel();
	private final LintConfigurationTableModel lintConfigurationTableModel = new LintConfigurationTableModel();
	private final JTable lintConfigurationTable = new JTable(this.lintConfigurationTableModel);
	private final ListSelectionListener lintSelectionListener = new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent lse) {
			if (!lse.getValueIsAdjusting()) {
				Object selectedValue = LintConfigurationPanel.this.loadedConfigurableLintList.getSelectedValue();
				if (selectedValue instanceof Lint<?>) {
					LintConfigurationPanel.this.lintConfigurationTableModel.setConfiguration(((Lint<?>) selectedValue).getLintConfiguration());
				}
			}
		}
	};

	public void initialise() throws Exception {
		this.listModel = new LoadedConfigurableLintListModel(this.getOWLEditorKit());
		this.loadedConfigurableLintList.setModel(this.listModel);
		this.loadedConfigurableLintList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.loadedConfigurableLintList.addListSelectionListener(this.lintSelectionListener);
		this.initGUI();
	}

	private void initGUI() {
		this.loadedConfigurableLintList.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				DefaultListCellRenderer renderer = new DefaultListCellRenderer();
				return renderer.getListCellRendererComponent(
						list,
						value instanceof Lint<?> ? ((Lint<?>) value).getName() : value.toString(),
						index,
						isSelected,
						cellHasFocus);
			}
		});
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(.5);
		splitPane.setResizeWeight(.5);
		JScrollPane loadedConfigurableLintPanel = ComponentFactory.createScrollPane(this.loadedConfigurableLintList);
		loadedConfigurableLintPanel.setBorder(ComponentFactory.createTitledBorder("Configurable Lint Checks"));
		JScrollPane lintConfigurationTableScrollPane = ComponentFactory.createScrollPane(this.lintConfigurationTable);
		lintConfigurationTableScrollPane.setBorder(ComponentFactory.createTitledBorder("Configuration"));
		splitPane.setTopComponent(loadedConfigurableLintPanel);
		splitPane.setBottomComponent(lintConfigurationTableScrollPane);
		this.mainPanel.add(splitPane);
		this.add(this.mainPanel);
	}

	public void dispose() throws Exception {
		if (this.listModel != null) {
			this.listModel.dispose();
		}
	}

	@Override
	public void applyChanges() {
		Set<Lint<?>> loadedLints = ProtegeLintManager.getInstance(this.getOWLEditorKit()).getLoadedLints();
		for (Lint<?> lint : loadedLints) {
			lint.getLintConfiguration().accept(new LintConfigurationVisitorAdapter() {
				@Override
				public void visitPropertiesBasedLintConfiguration(
						AbstractPropertiesBasedLintConfiguration propertiesBasedLintConfiguration) {
					try {
						propertiesBasedLintConfiguration.store();
					} catch (IOException e) {
						ProtegeApplication.getErrorLog().logError(e);
					}
				}
			});
		}
	}
}
