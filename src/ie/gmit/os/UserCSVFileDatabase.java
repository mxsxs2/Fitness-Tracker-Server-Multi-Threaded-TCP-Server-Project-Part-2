package ie.gmit.os;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class UserCSVFileDatabase implements Database {
	// The main folder of the data/user files
	private String mainFolder = "data";
	// The folder of user files
	private String userData = this.mainFolder + System.getProperty("file.separator") + "userdata";
	// The user list file
	private String userListFile = this.mainFolder + System.getProperty("file.separator") + "users_list.csv";
	// Transaction Logfile
	private String logFile = this.mainFolder + System.getProperty("file.separator") + "user_transaction.log";

	/**
	 * Constructor which initialises the database files
	 */
	public UserCSVFileDatabase() {
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

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#isUserRegistered(java.lang.String)
	 */
	@Override
	public boolean isUserRegistered(String ppsNumber) {
		// Check if the user_list file contains the given pps number
		return FileOperations.fileContains(this.userListFile, ppsNumber+",");
	}

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#registerUser(ie.gmit.os.User)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#isValidUserCredetials(ie.gmit.os.User)
	 */
	@Override
	public boolean isValidUserCredetials(User user) {
		// Return false if the user does not have pps number and password
		if (user == null || user.getPpsNumber().isEmpty() && user.getPassword().isEmpty())
			return false;
		// Check if the user exists in the list with the given password
		return FileOperations.fileContains(this.userListFile, user.getPpsNumber() + "," + user.getPassword());
	}

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#logTransaction(ie.gmit.os.TransactionEvent, java.lang.String)
	 */
	@Override
	public void logTransaction(TransactionEvent eventType, String transactionData) {
		// Get the current time stamp
		final String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		// Add to the log file
		FileOperations.appendToFile(this.logFile, timeStamp + " " + eventType + ": " + transactionData);
	}

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#addRecord(ie.gmit.os.Record, ie.gmit.os.User)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#deleteRecordAtPosition(ie.gmit.os.User, int)
	 */
	@Override
	public boolean deleteRecordAtPosition(User user, int lineNumber) {
		// If the user doesn't have PPS number
		if (user.getPpsNumber() == null)
			return false;
		// Try to delete the line
		return FileOperations.deleteLine(
				this.userData + System.getProperty("file.separator") + user.getPpsNumber() + ".csv", lineNumber);
	}

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#getLatestRecords(ie.gmit.os.User, int, ie.gmit.os.RecordType)
	 */
	@Override
	public ArrayList<Record> getLatestRecords(User user, int numberOfRecords, RecordType recordType) {
		// Get the list of records from the file
		ArrayList<String> list = FileOperations.getTailOfFile(
				this.userData + System.getProperty("file.separator") + user.getPpsNumber() + ".csv", numberOfRecords,
				recordType == null ? "" : recordType.name());
		// Convert the content of the array list to records
		return list.stream().map(Record::new).collect(Collectors.toCollection(ArrayList::new));
	}

	/* (non-Javadoc)
	 * @see ie.gmit.os.Database#loadUserByPPSNumber(java.lang.String)
	 */
	@Override
	public User loadUserByPPSNumber(String ppsNumber) {
		// If there is some value
		if (ppsNumber.length() > 0) {
			// Get the user from the file
			String userLine = FileOperations.findFirstInFile(this.userListFile, ppsNumber+",");
			// If there is a user
			if (userLine != null) {
				// Convert the user line to an user Object
				return User.createUserFromCSVLine(userLine, ",");
			}
		}

		return null;
	}
}
