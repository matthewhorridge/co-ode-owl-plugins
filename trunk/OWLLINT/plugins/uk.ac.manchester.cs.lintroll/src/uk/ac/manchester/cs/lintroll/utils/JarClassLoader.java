package uk.ac.manchester.cs.lintroll.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

 Copyright (c) Non, Inc. 1999 -- All Rights Reserved

 PACKAGE:	JavaWorld
 FILE:		JarClassLoader.java

 AUTHOR:		John D. Mitchell, Mar  3, 1999

 REVISION HISTORY:
 Name	Date		Description
 ----	----		-----------
 JDM	99.03.03   	Initial version.

 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
/**
 * * JarClassLoader provides a minimalistic ClassLoader which shows how to *
 * instantiate a class which resides in a .jar file. <br>
 * <br> * *
 * 
 * @author John D. Mitchell, Non, Inc., Mar 3, 1999 * *
 * @version 0.5 *
 */
public class JarClassLoader extends MultiClassLoader {
	private JarResources jarResources;

	public JarClassLoader(String jarName) throws IOException {
		// Create the JarResource and suck in the .jar file.
		this.jarResources = new JarResources(jarName);
	}

	public Set<String> getClassNames() {
		Set<String> toReturn = new HashSet<String>();
		Set<String> keys = this.jarResources.getContents();
		for (String string : keys) {
			if (string.endsWith(".class")) {
				toReturn.add(string.replaceAll(".class", "").replace("/", "."));
			}
		}
		return toReturn;
	}

	@Override
	protected byte[] loadClassBytes(String className) {
		// Support the MultiClassLoader's class name munging facility.
		className = this.formatClassName(className);
		// Attempt to get the class data from the JarResource.
		return this.jarResources.getResource(className);
	}

	/**
	 * @return the jarResources
	 */
	public JarResources getJarResources() {
		return this.jarResources;
	}
} // End of Class JarClassLoader.
