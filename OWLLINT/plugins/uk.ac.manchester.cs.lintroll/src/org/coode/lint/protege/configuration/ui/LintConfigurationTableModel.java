/**
 * 
 */
package org.coode.lint.protege.configuration.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.semanticweb.owl.lint.configuration.LintConfiguration;

import uk.ac.manchester.cs.owl.lint.commons.NonConfigurableLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public class LintConfigurationTableModel implements TableModel {
	private LintConfiguration configuration;

	/**
	 * @param configuration
	 */
	public LintConfigurationTableModel() {
		this.configuration = NonConfigurableLintConfiguration.getInstance();
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
		return this.configuration.getPropertyKeys().size();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		String toReturn = "";
		if (rowIndex >= 0 && rowIndex <= this.getRowCount() - 1 && columnIndex >= 0
				&& columnIndex <= 1) {
			List<String> keys = new ArrayList<String>(new TreeSet<String>(
					this.configuration.getPropertyKeys()));
			String key = keys.get(rowIndex);
			String value = this.configuration.getPropertyValue(key);
			toReturn = columnIndex == 0 ? key : value == null ? "" : value;
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
			this.configuration.setProperty(key, value.toString());
			this.notifyListeners();
		}
	}

	/**
	 * @return the configuration
	 */
	public LintConfiguration getConfiguration() {
		return this.configuration;
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
		this.notifyListeners();
	}
}
