/**
 * 
 */
package uk.ac.manchester.cs.lintroll.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Luigi Iannone
 * 
 */
public class JarFileFilter extends FileFilter {
	/**
	 * It accepts only file with .jar extension
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().endsWith(".jar");
	}

	/**
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Filters in files with .jar extension";
	}
}
