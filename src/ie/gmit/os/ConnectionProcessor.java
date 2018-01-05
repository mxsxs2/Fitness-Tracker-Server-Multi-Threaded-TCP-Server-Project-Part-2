package ie.gmit.os;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class used to process an incoming connection to the server
 * 
 * @author Mxsxs2
 *
 */
public class ConnectionProcessor implements Runnable {
	// The communication socket
	Socket clientSocket;
	// Message holder
	String message;
	// Current clients id
	int clientID = -1;
	// Processor status
	boolean running = true;
	// Input stream
	ObjectOutputStream out;
	// Input stream
	ObjectInputStream in;

	/**
	 * Initialise the connection processor by the Socket and the Connection od
	 * 
	 * @param Socket
	 * @param Connection
	 *            id
	 */
	ConnectionProcessor(Socket s, int i) {
		clientSocket = s;
		clientID = i;
	}

	/**
	 * Send a new message to the client
	 * 
	 * @param Message
	 */
	void sendMessage(String msg) {
		try {
			// Send the message
			out.writeObject(msg);
			// Flush the stream
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Create a new Database
		Database db = new UserCSVFileDatabase();
		try {
			// create a new output stream
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			// Flush the output stream
			out.flush();
			// Create a new input stream
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Accepted Client : ID - " + clientID + " : Address - "
					+ clientSocket.getInetAddress().getHostName());

			// Log
			db.logTransaction(TransactionEvent.ConnectionStarted,
					"ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
			// Create an empty user object
			User user;

			// Loop until connection is closed by client
			// TODO: Implement time out
			do {
				try {
					// Send the initial menu message
					this.sendMessage("\nPress 1 to login\nPress 2 to register \nPress 3 to exit");
					this.message = (String) this.in.readObject();

					// If login
					if (this.message.compareToIgnoreCase("1") == 0) {
						// Ask for the credentials
						String ppsn = this.sendMessageAndReadString("Please enter the login PPS number");
						String pass = this.sendMessageAndReadString("Please enter the password");

						// Create a new user object from the credentials
						user = User.createUserFromCredentials(ppsn, pass);
						// Try to login
						if (db.isValidUserCredetials(user)) {
							// Reload the user details from the user file
							User u2 = db.loadUserByPPSNumber(ppsn);
							// The user could be loaded
							if (u2 != null) {
								// Override the user
								user = u2;
								this.sendMessage("Welcome " + user.getName() + "!");
								// Log the login success
								db.logTransaction(TransactionEvent.LoginSuccess,
										clientSocket.getInetAddress() + " - " + user.getPpsNumber());

								// Draw the logged in menu
								do {
									this.sendMessage(
											"\nPress -1 to log out\nPress 1 to add fitness record\nPress 2 to add meal record\nPress 3 to view last ten records\nPress 4 to delete a record\nPress 5 to view user details");
									this.message = (String) in.readObject();

									// If fitness record adding
									if (this.message.compareToIgnoreCase("1") == 0) {
										FitnessModeAndMealType fmmt = null;
										String duration;
										// Get the mode of the fitness
										int option = this.sendMessageAndReadInt(
												"Press 1 to add Walking duration\nPress 2 to add Running duration\nPress 3 to add Cycling duration");
										// Get determine fitness mode
										if (option == 1) {
											fmmt = FitnessModeAndMealType.Walking;
										} else if (option == 2) {
											fmmt = FitnessModeAndMealType.Running;
										} else if (option == 3) {
											fmmt = FitnessModeAndMealType.Cycling;
										}

										// Get the duration
										duration = this
												.sendMessageAndReadString("Please enter the duration of trainig");
										// If there was valid entry
										if (fmmt != null && duration.length() > 0) {
											// Crate a new record
											Record r = new Record(RecordType.Fitness, fmmt, duration);
											// Try to add record to the db
											if (db.addRecord(r, user)) {
												this.sendMessage("The fitness record is added");
												// Log the success
												db.logTransaction(TransactionEvent.RecordAddSuccess,
														user.getPpsNumber() + ": " + r.toString());
											} else {
												this.sendMessage(
														"The fitness record could not be added. Please try again later");
												// Log the fail
												db.logTransaction(TransactionEvent.RecordAddFail,
														user.getPpsNumber() + ": " + r.toString());
											}
										} else {
											this.sendMessage("The provided details of the record are invalid");
											// Log the fail
											db.logTransaction(TransactionEvent.RecordAddFail,
													user.getPpsNumber() + ": Invalid input");
										}

										// Meal record adding
									} else if (this.message.compareToIgnoreCase("2") == 0) {
										FitnessModeAndMealType fmmt = null;
										String description;
										// Get the type of the meal
										int option = this.sendMessageAndReadInt(
												"Press 1 to add Breakfast\nPress 2 to add Lunch\nPress 3 to add Dinner");
										// Determine fitness mode
										if (option == 1) {
											fmmt = FitnessModeAndMealType.Breakfast;
										} else if (option == 2) {
											fmmt = FitnessModeAndMealType.Lunch;
										} else if (option == 3) {
											fmmt = FitnessModeAndMealType.Dinner;
										}

										// Get the duration
										description = this.sendMessageAndReadString(
												"Please enter the short meal description(max 100 characters)");
										// If there was valid entry
										if (fmmt != null && description.length() > 0) {
											// Crate a new record
											Record r = new Record(RecordType.Meal, fmmt, description);
											// Try to add record to the db
											if (db.addRecord(r, user)) {
												this.sendMessage("The meal record is added");
												// Log the success
												db.logTransaction(TransactionEvent.RecordAddSuccess,
														user.getPpsNumber() + ": " + r.toString());
											} else {
												this.sendMessage(
														"The meal record could not be added. Please try again later");
												// Log the fail
												db.logTransaction(TransactionEvent.RecordAddFail,
														user.getPpsNumber() + ": " + r.toString());
											}
										} else {
											this.sendMessage("The provided details of the record are invalid");
											// Log the fail
											db.logTransaction(TransactionEvent.RecordAddFail,
													user.getPpsNumber() + ": Invalid input");
										}
										// If record viewing option was selected
									} else if (this.message.compareToIgnoreCase("3") == 0) {
										// Number of records to select
										int numberOfRecords = 10;
										// Send the menu
										int option = this.sendMessageAndReadInt(
												"Press 1 for meal records\nPress 2 for fitness records\nPress 3 to both record types");
										// Record types to select
										RecordType recordType = null;
										// Determine record type
										if (option == 1) {
											recordType = RecordType.Meal;
										} else if (option == 2) {
											recordType = RecordType.Fitness;
										} else if (option == 3) {
											recordType = null;
										}
										// Get the records
										ArrayList<Record> records = db.getLatestRecords(user, numberOfRecords,
												recordType);
										// If there are no records
										if (records.size() == 0) {
											this.sendMessage("You dont have any"
													+ (recordType != null ? " " + recordType.name() : "")
													+ " records yet.");
											// Log the record loading
											db.logTransaction(TransactionEvent.RecordListLoaded,
													user.getPpsNumber() + ": No"
															+ (recordType != null ? " " + recordType.name() : "")
															+ " records were found");
										} else {
											// Create new string builder
											StringBuilder sb = new StringBuilder("");
											// Stringify the loaded records according to requested record type
											if (option == 1) {
												// Add each record to the string builder
												records.forEach(record -> sb.append("Position: " + record.getRecordId()
														+ "; Meal: " + record.getModeType() + "; Description:"
														+ record.getContent() + "\n"));
											} else if (option == 2) {
												// Add each record to the string builder
												records.forEach(record -> sb.append("Position: " + record.getRecordId()
														+ "; Mode: " + record.getModeType() + "; Duration:"
														+ record.getContent() + "\n"));
											} else if (option == 3) {
												// Add each record to the string builder
												records.forEach(record -> sb.append("Position: " + record.getRecordId()
														+ "; Type: " + record.getRecordType() + "; Meal/Mode: "
														+ record.getModeType() + "; Description/Duration:"
														+ record.getContent() + "\n"));
											}
											// Send the records
											this.sendMessage("Found " + records.size()
													+ (recordType != null ? " " + recordType.name() : "")
													+ " records:\n" + sb.toString());

											// Log the record loading
											db.logTransaction(TransactionEvent.RecordListLoaded,
													user.getPpsNumber() + ": " + records.size() + " "
															+ (recordType != null ? " " + recordType.name() : "")
															+ " found");
										}
										// If delete record
									} else if (this.message.compareToIgnoreCase("4") == 0) {
										// Get the record it
										int recordId = this.sendMessageAndReadInt("Please enter the record id");
										// If the id is valid
										if (recordId > 0) {
											// Try do delete the record
											if (db.deleteRecordAtPosition(user, recordId)) {
												this.sendMessage("The record was succesfully deleted");
												// Log
												db.logTransaction(TransactionEvent.RecordDeleteSuccess,
														user.getPpsNumber() + ": record id " + recordId
																+ " was deleted");

											} else {
												this.sendMessage(
														"The record could not be deleted this time. It either does not exists or there was a database error. Please try again later");
												// Log
												db.logTransaction(TransactionEvent.RecordDeleteFail, user.getPpsNumber()
														+ ": record id " + recordId + " could not be deleted");
											}
										} else {
											this.sendMessage("This record id does not exists");
											// Log
											db.logTransaction(TransactionEvent.RecordDeleteSuccess,
													user.getPpsNumber() + ": Invalid record id");
										}
										// If show user details
									} else if (this.message.compareToIgnoreCase("5") == 0) {
										//Send user details
										this.sendMessage("User details:\n" + "PPS Number: " + user.getPpsNumber()
												+ "\nName: " + user.getName() + "\nAddress: " + user.getAddress()
												+ "\nAge: " + user.getAge() + "\nWeight: " + user.getWeight()
												+ "\nHeight: " + user.getHeight());
									}
									// Log out if -1 is entered
								} while (!this.message.equals("-1"));

							} else {
								this.sendMessage("There is an issue with this account. Could not log in this time.");
								// Log the login fail
								db.logTransaction(TransactionEvent.LoginFail,
										clientSocket.getInetAddress() + " - " + user.getPpsNumber());
							}
						} else {
							this.sendMessage("This PPS number and password combination does not exists.");
							// Log the login fail
							db.logTransaction(TransactionEvent.LoginFail,
									clientSocket.getInetAddress() + " - " + user.getPpsNumber());
						}
						// If register
					} else if (this.message.compareToIgnoreCase("2") == 0) {
						// Ask for the user details
						String ppsn = this.sendMessageAndReadString("Please enter the your PPS number");
						String pass = this.sendMessageAndReadString("Please enter your password");
						String name = this.sendMessageAndReadString("Please enter your name");
						String address = this.sendMessageAndReadString("Please enter your address");
						int age = this.sendMessageAndReadInt("Please enter your age");
						float weight = this.sendMessageAndReadFloat("Please enter your weight");
						float height = this.sendMessageAndReadFloat("Please enter your height");
						// Crate the user object
						user = new User(ppsn, name, address, age, weight, height);
						// Set the password for the user
						user.setPassword(pass);

						try {
							// Try to register the user and check if it was registered
							if (db.registerUser(user)) {
								this.sendMessage("PPS number \"" + ppsn + "\" is successfully registered");
								// Log the register success
								db.logTransaction(TransactionEvent.RegisterSuccess,
										clientSocket.getInetAddress() + " - " + user.toString());
							} else {
								this.sendMessage("Could not register this time. Please try again later.");
								// Log the register fail
								db.logTransaction(TransactionEvent.RegisterFail,
										clientSocket.getInetAddress() + " - " + user.toString());
							}
						} catch (Exception e) {
							this.sendMessage(e.getMessage());
							// Log the register fail
							db.logTransaction(TransactionEvent.RegisterFail,
									clientSocket.getInetAddress() + " - " + user.toString() + " -- " + e.getMessage());
						}

					} else if (this.message.compareToIgnoreCase("3") == 0) {
						this.message = "bye";
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
				// Exit when the user send "bye"
			} while (!this.message.equals("bye"));

			System.out.println(
					"Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
			// Log
			db.logTransaction(TransactionEvent.ConnectionEnded,
					"ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		} catch (Exception e) {

			if (e.getMessage().equals("Connection reset")) {
				System.out.println("Client connection reset : ID - " + clientID + " : Address - "
						+ clientSocket.getInetAddress().getHostName());
			} else {
				System.out.println("Client connection error (" + e.getMessage() + "): ID - " + clientID
						+ " : Address - " + clientSocket.getInetAddress().getHostName());
			}
			// Log
			db.logTransaction(TransactionEvent.ConnectionError, e.getMessage() + ": ID - " + clientID + " : Address - "
					+ clientSocket.getInetAddress().getHostName());
		}
	}

	/**
	 * Sends a message and waits for a String response
	 * 
	 * @param message
	 * @return String
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private String sendMessageAndReadString(String message) throws ClassNotFoundException, IOException {
		sendMessage(message);
		return ((String) this.in.readObject()).trim();
	}

	/**
	 * Sends a message and waits for a int response
	 * 
	 * @param message
	 * @return int
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private int sendMessageAndReadInt(String message) throws ClassNotFoundException, IOException {
		sendMessage(message);
		try {
			return Integer.parseInt((String) this.in.readObject());
		} catch (NumberFormatException e) {

		}
		return 0;
	}

	/**
	 * Sends a message and waits for a float response
	 * 
	 * @param message
	 * @return float
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private float sendMessageAndReadFloat(String message) throws ClassNotFoundException, IOException {
		sendMessage(message);
		try {
			return Float.parseFloat((String) this.in.readObject());
		} catch (NumberFormatException e) {

		}
		return 0;
	}

}
