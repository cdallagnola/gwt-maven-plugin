package org.codehaus.mojo.gwt.utils;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileRetriever {

	public static String[] retrieveCssFiles(File projectFile, Log log) {
		Set<String> retrievedCssFiles = new HashSet<String>();
		listFileWithExtension(retrievedCssFiles, new File(projectFile.getParent()+File.separator+"src\\main\\resources"), ".css");
		if (retrievedCssFiles.size()>0) {
			log.info("Auto discovered CSS files: ");
			for (String string : retrievedCssFiles) {
				log.info(string);
			}
		}
		return retrievedCssFiles.toArray(new String[retrievedCssFiles.size()]);
	}

	public static String[] retrieveI18nFiles(File projectFile, Log log) {
		Set<String> retrievedI18nFiles = new HashSet<String>();
		listFileWithExtension(retrievedI18nFiles, new File(projectFile.getParent()+File.separator+"src\\main\\resources"), ".properties");
		if (retrievedI18nFiles.size()>0) {
			log.info("Auto discovered I18n files: ");
			for (String string : retrievedI18nFiles) {
				log.info(string);
			}
		}
		return retrievedI18nFiles.toArray(new String[retrievedI18nFiles.size()]);
	}

	private static void listFileWithExtension(Set<String> retrievedCssFiles, File currentFolder, String extension) {
		for (File currentFile : currentFolder.listFiles()) {
			if (currentFile.isDirectory()) {
				listFileWithExtension(retrievedCssFiles, currentFile, extension);
			}
			if (currentFile.getName().endsWith(extension) ) {
				retrievedCssFiles.add(retrieveFileName(currentFile.getAbsoluteFile()));
			}
		}
	}
	
	private static String retrieveFileName(final File file) {
		return createPathAfterResourceDirectory(file.getParentFile()) + file.getName();
	}

	private static String createPathAfterResourceDirectory(final File file) {
		if (file.getName().equals("resources")) {
			return "";
		} else {
			return createPathAfterResourceDirectory(file.getParentFile()) + file.getName() + File.separator;
		}
	}

}
