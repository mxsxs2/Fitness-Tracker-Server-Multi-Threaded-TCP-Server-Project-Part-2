package ie.gmit.os;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public final class FileOperations {

	/**
	 * Check if a path can resolve to a valid directory
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean isValidFolder(File path) {
		// Check if it is a reachable directory
		return path != null && path.exists() && path.isDirectory() && path.canWrite() && path.canRead();
	}

	/**
	 * Check if a path can resolve to a valid file
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean isValidFile(File path) {
		// Check if it is a reachable file
		return path != null && path.exists() && path.isFile() && path.canWrite() && path.canRead();
	}

	/**
	 * Check if a given path exists, if not, it creates a new directory from it.
	 * 
	 * @param path
	 * @return File - null if the directory was not created
	 */
	public static File createDirectoryIfNotExists(String path) {
		// Create a new "file" from path
		final File dir = new File(path);
		// Check if exists
		if (!dir.exists()) {
			try {
				// Create the directory
				dir.mkdir();
				return dir;
			} catch (Exception e) {
				return null;
			}
		}

		return dir;
	}

	/**
	 * Check if a given path exists, if not, it creates a new file from it.
	 * 
	 * @param path
	 * @return File - null if the directory was not created
	 */
	public static File createFileIfNotExists(String path) {
		// Create a new file from path
		final File file = new File(path);
		// Check if exists
		if (!file.exists()) {
			try {
				// Create the file
				file.createNewFile();
				return file;
			} catch (Exception e) {
				return null;
			}
		}

		return file;
	}

	/**
	 * Check if a file contains given text.
	 * 
	 * @param filePath
	 * @param searchString
	 * @return boolean
	 */
	public static boolean fileContains(String filePath, String searchString) {
		// Create a new scanner from the path

		try(Scanner scanner = new Scanner(new File(filePath))){
			// Loop every line in the path
			while (scanner.hasNextLine()) {
				// Check if the next line contains the string
				if (scanner.nextLine().contains(searchString)) {
					// Close the scanner
					scanner.close();
					// Return true on match
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Return false
		return false;
	}

	/**
	 * Writes to the end of a file.
	 * 
	 * @param filePath
	 * @param stringtoadd
	 * @return
	 */
	public static boolean appendToFile(String filePath, String stringtoadd) {
		try {
			// Add to the end of the file
			Files.write(Paths.get(filePath), (stringtoadd + "\r\n").getBytes(), StandardOpenOption.APPEND,StandardOpenOption.CREATE);
			// Return true on success
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Return false
		return false;
	}

}
