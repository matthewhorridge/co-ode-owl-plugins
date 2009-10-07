package org.coode.oae.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.coode.oae.ui.VariableListModel.VariableListItem;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.manchester.mae.evaluation.BindingModel;
import uk.ac.manchester.mae.evaluation.PropertyChainCell;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;

public class StoreToEditor extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3420300348376487738L;

	protected final class MyMList extends MList {
		private static final long serialVersionUID = -6798464125753601852L;

		@Override
		@SuppressWarnings("unchecked")
		protected void handleDelete() {
			StoreToEditor.this.propertyChainCells
					.remove(((VariableListItem<PropertyChainCell>) getSelectedValue())
							.getItem());
			handlePropertyChainsUpdate();
		}

		@Override
		protected List<MListButton> getSectionButtons(MListSectionHeader header) {
			List<MListButton> buttons = new ArrayList<MListButton>();
			if (header.canAdd()) {
				buttons.add(StoreToEditor.this.addObjectProperty);
			}
			return buttons;
		}
	}

	private class MyButton extends MListButton implements ImageObserver {
		private Image image;

		MyButton(String name, Color color, Image i, ActionListener l) {
			super(name, color);
			this.image = i;
			setActionListener(l);
		}

		@Override
		public void paintButtonContent(Graphics2D g) {
			Rectangle r = getBounds();
			g.drawImage(this.image, r.x, r.y, r.width, r.height, this);
		}

		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private final ImageIcon objectPropertyIcon = (ImageIcon) OWLIcons
			.getIcon("property.object.png");
	protected MListButton addObjectProperty = new MyButton(
			"Add Object Property", Color.BLUE, this.objectPropertyIcon
					.getImage(), this);
	private OWLEditorKit kit;
	protected ObjectPropertySelector obp;
	private final MyMList propertychainsView = new MyMList();
	protected final List<PropertyChainCell> propertyChainCells = new ArrayList<PropertyChainCell>();
	protected final VariableListModel<PropertyChainCell> propertychainsModel = new VariableListModel<PropertyChainCell>(
			this.propertyChainCells, "Property chain elements", true);

	public boolean isCorrect() {
		return this.propertyChainCells.size() == 0;
	}

	/**
	 * @param k
	 *            the {@link OWLEditorKit} to use
	 * @param model
	 *            the {@link BindingModel} to use; must not be null but can be
	 *            empty
	 */
	public StoreToEditor(OWLEditorKit k) {
		super(new BorderLayout());
		this.kit = k;
		this.obp = new ObjectPropertySelector(this.kit);
		this.propertychainsView.setModel(this.propertychainsModel);
		this.propertychainsView
				.setCellRenderer(new RenderableObjectCellRenderer(this.kit));
		handlePropertyChainsUpdate();
		JScrollPane jpreport = new JScrollPane(this.propertychainsView);
		jpreport
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpreport.setBorder(new TitledBorder(""));
		this.add(jpreport);
	}

	public void setStoreTo(PropertyChainModel _pcm) {
		this.propertyChainCells.clear();
		PropertyChainModel temp = _pcm;
		while (temp != null) {
			this.propertyChainCells.add(temp.getCell());
			temp = temp.getChild();
		}
		handlePropertyChainsUpdate();
	}

	protected void handlePropertyChainsUpdate() {
		this.propertychainsModel.init();
		for (InputVerificationStatusChangedListener i : this.listeners) {
			i.verifiedStatusChanged(true);
		}
	}

	public PropertyChainModel getPropertyChainModel() {
		if (this.propertyChainCells.size() > 0) {
			return new PropertyChainModel(this.propertyChainCells);
		} else {
			return null;
		}
	}

	protected Set<OWLClass> getOWLClasses(OWLObjectProperty op) {
		Set<OWLClass> ranges = new HashSet<OWLClass>();
		for (OWLDescription d : op.getRanges(this.kit.getOWLModelManager()
				.getActiveOntology())) {
			if (d instanceof OWLClass) {
				ranges.add((OWLClass) d);
			}
		}
		return ranges;
	}

	public int showDialog(String title, JComponent component) {
		return JOptionPaneEx.showValidatingConfirmDialog(getParent(), title,
				component, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null);
	}

	public void actionPerformed(ActionEvent e) {
		int ret = showDialog("Select Object Property and Facet", this.obp);
		if (ret == JOptionPane.OK_OPTION) {
			PropertyChainCell p = this.obp.getCell();
			if (p != null) {
				this.propertyChainCells.add(p);
				handlePropertyChainsUpdate();
			}
		}
		this.obp.clear();
	}

	public void clear() {
		setStoreTo(null);
	}

	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		this.listeners.remove(listener);
	}
}
