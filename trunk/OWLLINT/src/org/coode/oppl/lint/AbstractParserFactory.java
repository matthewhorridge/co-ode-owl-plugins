/**
 * 
 */
package org.coode.oppl.lint;

import java.io.StringReader;

import org.coode.oppl.lint.syntax.OPPLLintParser;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class AbstractParserFactory {
	private static OPPLLintParser parser;
	private static AbstractParserFactory instance;

	/**
	 * @return the instance
	 */
	public static AbstractParserFactory getInstance() {
		return instance;
	}

	public static void setAbstractParserFactory(AbstractParserFactory factory) {
		instance = factory;
	}

	public OPPLLintParser initParser(String formulaBody) {
		if (parser == null) {
			parser = new OPPLLintParser(new StringReader(formulaBody));
		} else {
			OPPLLintParser.ReInit(new StringReader(formulaBody));
		}
		this.initOPPLLintFactory();
		return parser;
	}

	protected abstract void initOPPLLintFactory();
}
