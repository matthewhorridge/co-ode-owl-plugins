package org.coode.oppl.protege.ui;

import org.coode.oppl.lint.AbstractParserFactory;
import org.coode.oppl.lint.OPPLLintScript;
import org.coode.oppl.lint.syntax.OPPLLintParser;
import org.coode.oppl.lint.syntax.ParseException;
import org.coode.oppl.lint.syntax.TokenMgrError;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;

public class OPPLLintExpressionChecker implements
		OWLExpressionChecker<OPPLLintScript> {
	private OPPLLintScript lintScript;
	private final OWLEditorKit owlEditorKit;

	/**
	 * @return the owlEditorKit
	 */
	public OWLEditorKit getOwlEditorKit() {
		return this.owlEditorKit;
	}

	/**
	 * @param owlEditorKit
	 */
	public OPPLLintExpressionChecker(OWLEditorKit owlEditorKit) {
		if (owlEditorKit == null) {
			throw new NullPointerException("The OWL editor kit cannot be null");
		}
		this.owlEditorKit = owlEditorKit;
	}

	public void check(String text) throws OWLExpressionParserException {
		this.lintScript = null;
		AbstractParserFactory.getInstance().initParser(text);
		try {
			OPPLLintScript parsedLint = OPPLLintParser.Start();
			this.lintScript = parsedLint;
		} catch (ParseException e) {
			throw new OWLExpressionParserException(e);
		} catch (TokenMgrError e) {
			throw new OWLExpressionParserException(e);
		}
	}

	public OPPLLintScript createObject(String text)
			throws OWLExpressionParserException {
		this.check(text);
		return this.lintScript;
	}
}
