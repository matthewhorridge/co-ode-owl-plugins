package org.coode.oae.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxDescriptionParser;
import org.coode.oae.utils.RenderingOWLEntityChecker;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

import uk.ac.manchester.mae.evaluation.PropertyChainCell;
import uk.ac.manchester.mae.evaluation.PropertyChainModel;
import uk.ac.manchester.mae.parser.ArithmeticsParser;
import uk.ac.manchester.mae.parser.MAEpropertyChainCell;
import uk.ac.manchester.mae.parser.ParseException;
import uk.ac.manchester.mae.parser.TokenMgrError;

public class PropertyChainExpressionChecker implements
		OWLExpressionChecker<PropertyChainModel> {
	// final OWLExpressionChecker<List<OWLObjectPropertyExpression>>
	// objectPortionChecker;
	final RenderingOWLEntityChecker r;
	final ManchesterOWLSyntaxDescriptionParser parser;
	final List<PropertyChainCell> cells = new ArrayList<PropertyChainCell>();
	final Set<String> emptySet = Collections.emptySet();
	final boolean objectPropertiesOnly;
	final boolean datatypesAllowed;
	final boolean canBeEmpty;

	public PropertyChainExpressionChecker(OWLEditorKit kit,
			boolean isObjectPropertiesOnly, boolean canBeEmpty) {
		this.r = new RenderingOWLEntityChecker(kit.getModelManager());
		this.parser = new ManchesterOWLSyntaxDescriptionParser(kit
				.getModelManager().getOWLDataFactory(), this.r);
		this.objectPropertiesOnly = isObjectPropertiesOnly;
		this.datatypesAllowed = !this.objectPropertiesOnly;
		this.canBeEmpty = canBeEmpty;
	}

	public void check(String _text) throws OWLExpressionParserException {
		this.cells.clear();
		String text = _text.trim();
		// catches the bogus fake expression used by autocompleter
		if ((text.length() == 0 || text.equals("**")) && !this.canBeEmpty) {
			throw new OWLExpressionParserException("The chain cannot be empty",
					0, 0, false, true, true && this.datatypesAllowed, false,
					false, this.emptySet);
		}
		List<MAEpropertyChainCell> list = checkBasicPropertyChain(_text);
		for (int i = 0; i < list.size() - 1; i++) {
			MAEpropertyChainCell c = list.get(i);
			OWLObjectProperty op = checkObjectProperty(_text, c);
			OWLDescription odfacet = checkFacet(_text, c, c.getFacet());
			this.cells.add(new PropertyChainCell(op, odfacet));
		}
		// now check that the last one is a datatype property
		if (list.size() > 0) {
			MAEpropertyChainCell c = list.get(list.size() - 1);
			if (this.objectPropertiesOnly) {
				OWLObjectProperty op = checkObjectProperty(_text, c);
				OWLDescription odfacet = checkFacet(_text, c, c.getFacet());
				this.cells.add(new PropertyChainCell(op, odfacet));
			} else {
				OWLDataProperty dp = checkDataProperty(_text, c);
				this.cells.add(new PropertyChainCell(dp, null));
			}
		}
	}

	private List<MAEpropertyChainCell> checkBasicPropertyChain(String _text)
			throws OWLExpressionParserException {
		List<MAEpropertyChainCell> list;
		try {
			list = ArithmeticsParser.parsePropertyChain(_text);
		} catch (TokenMgrError e) {
			if (e.getMessage().contains("Encountered: \"[\"")) {
				throw new OWLExpressionParserException(e.getMessage(), 0, _text
						.length(), true, false, false, false, false,
						this.emptySet);
			}
			throw new OWLExpressionParserException(e.getMessage(), 0, _text
					.length(), true, true, true && this.datatypesAllowed,
					false, false, this.emptySet);
		} catch (ParseException e1) {
			if (e1.getMessage().startsWith("Unbalanced brackets")) {
				throw new OWLExpressionParserException(e1.getMessage(), 0,
						_text.length(), true, false, false, false, false,
						this.emptySet);
			}
			throw new OWLExpressionParserException(e1.getMessage(), 0, _text
					.length(), false, true, true && this.datatypesAllowed,
					false, false, this.emptySet);
		}
		return list;
	}

	private OWLObjectProperty checkObjectProperty(String _text,
			MAEpropertyChainCell c) throws OWLExpressionParserException {
		if (c.getPropertyName().isEmpty()) {
			throw new OWLExpressionParserException(
					"The chain is not complete: missing objectproperty", _text
							.length(), _text.length(), false, true, false,
					false, false, this.emptySet);
		} else {
			OWLObjectProperty op = this.r.getOWLObjectProperty(c
					.getPropertyName());
			if (op == null) {
				String s = c.getPropertyName();
				int i1 = _text.indexOf(s);
				int i2 = _text.indexOf(s) + s.length();
				throw new OWLExpressionParserException(
						"The current property is not an OWLObjectProperty: "
								+ s, i1, i2, false, true, false, false, false,
						this.emptySet);
			}
			return op;
		}
	}

	private OWLDescription checkFacet(String _text, MAEpropertyChainCell c,
			String facet) throws OWLExpressionParserException {
		OWLDescription odfacet = null;
		if (facet != null) {
			try {
				odfacet = this.parser.parse(facet);
			} catch (ParserException e) {
				String s = c.getContent();
				int i1 = _text.indexOf(s);
				int i2 = _text.indexOf(s) + s.length();
				throw new OWLExpressionParserException(
						"The current facet is not a valid Manchester syntax class expression: "
								+ s, i1, i2, true, false, false, false, false,
						this.emptySet);
			}
		}
		return odfacet;
	}

	private OWLDataProperty checkDataProperty(String _text,
			MAEpropertyChainCell c) throws OWLExpressionParserException {
		if (c.getPropertyName().isEmpty()) {
			throw new OWLExpressionParserException(
					"The chain is not complete: missing dataproperty", _text
							.length(), _text.length(), false, true, true,
					false, false, this.emptySet);
		} else {
			OWLDataProperty dp = this.r.getOWLDataProperty(c.getPropertyName());
			if (dp == null) {
				String s = c.getContent();
				int i1 = _text.indexOf(s);
				int i2 = _text.indexOf(s) + s.length();
				throw new OWLExpressionParserException(
						"The current property is not an OWLDataProperty: " + s,
						i1, i2, false, false, true, false, false, this.emptySet);
			}
			return dp;
		}
	}

	public PropertyChainModel createObject(String text)
			throws OWLExpressionParserException {
		check(text);
		return new PropertyChainModel(this.cells);
	}
}