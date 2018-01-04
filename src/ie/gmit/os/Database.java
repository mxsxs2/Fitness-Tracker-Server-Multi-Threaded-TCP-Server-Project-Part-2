package ie.gmit.os;

import java.util.ArrayList;

public interface Database {

	/**
	 * Checks if a given pps number is already added to the user_list file
	 * 
	 * @param ppsNumber
	 * @return boolean
	 */
	boolean isUserRegistered(String ppsNumber);

	/**
	 * Register user to the user_list file, create user data and user transaction
	 * log files
	 * 
	 * @param User
	 * @return boolean
	 */
	boolean registerUser(User user) throws Exception;

	/**
	 * Checks is the user's PPS number and the encrypted password exists in the
	 * user_list file
	 * 
	 * @param User
	 * @return boolean
	 */
	boolean isValidUserCredetials(User user);

	/**
	 * Logs an event and the event data into the transaction log file
	 * 
	 * @param eventType
	 * @param transactionData
	 */
	void logTransaction(TransactionEvent eventType, String transactionData);

	/**
	 * Check is a record is valid. If it is valid it adds to the end of the users's
	 * data file.
	 * 
	 * @param record
	 * @param user
	 * @return boolean
	 */
	boolean addRecord(Record record, User user);

	/**
	 * Deletes a line at a given position Line numbering starts from one
	 * 
	 * @param user
	 * @param lineNumber
	 * @return boolean
	 */
	boolean deleteRecordAtPosition(User user, int lineNumber);

	/**
	 * Get the last <i>numberOfRecords</i> amount of lines from the back of the
	 * file. The records can be filtered to given RecordType. If the type is
	 * specified as null, all record types are shown.
	 * 
	 * @param user
	 * @param numberOfRecords
	 * @param recordType
	 * @return ArrayList of Records
	 */
	ArrayList<Record> getLatestRecords(User user, int numberOfRecords, RecordType recordType);

	/**
	 * Loads a user from the database/text file if the given pps number is found.
	 * Returns null the user was not found
	 * 
	 * @param ppsNumber
	 * @return null
	 */
	User loadUserByPPSNumber(String ppsNumber);

}