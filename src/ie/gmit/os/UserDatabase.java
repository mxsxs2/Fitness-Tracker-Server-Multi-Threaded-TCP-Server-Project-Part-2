package ie.gmit.os;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserDatabase {
	// The main folder of the data/user files
	private String mainFolder = "data";
	// The folder of user files
	private String userData = this.mainFolder + System.getProperty("file.separator") + "userdata";
	// The user list file
	private String userListFile = this.mainFolder + System.getProperty("file.separator") + "users_list.txt";
	// Transaction Logfile
	private String logFile = this.mainFolder + System.getProperty("file.separator") + "user_transaction.log";

	/**
	 * Constructor which initialises the database files
	 */
	public UserDatabase() {
		// Initialise the database files
		this.initializeDatabase();
	}

	/**
	 * Creates the database file structure or repairs if any directory or file is
	 * missing.
	 */
	public void initializeDatabase() {
		// Create the main directory if it does not exists
		if (FileOperations.isValidFolder(FileOperations.createDirectoryIfNotExists(this.mainFolder))) {
			// Create the user data directory if it does not exists
			FileOperations.createDirectoryIfNotExists(this.userData);
			// create the user list file if it does not exists
			FileOperations.createFileIfNotExists(this.userListFile);
		}
	}

	/**
	 * Checks if a given pps number is already added to the user_list file
	 * 
	 * @param ppsNumber
	 * @return boolean
	 */
	public boolean isUserRegistered(String ppsNumber) {
		// Check if the user_list file contains the given pps number
		return FileOperations.fileContains(this.userListFile, ppsNumber);
	}

	/**
	 * Register user to the user_list file, create user data and user transaction
	 * log files
	 * 
	 * @param User
	 * @return boolean
	 */
	public boolean registerUser(User user) throws Exception {
		// If user is null return false
		if (user == null)
			return false;
		// Return false if the user does not have pps number and name
		if (user.getPpsNumber().isEmpty() && user.getName().isEmpty())
			throw new Exception("Name and PPS number is mandatory");
		// Check if the user is registered already
		if (this.isUserRegistered(user.getPpsNumber()))
			throw new Exception("PPS number \"" + user.getPpsNumber() + "\"is already registered.");
		// Try to register the user
		return FileOperations.appendToFile(this.userListFile, user.toCSVString());
	}

	/**
	 * Checks is the user's PPS number and the encrypted password exists in the
	 * user_list file
	 * 
	 * @param User
	 * @return boolean
	 */
	public boolean isValidUserCredetials(User user) {
		// Return false if the user does not have pps number and password
		if (user == null || user.getPpsNumber().isEmpty() && user.getPassword().isEmpty())
			return false;
		// Check if the user exists in the list with the given password
		return FileOperations.fileContains(this.userListFile, user.getPpsNumber() + "," + user.getPassword());
	}

	/**
	 * Logs an event and the event data into the transaction log file
	 * 
	 * @param eventType
	 * @param transactionData
	 */
	public void logTransaction(TransactionEvent eventType, String transactionData) {
		// Get the current time stamp
		final String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		System.out.println(this.logFile + "  " + timeStamp + " " + eventType + ": " + transactionData);
		// Add to the log file
		FileOperations.appendToFile(this.logFile, timeStamp + " " + eventType + ": " + transactionData);
	}

	/**
	 * Check is a record is valid. If it is valid it adds to the end of the users's
	 * data file.
	 * 
	 * @param record
	 * @param user
	 * @return boolean
	 */
	public boolean addRecord(Record record, User user) {
		// If the user doesn't have PPS number or the record is not filled
		if (user.getPpsNumber() == null || record.getRecordType() == null || record.getContent() == null
				|| record.getModeType() == null)
			return false;
		// Add the record to the user file
		return FileOperations.appendToFile(
				this.userData + System.getProperty("file.separator") + user.getPpsNumber() + ".csv",
				record.toCSVString());
	}
}
