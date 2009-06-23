/**
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package uk.ac.manchester.cs.lintroll.ui.preference;

import java.io.File;

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owl.lint.Lint;

/**
 * @author Luigi Iannone
 * 
 */
public class JarLListItem implements MListItem {
	protected String jarName;

	/**
	 * @param jarName
	 */
	public JarLListItem(String jarName) {
		this.jarName = jarName;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#getTooltip()
	 */
	public String getTooltip() {
		return this.jarName;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#handleDelete()
	 */
	public boolean handleDelete() {
		LintRollPreferences.removeJar(this.jarName);
		return true;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#handleEdit()
	 */
	public void handleEdit() {
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#isDeleteable()
	 */
	public boolean isDeleteable() {
		return true;
	}

	/**
	 * @see org.protege.editor.core.ui.list.MListItem#isEditable()
	 */
	public boolean isEditable() {
		return false;
	}

	@Override
	public String toString() {
		String toReturn = "";
		String shortName = this.jarName.indexOf(File.separator) != -1 ? this.jarName
				.substring(this.jarName.lastIndexOf(File.separator) + 1)
				: this.jarName;
		toReturn += shortName + ": {";
		boolean first = true;
		for (Lint lint : LintRollPreferences.getLoadedLints()) {
			if (this.jarName.compareTo(LintRollPreferences.getJarName(lint)) == 0) {
				if (!first) {
					toReturn += ", ";
				}
				toReturn += lint.getName();
				first = false;
			}
		}
		toReturn += "}";
		return toReturn;
	}

	public String getJarName() {
		return this.jarName;
	}
}
