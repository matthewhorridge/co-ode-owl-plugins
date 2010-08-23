/**
 * 
 */
package org.coode.lint.protege.configuration.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.coode.lint.protege.ProtegeLintManager;
import org.coode.lint.protege.ProtegeLintManager.LintLoadListener;
import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.lint.Lint;
import org.semanticweb.owlapi.lint.configuration.LintConfiguration;
import org.semanticweb.owlapi.lint.configuration.LintConfigurationVisitorAdapter;
import org.semanticweb.owlapi.lint.configuration.PropertyBasedLintConfiguration;

/**
 * @author Luigi Iannone
 * 
 */
public final class LoadedConfigurableLintListModel implements ListModel, Disposable {
	private final OWLEditorKit owlEditorKit;
	private final Set<ListDataListener> listDataListeners = new HashSet<ListDataListener>();
	private final List<Lint<?>> configurableLints = new ArrayList<Lint<?>>();
	private final LintLoadListener loadListener = new LintLoadListener() {
		public void loadChanged() {
			LoadedConfigurableLintListModel.this.reset();
		}
	};

	/**
	 * @param owlEditorKit
	 */
	public LoadedConfigurableLintListModel(OWLEditorKit owlEditorKit) {
		if (owlEditorKit == null) {
			throw new NullPointerException("The OWL Editor kit cannot be null");
		}
		this.owlEditorKit = owlEditorKit;
		ProtegeLintManager.getInstance(this.getOWLEditorKit()).addLintLoadListener(
				this.loadListener);
		this.reset();
	}

	private void reset() {
		this.configurableLints.clear();
		Set<Lint<?>> loadedLints = ProtegeLintManager.getInstance(this.getOWLEditorKit()).getLoadedLints();
		for (final Lint<?> lint : loadedLints) {
			LintConfiguration lintConfiguration = lint.getLintConfiguration();
			lintConfiguration.accept(new LintConfigurationVisitorAdapter() {
				@Override
				public void visitPropertiesBasedLintConfiguration(
						PropertyBasedLintConfiguration propertiesBasedLintConfiguration) {
					LoadedConfigurableLintListModel.this.configurableLints.add(lint);
				}
			});
		}
		this.notifyListeners();
	}

	/**
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener
	 *      )
	 */
	public void addListDataListener(ListDataListener l) {
		if (l != null) {
			this.listDataListeners.add(l);
		}
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int i) {
		return this.configurableLints.get(i);
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return this.configurableLints.size();
	}

	/**
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.
	 *      ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		this.listDataListeners.remove(l);
	}

	/**
	 * @return the owlEditorKit
	 */
	public OWLEditorKit getOWLEditorKit() {
		return this.owlEditorKit;
	}

	private void notifyListeners() {
		for (ListDataListener l : this.listDataListeners) {
			l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0,
					this.configurableLints.size() - 1));
		}
	}

	public void dispose() throws Exception {
		ProtegeLintManager.getInstance(this.getOWLEditorKit()).removeLintLoadListener(
				this.loadListener);
	}
}
