package org.coode.oae.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.coode.oae.ui.utils.ExceptionUtils;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;

public class StaticListModel<I> implements ListModel {
	private final static class MySectionHeader implements MListSectionHeader {
		String header;

		@Override
		public String getName() {
			return this.header;
		}

		@Override
		public boolean canAdd() {
			return true;
		}
	}

	private MySectionHeader myHeader = new MySectionHeader();

	public final static class StaticListItem<E> implements MListItem {
		private final E item;

		public E getItem() {
			return this.item;
		}

		public StaticListItem(E b) {
			ExceptionUtils.checkNullArg(b);
			this.item = b;
		}

		@Override
		public String getTooltip() {
			return this.item.toString();
		}

		@Override
		public boolean handleDelete() {
			// TODO figure out how to configure this stuff for general use
			return false;
		}

		@Override
		public void handleEdit() {
		}

		@Override
		public boolean isDeleteable() {
			return false;
		}

		@Override
		public boolean isEditable() {
			return false;
		}
	}

	private final List<Object> delegate = new ArrayList<Object>();
	private final Set<ListDataListener> listeners = new HashSet<ListDataListener>();
	private final Collection<I> modelElements;

	public StaticListModel(Collection<I> elements, String sectionHeader) {
		this.modelElements = elements;
		if (sectionHeader != null) {
			this.myHeader.header = sectionHeader;
		} else {
			this.myHeader = null;
		}
	}

	protected void init() {
		this.delegate.clear();
		// XXX this use of the list is actually not too nice...
		if (this.myHeader != null) {
			this.delegate.add(this.myHeader);
		}
		for (I bm : this.modelElements) {
			this.delegate.add(new StaticListItem<I>(bm));
		}
		notifyListeners();
	}

	protected void notifyListeners() {
		ListDataEvent event = new ListDataEvent(this,
				ListDataEvent.CONTENTS_CHANGED, 0, this.delegate.size() - 1);
		for (ListDataListener l : this.listeners) {
			l.contentsChanged(event);
		}
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		ExceptionUtils.checkNullArg(l);
		this.listeners.add(l);
	}

	@Override
	public Object getElementAt(int index) {
		return this.delegate.get(index);
	}

	@Override
	public int getSize() {
		return this.delegate.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		this.listeners.remove(l);
	}
}
