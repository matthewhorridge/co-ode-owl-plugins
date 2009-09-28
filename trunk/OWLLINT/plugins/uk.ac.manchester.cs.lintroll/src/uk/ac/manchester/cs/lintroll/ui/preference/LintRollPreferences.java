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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coode.oppl.lint.AbstractParserFactory;
import org.coode.oppl.lint.OPPLLintScript;
import org.coode.oppl.lint.syntax.OPPLLintParser;
import org.coode.oppl.lint.syntax.ParseException;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.PatternBasedLint;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.NamespaceUtil;

import uk.ac.manchester.cs.lintroll.utils.JarClassLoader;
import uk.ac.manchester.cs.lintroll.utils.JarResources;

/**
 * @author Luigi Iannone
 * 
 */
public class LintRollPreferences {
	private static final String LOADED_JARS_PREF_NAME = "loadedJars";
	private static final String OPPL_LINTS_PREF_NAME = "opplLints";

	private static class OPPLLintManager implements OWLOntologyChangeListener {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			for (OWLOntologyChange ontologyChange : changes) {
				if (ontologyChange.isAxiomChange()) {
					OWLAxiomChange axiomChange = (OWLAxiomChange) ontologyChange;
					OWLAxiom axiom = axiomChange.getAxiom();
					if (OWLOntologyAnnotationAxiom.class.isAssignableFrom(axiom
							.getClass())
							&& this.check(((OWLOntologyAnnotationAxiom) axiom))) {
						String lintString = ((OWLOntologyAnnotationAxiom) axiom)
								.getAnnotation().getAnnotationValueAsConstant()
								.getLiteral();
						AbstractParserFactory.getInstance().initParser(
								lintString);
						try {
							OPPLLintScript lint = OPPLLintParser.Start();
							if (AddAxiom.class.isAssignableFrom(axiomChange
									.getClass())) {
								addLoadedLint(lint);
							} else {
								removeLoadedLint(lint);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private boolean check(OWLOntologyAnnotationAxiom ontologyAnnotationAxiom) {
			OWLAnnotation<? extends OWLObject> annotation = ontologyAnnotationAxiom
					.getAnnotation();
			URI annotationURI = annotation.getAnnotationURI();
			String annotationURIString = annotationURI.toString();
			NamespaceUtil nsUtil = new NamespaceUtil();
			String[] split = nsUtil.split(annotationURIString, null);
			return split != null
					&& split.length == 2
					&& split[0]
							.compareToIgnoreCase(OPPL_LINT_NAMESPACE_URI_STRING) == 0;
		}
	}

	public static final String OPPL_LINT_NAMESPACE_URI_STRING = "http://www.co-orde.org/lints/oppl#";
	private static Set<Lint> loadedLints;
	private static Set<Lint> selectedLints;
	private static PreferencesManager preferencesManager;
	static Set<String> wellKnownLintClassNames;
	private static Map<Lint, String> lintJarMap = new HashMap<Lint, String>();
	private static Set<LintRollPreferenceChangeListener> listeners = new HashSet<LintRollPreferenceChangeListener>();
	private static Set<String> loadedJars;
	private static OWLOntologyManager ontologyManager = OWLManager
			.createOWLOntologyManager();
	private static List<String> invalidJars;
	private static List<String> loadedJarPrefList;
	private static OPPLLintManager opplLintManager;
	private static List<String> personalOPPLLints;
	private static Preferences prefs;
	public static final URI OPPL_LINT_NAMESPACE_URI = URI
			.create(OPPL_LINT_NAMESPACE_URI_STRING);
	static {
		LintRollPreferences.wellKnownLintClassNames = new HashSet<String>();
		LintRollPreferences.wellKnownLintClassNames.add(Lint.class.getName());
		LintRollPreferences.wellKnownLintClassNames.add(PatternBasedLint.class
				.getName());
		loadedLints = new HashSet<Lint>();
		selectedLints = new HashSet<Lint>();
		invalidJars = new ArrayList<String>();
		preferencesManager = PreferencesManager.getInstance();
		opplLintManager = new OPPLLintManager();
		initPreferences();
	}

	public static Set<Lint> getLoadedLints() {
		return new HashSet<Lint>(loadedLints);
	}

	/**
	 * Loading preferences from the stored ones
	 */
	private static void initPreferences() {
		prefs = preferencesManager.getPreferencesForSet("LINTROLL",
				"panelPrefs");
		loadedJarPrefList = prefs.getStringList(
				LintRollPreferences.LOADED_JARS_PREF_NAME,
				new ArrayList<String>());
		loadedJars = new HashSet<String>(loadedJarPrefList);
		// Just to avoid duplicates
		for (String loadedString : loadedJars) {
			System.out.println("Loading jar " + loadedString);
			Set<Lint> jarLoadedLints = loadLints(loadedString);
			for (Lint lint : jarLoadedLints) {
				addLoadedLint(loadedString, lint);
			}
		}
		personalOPPLLints = prefs.getStringList(
				LintRollPreferences.OPPL_LINTS_PREF_NAME,
				new ArrayList<String>());
		// Set<Lint> opplLoadedLint = loadOPPLLint();
		// for (Lint lint : opplLoadedLint) {
		// addLoadedLint(lint);
		// }
	}

	// private static Set<Lint> loadOPPLLint() {
	// // OPPLLintRepository repository = getOPPLLintRepository();
	// // Set<OPPLLintScript> lintScripts = repository.getOPPLLintScripts();
	// // Set<Lint> toReturn = new HashSet<Lint>(lintScripts);
	// // // toReturn.addAll(getPersonalLints());
	// // return toReturn;
	// }
	// public static Collection<? extends OPPLLintScript> getPersonalLints() {
	// Set<OPPLLintScript> toReturn = new HashSet<OPPLLintScript>();
	// for (String personalLintString : personalOPPLLints) {
	// ParserFactory.initParser(personalLintString);
	// try {
	// OPPLLintScript personalLint = OPPLLintParser.Start();
	// toReturn.add(personalLint);
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// }
	// return toReturn;
	// }
	// public static OPPLLintRepository getOPPLLintRepository() {
	// return new TextFileOPPLLintRepository();
	// }
	/**
	 * @return the selectedLints
	 */
	public static Set<Lint> getSelectedLints() {
		return new HashSet<Lint>(selectedLints);
	}

	public static void addSelectedLint(Lint lint) {
		boolean changed = selectedLints.add(lint);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints, EventType.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void removeSelectedLint(Lint lint) {
		boolean changed = selectedLints.remove(lint);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints, EventType.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void addLoadedLint(Lint lint) {
		boolean changed = loadedLints.add(lint);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints, EventType.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void addLoadedLint(String jarPath, Lint lint) {
		boolean changed = loadedLints.add(lint);
		lintJarMap.put(lint, jarPath);
		if (changed) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints, EventType.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void removeLoadedLint(Lint lint) {
		boolean changed = loadedLints.remove(lint);
		lintJarMap.remove(lint);
		if (changed) {
			selectedLints.remove(lint);
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints, EventType.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void clearSelected() {
		selectedLints.clear();
		for (LintRollPreferenceChangeListener listener : listeners) {
			listener.handleChange(new LintRollPreferenceChangeEvent(
					selectedLints, EventType.SELECTED_LINT_CHANGE));
		}
	}

	public static void addAllSelected(Collection<? extends Lint> lints) {
		boolean change = selectedLints.addAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints, EventType.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void addAllLoaded(Collection<? extends Lint> lints) {
		boolean change = loadedLints.addAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints, EventType.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void removeAllSelected(Collection<? extends Lint> lints) {
		boolean change = selectedLints.removeAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						selectedLints, EventType.SELECTED_LINT_CHANGE));
			}
		}
	}

	public static void removeAllLoaded(Collection<? extends Lint> lints) {
		boolean change = loadedLints.removeAll(lints);
		if (change) {
			for (LintRollPreferenceChangeListener listener : listeners) {
				listener.handleChange(new LintRollPreferenceChangeEvent(
						loadedLints, EventType.LOADED_LINT_CHANGE));
			}
		}
	}

	public static void addLintRollPreferenceChangeListener(
			LintRollPreferenceChangeListener listener) {
		listeners.add(listener);
	}

	public static Set<Lint> loadLints(String jarName) {
		Set<String> classNames = new HashSet<String>();
		Set<Lint> toReturn = new HashSet<Lint>();
		Class<? extends Object> clazz = null;
		try {
			JarClassLoader cl = new JarClassLoader(jarName);
			classNames = cl.getClassNames();
			URLClassLoader urlClassLoader = new URLClassLoader(
					new URL[] { new File(jarName).toURI().toURL() },
					LintRollPreferences.class.getClassLoader());
			for (String string : new HashSet<String>(classNames)) {
				try {
					clazz = urlClassLoader.loadClass(string);
					if (!wellKnownLintClassNames.contains(string)
							&& Lint.class.isAssignableFrom(clazz)
							&& !Modifier.isAbstract(clazz.getModifiers())) {
						Constructor<? extends Object> constructor = clazz
								.getConstructor(OWLOntologyManager.class);
						Lint loadedLint = (Lint) constructor
								.newInstance(ontologyManager);
						toReturn.add(loadedLint);
						lintJarMap.put(loadedLint, jarName);
					} else {
						classNames.remove(string);
					}
				} catch (ClassNotFoundException e) {
					Logger.getLogger(LintRollPreferences.class.getName())
							.error("Unable to load class", e);
				} catch (InstantiationException e) {
					Logger.getLogger(LintRollPreferences.class.getName())
							.error("Unable to instantiate lint class", e);
				} catch (IllegalAccessException e) {
					Logger.getLogger(LintRollPreferences.class.getName())
							.error("Unable to instantiate lint class", e);
				} catch (SecurityException e) {
					Logger
							.getLogger(LintRollPreferences.class.getName())
							.warn(
									"Impossible to find the expected constructor security access denied ");
				} catch (NoSuchMethodException e) {
					Logger.getLogger(LintRollPreferences.class.getName()).warn(
							"Impossible to find the expected constructor "
									+ clazz);
				} catch (IllegalArgumentException e) {
					Logger.getLogger(LintRollPreferences.class.getName()).warn(
							"Impossible to invoke the expected constructor illegal argument "
									+ clazz);
				} catch (InvocationTargetException e) {
					Logger.getLogger(LintRollPreferences.class.getName()).warn(
							"Problem invoking the expected constructor "
									+ clazz);
				}
			}
			if (!toReturn.isEmpty()) {
				loadedJars.add(jarName);
				loadedJarPrefList.add(jarName);
			}
		} catch (IOException e) {
			Logger
					.getLogger(JarResources.class.getName())
					.warn(
							"Problem in loading jar: "
									+ jarName
									+ " the file has not been found, please restore it");
			loadedJars.add(jarName);
			invalidJars.add(jarName);
		}
		return toReturn;
	}

	public static void setOWLOntologyManager(OWLOntologyManager ontologyManager) {
		LintRollPreferences.ontologyManager = ontologyManager;
	}

	public static void removeJar(String jarName) {
		for (Lint lint : new HashSet<Lint>(lintJarMap.keySet())) {
			String lintJarName = lintJarMap.get(lint);
			if (jarName.compareTo(lintJarName) == 0) {
				removeLoadedLint(lint);
				removeSelectedLint(lint);
				lintJarMap.remove(lint);
			}
		}
		loadedJars.remove(jarName);
		invalidJars.remove(jarName);
		// for the preferences to be saved
		loadedJarPrefList.clear();
		loadedJarPrefList.addAll(loadedJars);
	}

	public static Set<String> getLoadedJars() {
		return new HashSet<String>(loadedJars);
	}

	public static String getJarName(Lint lint) {
		return LintRollPreferences.lintJarMap.get(lint);
	}

	public static boolean isInvalid(String jarName) {
		return invalidJars.contains(jarName);
	}

	public static void startListenting() {
		ontologyManager.addOntologyChangeListener(opplLintManager);
	}

	public static void stopListening() {
		ontologyManager.removeOntologyChangeListener(opplLintManager);
	}

	public static void addPersonalOPPLLint(OPPLLintScript lint) {
		personalOPPLLints.add(lint.toString());
		addLoadedLint(lint);
	}

	public static void savePreferences() {
		if (prefs != null) {
			if (loadedJarPrefList != null) {
				prefs.putStringList(LOADED_JARS_PREF_NAME, loadedJarPrefList);
			}
			if (personalOPPLLints != null) {
				prefs.putStringList(OPPL_LINTS_PREF_NAME, personalOPPLLints);
			}
		}
	}

	public static void removePersonalOPPLLint(OPPLLintScript lint) {
		personalOPPLLints.remove(lint.toString());
		removeLoadedLint(lint);
	}

	public static void resetAll() {
		loadedJarPrefList.clear();
		personalOPPLLints.clear();
		savePreferences();
	}
}
