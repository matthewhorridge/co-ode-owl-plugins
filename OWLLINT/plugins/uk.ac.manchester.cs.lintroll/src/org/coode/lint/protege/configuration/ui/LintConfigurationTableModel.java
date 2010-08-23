/**
 * 
 */
package org.coode.lint.protege.configuration.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.semanticweb.owlapi.lint.configuration.LintConfiguration;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public final class LintConfigurationTableModel implements TableModel {
	private LintConfiguration configuration;
	private final Map<LintConfiguration, Properties> changes = new HashMap<LintConfiguration, Properties>();

	/**
	 * @param configuration
	 */
	public LintConfigurationTableModel() {
		this.setConfiguration(NonConfigurableLintConfiguration.getInstance());
	}

	private final Set<TableModelListener> tableModelListeners = new HashSet<TableModelListener>();

	private void notifyListeners() {
		for (TableModelListener l : this.tableModelListeners) {
			l.tableChanged(new TableModelEvent(this));
		}
	}

	/**
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.
	 *      TableModelListener)
	 */
	public void addTableModelListener(TableModelListener l) {
		if (l != null) {
			this.tableModelListeners.add(l);
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int i) {
		String toReturn = "";
		if (i == 0) {
			toReturn = "NAME";
		}
		if (i == 1) {
			toReturn = "VALUE";
		}
		return toReturn;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.getKeys().size();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		String toReturn = "";
		if (rowIndex >= 0 && rowIndex <= this.getRowCount() - 1
				&& columnIndex >= 0 && columnIndex <= 1) {
			List<String> keys = new ArrayList<String>(this.getKeys());
			String key = keys.get(rowIndex);
			String value = this.changes.get(this.configuration)
					.getProperty(key);
			toReturn = columnIndex == 0 ? key : value == null ? "" : value;
		}
		return toReturn;
	}

	/**
	 * @return
	 */
	private Set<String> getKeys() {
		TreeSet<String> toReturn = new TreeSet<String>();
		Set<Object> keySet = this.changes.get(this.configuration).keySet();
		for (Object object : keySet) {
			toReturn.add(object.toString());
		}
		return toReturn;
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 0;
	}

	/**
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event
	 *      .TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener l) {
		this.tableModelListeners.remove(l);
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			String key = this.getValueAt(rowIndex, 0).toString();
			this.setPropertyValue(key, value);
		}
	}

	void setPropertyValue(String key, Object value) {
		Properties properties = this.changes.get(this.configuration);
		if (properties == null) {
			properties = new Properties();
			this.changes.put(this.configuration, properties);
		}
		properties.setProperty(key, value.toString());
		this.notifyListeners();
	}

	public void applyChanges() {
		Set<LintConfiguration> configurations = this.changes.keySet();
		for (LintConfiguration lintConfiguration : configurations) {
			Properties properties = this.changes.get(lintConfiguration);
			Enumeration<?> propertyNames = properties.propertyNames();
			while (propertyNames.hasMoreElements()) {
				String string = propertyNames.nextElement().toString();
				lintConfiguration.setProperty(string, properties
						.getProperty(string));
			}
		}
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(LintConfiguration configuration) {
		if (configuration == null) {
			throw new NullPointerException("The configuration cannnot be null");
		}
		this.configuration = configuration;
		Properties properties = this.changes.get(configuration);
		if (properties == null) {
			Properties initialProperties = new Properties();
			Set<String> propertyKeys = configuration.getPropertyKeys();
			for (String string : propertyKeys) {
				initialProperties.setProperty(string, configuration
						.getPropertyValue(string));
			}
			this.changes.put(configuration, initialProperties);
		}
		this.notifyListeners();
	}
}
