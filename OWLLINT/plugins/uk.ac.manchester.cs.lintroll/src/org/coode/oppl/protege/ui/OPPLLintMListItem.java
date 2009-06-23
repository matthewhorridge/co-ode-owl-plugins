/**
 * 
 */
package org.coode.oppl.protege.ui;

import org.coode.oppl.lint.OPPLLintScript;
import org.protege.editor.core.ui.list.MListItem;

public class OPPLLintMListItem implements MListItem {
	private final OPPLLintScript lint;

	/**
	 * @param lint
	 */
	public OPPLLintMListItem(OPPLLintScript lint) {
		this.lint = lint;
	}

	public String getTooltip() {
		return this.lint.getDescription();
	}

	public boolean handleDelete() {
		return false;
	}

	public void handleEdit() {
		// TODO Auto-generated method stub
	}

	public boolean isDeleteable() {
		return true;
	}

	public boolean isEditable() {
		return true;
	}

	public OPPLLintScript getLint() {
		return this.lint;
	}
}