package org.coode.oppl.protege.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.coode.oppl.lint.OPPLLintScript;
import org.coode.oppl.lint.ParserFactory;
import org.coode.oppl.lint.syntax.OPPLLintParser;
import org.coode.oppl.lint.syntax.ParseException;

public class TextFileOPPLLintRepository implements OPPLLintRepository {
	private static final String FILENAME = "opplLints.txt";
	private final Set<OPPLLintScript> opplLintScripts = new HashSet<OPPLLintScript>();

	public TextFileOPPLLintRepository() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(
				FILENAME);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(in));
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				ParserFactory.initParser(line);
				OPPLLintScript opplLintScript = OPPLLintParser.Start();
				this.opplLintScripts.add(opplLintScript);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Set<OPPLLintScript> getOPPLLintScripts() {
		return this.opplLintScripts;
	}
}
