/**
 * 
 */
package uk.ac.manchester.cs.lintroll.ui.preference;

import java.awt.Window;
import java.io.File;
import java.util.Collections;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.lint.Lint;

/**
 * @author Luigi Iannone
 * 
 */
public class JarList extends MList {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected OWLEditorKit owlEditorKit;

	/**
	 * @param owlEditorKit
	 */
	public JarList(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
		DefaultListModel model = new DefaultListModel();
		model.addElement(new JarListSectionHeader());
		this.setModel(model);
	}

	@Override
	protected void handleAdd() {
		Window f = (Window) SwingUtilities.getAncestorOfClass(Window.class,
				this);
		File selectedJar = UIUtil.openFile(f, "Select jar", Collections
				.singleton(".jar"));
		if (selectedJar != null) {
			Set<Lint> lints = LintRollPreferences.loadLints(selectedJar
					.getAbsolutePath());
			LintRollPreferences.addAllLoaded(lints);
			DefaultListModel model = (DefaultListModel) this.getModel();
			model.addElement(new JarLListItem(selectedJar.getAbsolutePath()));
		}
	}

	@Override
	protected void handleDelete() {
		super.handleDelete();
		if (this.getSelectedValue() instanceof MListItem) {
			MListItem item = (MListItem) this.getSelectedValue();
			DefaultListModel model = (DefaultListModel) this.getModel();
			model.removeElement(item);
			System.out.println(model.getSize());
		}
	}
}
