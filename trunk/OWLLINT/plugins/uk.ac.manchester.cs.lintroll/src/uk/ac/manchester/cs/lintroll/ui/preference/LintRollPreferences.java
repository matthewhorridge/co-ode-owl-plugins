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
import java.net.MalformedURLException;
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
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owl.lint.Lint;
import org.semanticweb.owl.lint.PatternBasedLint;

import uk.ac.manchester.cs.lintroll.utils.JarClassLoader;
import uk.ac.manchester.cs.lintroll.utils.JarResources;

/**
 * @author Luigi Iannone
 * 
 */
public class LintRollPreferences {
	private static Set<Lint> loadedLints;
	private static Set<Lint> selectedLints;
	private static PreferencesManager preferencesManager;
	static Set<String> wellKnownLintClassNames;
	private static Map<Lint, String> lintJarMap = new HashMap<Lint, String>();
	private static Set<LintRollPreferenceChangeListener> listeners = new HashSet<LintRollPreferenceChangeListener>();
	private static List<String> loadedJars;
	static {
		LintRollPreferences.wellKnownLintClassNames = new HashSet<String>();
		LintRollPreferences.wellKnownLintClassNames.add(Lint.class.getName());
		LintRollPreferences.wellKnownLintClassNames.add(PatternBasedLint.class
				.getName());
		loadedLints = new HashSet<Lint>();
		selectedLints = new HashSet<Lint>();
		preferencesManager = PreferencesManager.getInstance();
		initPreferences();
	}

	public static Set<Lint> getLoadedLints() {
		return new HashSet<Lint>(loadedLints);
	}

	/**
	 * Loading preferences from the stored ones
	 */
	private static void initPreferences() {
		Preferences prefs = preferencesManager.getPreferencesForSet("LINTROLL",
				"panelPrefs");
		loadedJars = prefs.getStringList("loadedJars", new ArrayList<String>());
		// Just to avoid duplicates
		Set<String> loadedPrefsSet = new HashSet<String>(loadedJars);
		for (String loadedString : loadedPrefsSet) {
			Set<Lint> jarLoadedLints = loadLints(loadedString);
			for (Lint lint : jarLoadedLints) {
				addLoadedLint(loadedString, lint);
			}
		}
	}

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
		if (changed) {
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
		try {
			JarClassLoader cl = new JarClassLoader(jarName);
			classNames = cl.getClassNames();
			URLClassLoader urlClassLoader = new URLClassLoader(
					new URL[] { new File(jarName).toURL() },
					LintRollPreferences.class.getClassLoader());
			for (String string : new HashSet<String>(classNames)) {
				Class<? extends Object> clazz = urlClassLoader
						.loadClass(string);
				if (!wellKnownLintClassNames.contains(string)
						&& Lint.class.isAssignableFrom(clazz)) {
					toReturn.add((Lint) clazz.newInstance());
				} else {
					classNames.remove(string);
				}
			}
			if (!toReturn.isEmpty()) {
				loadedJars.add(jarName);
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(LintRollPreferences.class.getName()).error(
					"Unable to load class", e);
		} catch (InstantiationException e) {
			Logger.getLogger(LintRollPreferences.class.getName()).error(
					"Unable to instantiate lint class", e);
		} catch (IllegalAccessException e) {
			Logger.getLogger(LintRollPreferences.class.getName()).error(
					"Unable to instantiate lint class", e);
		} catch (MalformedURLException e) {
			Logger.getLogger(LintRollPreferences.class.getName()).error(
					"Unable to load the selected jar containing the lints", e);
		} catch (IOException e) {
			Logger
					.getLogger(JarResources.class.getName())
					.warn(
							"Problem in loading jar: "
									+ jarName
									+ " the file has not been found, please restore it");
		}
		return toReturn;
	}
}
