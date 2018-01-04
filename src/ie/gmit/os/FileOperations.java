package ie.gmit.os;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
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
		// Create a new stream from the file
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			// Check if the file contains the string
			if (lines.filter(line -> line.contains(searchString)).findFirst().isPresent())
				return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Return false
		return false;
	}

	/**
	 * Returns the line of a file if it contains the given text.
	 * Returns null if the text was not found.
	 * 
	 * @param filePath
	 * @param searchString
	 * @return String
	 */
	public static String findFirstInFile(String filePath, String searchString) {
		// Create a new stream from the file
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			// Try to get the filtered line
			String str = lines.filter(line -> line.contains(searchString)).findFirst().get().trim();
			// If the line was found
			if (str != null && str.length() > 0)
				// Return the string
				return str;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		// Return false
		return null;
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
			Files.write(Paths.get(filePath), (stringtoadd + System.getProperty("line.separator")).getBytes(),
					StandardOpenOption.APPEND, StandardOpenOption.CREATE);
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
		// Create a new stream from the file
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
			Files.write(Paths.get(filePath), sb.toString().getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
			//If the line number was too high
			if(currentLine.get()<lineNumber) {
				return false;
			}
			// Return true
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Loops the lines of a file starting from the beginning. Stores only the
	 * specified amount of lines from the back. Filter should be an empty string if
	 * filtering is not required
	 * 
	 * @param filePath
	 * @param numberOfLines
	 * @param filter
	 * @return ArrayList of lines
	 */
	public static ArrayList<String> getTailOfFile(String filePath, int numberOfLines, String filter) {
		// Current line
		AtomicInteger counter = new AtomicInteger(1);
		// Lines holder
		LinkedList<String> linesHolder = new LinkedList<String>();
		// Create a new stream from the file
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			// Loop each line
			lines.forEach(line -> {
				// Check if the line has to be or contains the filter text
				if (filter == "" || line.contains(filter)) {
					// Add the line to the holder
					linesHolder.offer(counter.get() + "," + line);
					// Remove one from the holder if it exceeds the maximum number of lines
					if (linesHolder.size() > numberOfLines)
						linesHolder.pop();
				}
				// Increment the counter
				counter.incrementAndGet();
			});
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			// e.printStackTrace();
		}
		// Convert the holder to an array list and return it
		return new ArrayList<String>(linesHolder);

	}

}
