/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Luigi Iannone
 * 
 * http://www.cs.man.ac.uk/~iannonel
 * 
 * The University Of Manchester Bio Health Informatics Group Date: February 11,
 * 2008
 * 
 */
// TODO: manage duplicates
public class SelectLists<T> extends JPanel implements ListDataListener,
		ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -268139023500715960L;
	public static final String SELECTED_NOT_EMPTY = "selected not empty";
	public static final String AVAILABLE_SELECTION_CHANGE_EVENT = "available selection changed";
	JLabel availableLabel, selectedLabel;
	JButton addButton, removeButton, addAllButton, removeAllButton;
	JList availableItems, selectedItems;
	DefaultListModel selectedListModel = new DefaultListModel();
	DefaultListModel availableListModel = new DefaultListModel();
	private boolean isSelectedEmpty = true;

	public SelectLists(String firstLabel, String secondLabel,
			Collection<? extends T> options) {
		super();
		this.init(firstLabel, secondLabel, ">", ">>", "<", "<<", options);
	}

	public SelectLists(String firstLabel, String secondLabel,
			String addButtonCaption, String addAllButtonCaption,
			String removeButtonCaption, String removeAllButtonCaption,
			Collection<? extends T> options) {
		super();
		this.init(firstLabel, secondLabel, addButtonCaption,
				addAllButtonCaption, removeButtonCaption,
				removeAllButtonCaption, options);
	}

	private void init(String firstLabel, String secondLabel,
			String addButtonCaption, String addAllButtonCaption,
			String removeButtonCaption, String removeAllButtonCaption,
			Collection<? extends T> options) {
		this.setSize(new Dimension(100, 50));
		this.setLayout(new GridLayout(0, 3));
		this.initCaptions(firstLabel, secondLabel, addButtonCaption,
				addAllButtonCaption, removeButtonCaption,
				removeAllButtonCaption);
		this.initLists(options);
		this.initButtons();
		this.buildPanel();
	}

	private void registerListListeners() {
		this.availableListModel.addListDataListener(this);
		this.selectedListModel.addListDataListener(this);
		this.availableItems.addListSelectionListener(this);
	}

	/**
	 * 
	 */
	private void initButtons() {
		this.initButtonsActions();
		this.workOutButtonEnabling();
	}

	/**
	 * 
	 */
	private void initButtonsActions() {
		this.addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int[] selectedItemsIndices = SelectLists.this.availableItems
						.getSelectedIndices();
				if (selectedItemsIndices.length > 0) {
					for (int i : selectedItemsIndices) {
						Object item = SelectLists.this.availableListModel
								.get(i);
						SelectLists.this.availableListModel.remove(i);
						SelectLists.this.selectedListModel.addElement(item);
					}
				}
			}
		});
		this.removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int[] selectedItemsIndices = SelectLists.this.selectedItems
						.getSelectedIndices();
				if (selectedItemsIndices.length > 0) {
					for (int i : selectedItemsIndices) {
						Object item = SelectLists.this.selectedListModel.get(i);
						SelectLists.this.selectedListModel.remove(i);
						SelectLists.this.availableListModel.addElement(item);
					}
				}
			}
		});
		this.addAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Enumeration<? extends Object> availables = SelectLists.this.availableListModel
						.elements();
				while (availables.hasMoreElements()) {
					SelectLists.this.selectedListModel.addElement(availables
							.nextElement());
				}
				SelectLists.this.availableListModel.clear();
			}
		});
		this.removeAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Enumeration<? extends Object> availables = SelectLists.this.selectedListModel
						.elements();
				while (availables.hasMoreElements()) {
					SelectLists.this.availableListModel.addElement(availables
							.nextElement());
				}
				SelectLists.this.selectedListModel.clear();
			}
		});
	}

	/**
	 * 
	 */
	private void buildPanel() {
		JPanel availablePanel = new JPanel(new BorderLayout());
		availablePanel.add(this.availableLabel, BorderLayout.NORTH);
		availablePanel.add(new JScrollPane(this.availableItems),
				BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
		buttonPanel.add(this.addAllButton);
		buttonPanel.add(this.addButton);
		buttonPanel.add(this.removeButton);
		buttonPanel.add(this.removeAllButton);
		JPanel selectedPanel = new JPanel(new BorderLayout());
		selectedPanel.add(this.selectedLabel, BorderLayout.NORTH);
		selectedPanel.add(new JScrollPane(this.selectedItems),
				BorderLayout.CENTER);
		this.add(availablePanel);
		this.add(buttonPanel);
		this.add(selectedPanel);
	}

	/**
	 * @param firstLabel
	 * @param secondLabel
	 * @param addButtonCaption
	 * @param addAllButtonCaption
	 * @param removeButtonCaption
	 * @param removeAllButtonCaption
	 */
	private void initCaptions(String firstLabel, String secondLabel,
			String addButtonCaption, String addAllButtonCaption,
			String removeButtonCaption, String removeAllButtonCaption) {
		this.availableLabel = new JLabel(firstLabel);
		this.selectedLabel = new JLabel(secondLabel);
		this.addButton = new JButton(addButtonCaption);
		this.addAllButton = new JButton(addAllButtonCaption);
		this.removeButton = new JButton(removeButtonCaption);
		this.removeButton = new JButton(removeButtonCaption);
		this.removeAllButton = new JButton(removeAllButtonCaption);
	}

	/**
	 * @param options
	 */
	private void initLists(Collection<? extends T> options) {
		for (T option : options) {
			this.availableListModel.addElement(option);
		}
		this.availableItems = new JList(this.availableListModel);
		this.selectedItems = new JList(this.selectedListModel);
		this.isSelectedEmpty = this.selectedListModel.size() == 0;
		this.registerListListeners();
	}

	public void addAllAvailable(Set<? extends T> options) {
		for (T option : options) {
			this.availableListModel.addElement(option);
		}
	}

	public void addAvailable(T option) {
		this.availableListModel.addElement(option);
	}

	public Set<T> getAvailableItems() {
		Set<T> toReturn = new HashSet<T>(this.availableListModel.capacity());
		this.fill(toReturn, this.availableListModel);
		return toReturn;
	}

	public Set<T> getSelectedItems() {
		Set<T> toReturn = new HashSet<T>(this.selectedListModel.capacity());
		this.fill(toReturn, this.selectedListModel);
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	private void fill(Set<T> toBeFilled, DefaultListModel listModel) {
		Enumeration e = listModel.elements();
		while (e.hasMoreElements()) {
			toBeFilled.add((T) e.nextElement());
		}
	}

	private void workOutButtonEnabling() {
		this.addAllButton.setEnabled(!this.availableListModel.isEmpty());
		this.removeAllButton.setEnabled(!this.selectedListModel.isEmpty());
		this.removeButton.setEnabled(!this.selectedListModel.isEmpty());
		this.addButton.setEnabled(!this.availableListModel.isEmpty());
	}

	public void contentsChanged(ListDataEvent lde) {
		this.workOutButtonEnabling();
		if (lde.getSource().equals(this.selectedListModel)) {
			this.firePropertyChange(SELECTED_NOT_EMPTY, !this.isSelectedEmpty,
					this.selectedListModel.size() > 0);
			this.isSelectedEmpty = this.selectedListModel.size() == 0;
		}
	}

	public void intervalAdded(ListDataEvent lde) {
		this.workOutButtonEnabling();
		if (lde.getSource().equals(this.selectedListModel)) {
			this.firePropertyChange(SELECTED_NOT_EMPTY, !this.isSelectedEmpty,
					this.selectedListModel.size() > 0);
			this.isSelectedEmpty = this.selectedListModel.size() == 0;
		}
	}

	public void intervalRemoved(ListDataEvent lde) {
		this.workOutButtonEnabling();
		if (lde.getSource().equals(this.selectedListModel)) {
			this.firePropertyChange(SELECTED_NOT_EMPTY, !this.isSelectedEmpty,
					this.selectedListModel.size() > 0);
			this.isSelectedEmpty = this.selectedListModel.size() == 0;
		}
	}

	/**
	 * Sets the same {@link ListCellRenderer} to both JLists
	 * 
	 * @param lcr
	 */
	public void setCellRenderer(ListCellRenderer lcr) {
		this.availableItems.setCellRenderer(lcr);
		this.selectedItems.setCellRenderer(lcr);
	}

	public Object[] getAvailableSelected() {
		return this.availableItems.getSelectedValues();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(this.availableItems)) {
			this.firePropertyChange(
					SelectLists.AVAILABLE_SELECTION_CHANGE_EVENT, false, true);
		}
	}
}
