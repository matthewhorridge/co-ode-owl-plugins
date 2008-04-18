package uk.ac.manchester.cs.lintroll.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * JarResources: JarResources maps all resources included in a Zip or Jar file.
 * Additionaly, it provides a method to extract one as a blob.
 */
public final class JarResources {
	// external debug flag
	public boolean debugOn = false;
	// jar resource mapping tables
	private Hashtable<String, Integer> htSizes = new Hashtable<String, Integer>();
	private Hashtable<String, byte[]> htJarContents = new Hashtable<String, byte[]>();
	// a jar file
	private String jarFileName;

	/**
	 * creates a JarResources. It extracts all resources from a Jar into an
	 * internal hashtable, keyed by resource names.
	 * 
	 * @param jarFileName
	 *            a jar or zip file
	 * @throws IOException
	 */
	public JarResources(String jarFileName) throws IOException {
		this.jarFileName = jarFileName;
		this.init();
	}

	/**
	 * Extracts a jar resource as a blob.
	 * 
	 * @param name
	 *            a resource name.
	 */
	public byte[] getResource(String name) {
		return this.htJarContents.get(name);
	}

	public Set<String> getContents() {
		return this.htJarContents.keySet();
	}

	/**
	 * initializes internal hash tables with Jar file resources.
	 * 
	 * @throws IOException
	 */
	private void init() throws IOException {
		// extracts just sizes only.
		ZipFile zf = new ZipFile(this.jarFileName);
		Enumeration<? extends ZipEntry> e = zf.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze = e.nextElement();
			if (this.debugOn) {
				System.out.println(this.dumpZipEntry(ze));
			}
			this.htSizes.put(ze.getName(), new Integer((int) ze.getSize()));
		}
		zf.close();
		// extract resources and put them into the hashtable.
		FileInputStream fis = new FileInputStream(this.jarFileName);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ZipInputStream zis = new ZipInputStream(bis);
		ZipEntry ze = null;
		while ((ze = zis.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				continue;
			}
			if (this.debugOn) {
				System.out.println("ze.getName()=" + ze.getName() + ","
						+ "getSize()=" + ze.getSize());
			}
			int size = (int) ze.getSize();
			// -1 means unknown size.
			if (size == -1) {
				size = this.htSizes.get(ze.getName()).intValue();
			}
			byte[] b = new byte[size];
			int rb = 0;
			int chunk = 0;
			while (size - rb > 0) {
				chunk = zis.read(b, rb, size - rb);
				if (chunk == -1) {
					break;
				}
				rb += chunk;
			}
			// add to internal resource hashtable
			this.htJarContents.put(ze.getName(), b);
			if (this.debugOn) {
				System.out.println(ze.getName() + "  rb=" + rb + ",size="
						+ size + ",csize=" + ze.getCompressedSize());
			}
		}
	}

	/**
	 * Dumps a zip entry into a string.
	 * 
	 * @param ze
	 *            a ZipEntry
	 */
	private String dumpZipEntry(ZipEntry ze) {
		StringBuffer sb = new StringBuffer();
		if (ze.isDirectory()) {
			sb.append("d ");
		} else {
			sb.append("f ");
		}
		if (ze.getMethod() == ZipEntry.STORED) {
			sb.append("stored   ");
		} else {
			sb.append("defalted ");
		}
		sb.append(ze.getName());
		sb.append("\t");
		sb.append("" + ze.getSize());
		if (ze.getMethod() == ZipEntry.DEFLATED) {
			sb.append("/" + ze.getCompressedSize());
		}
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		JarResources jarResources = new JarResources(args[0]);
		Set<String> keys = jarResources.htJarContents.keySet();
		for (String string : keys) {
			if (string.endsWith(".class")) {
				System.out.println(string.replaceAll(".class", "").replace("/",
						"."));
			}
		}
	}
} // End of JarResources class.
