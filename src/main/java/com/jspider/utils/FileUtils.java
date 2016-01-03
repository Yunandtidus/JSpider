package com.jspider.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FileUtils {
	/**
	 * 
	 * @param f
	 *            the zip file
	 * @return the first Zip Entry as InputStream
	 * @throws IOException
	 * @throws ZipException
	 */
	public static InputStream getFromZipFile(File f) throws ZipException, IOException {
		ZipFile zip = new ZipFile(f);
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			return zip.getInputStream(entry);
		}
		return null;
	}
}
