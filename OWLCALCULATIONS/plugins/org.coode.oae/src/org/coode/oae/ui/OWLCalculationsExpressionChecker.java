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
package org.coode.oae.ui;

import org.coode.oae.utils.ParserFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

import uk.ac.manchester.mae.evaluation.FormulaModel;
import uk.ac.manchester.mae.parser.ArithmeticsParser;
import uk.ac.manchester.mae.parser.MAEStart;
import uk.ac.manchester.mae.parser.ParseException;

/**
 * @author Luigi Iannone
 * 
 *         The University Of Manchester<br>
 *         Bio-Health Informatics Group<br>
 *         Aug 11, 2008
 */
public class OWLCalculationsExpressionChecker implements
		OWLExpressionChecker<FormulaModel> {
	private OWLEditorKit owlEditorKit;

	/**
	 * @param formulaURI
	 * @param owlEditorKit
	 */
	public OWLCalculationsExpressionChecker(OWLEditorKit owlEditorKit) {
		this.owlEditorKit = owlEditorKit;
	}

	/**
	 * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#check(java.lang.String)
	 */
	public void check(String text) throws OWLExpressionParserException {
		createObject(text);
	}

	/**
	 * @see org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker#createObject(java.lang.String)
	 */
	public FormulaModel createObject(String text)
			throws OWLExpressionParserException {
		ParserFactory.initParser(text, this.owlEditorKit.getModelManager());
		try {
			MAEStart formulaNode = (MAEStart) ArithmeticsParser.Start();
			return MAENodeAdapter.toFormulaModel(formulaNode, null,
					this.owlEditorKit);
		} catch (ParseException e) {
			throw new OWLExpressionParserException(e);
		}
	}
}
