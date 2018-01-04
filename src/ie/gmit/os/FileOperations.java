package ie.gmit.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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
		//Create a new stream from the file
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			//Check if the file contains the string
			if(lines.filter(line->line.contains(searchString)).findFirst().isPresent()) return true;
		} catch (Exception e) {
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
			Files.write(Paths.get(filePath), (stringtoadd + System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND,
					StandardOpenOption.CREATE);
			// Return true on success
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Return false
		return false;
	}

	/**
	 * Deletes a line from the given file. (Stores the line contents in memory,
	 * which might be an issue with big files)
	 * 
	 * @param filePath
	 * @param lineNumber
	 * @return boolean
	 */
	public static boolean deleteLine(String filePath, int lineNumber) {
		//Create a new stream from the file
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			// String buffer to store contents of the file
			StringBuffer sb = new StringBuffer("");
			// the number of the current line
			AtomicInteger currentLine = new AtomicInteger(1);
			// Loop each line
			lines.forEach(line -> {
				// Store the line if it is not the line which should be delete
				if (currentLine.getAndIncrement() != lineNumber)
					sb.append(line + System.getProperty("line.separator"));
			});

			// Write the file content back
			Files.write(Paths.get(filePath), sb.toString().getBytes(), StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
			// Return true
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	

}
