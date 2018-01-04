package ie.gmit.os;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			System.out.println("server> " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println(
				"Accepted Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		try {
			// create a new output stream
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			// Flush the output stream
			out.flush();
			// Create a new input stream
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Accepted Client : ID - " + clientID + " : Address - "
					+ clientSocket.getInetAddress().getHostName());

			//Create a new Database 
			Database db=new UserCSVFileDatabase();
			//Create an empty user object
			User user;
			
			// Loop until connection is closed by client
			// TODO: Implement time out
			do {
				try {
					//Send the initial menu message
					sendMessage("\nPress 1 to login\nPress 2 to register \nPress 3 to exit");
					message = (String) in.readObject();
					
					//If login
					if (message.compareToIgnoreCase("1") == 0) {
						//Ask for the credentials
						String ppsn = this.sendMessageAndReadString("Please enter the login PPS number");
						String pass = this.sendMessageAndReadString("Please enter the password");
						
						//Create a new user object from the credentials
						user =User.createUserFromCredentials(ppsn, pass);
						//Try to login
						if(db.isValidUserCredetials(user)) {
							//Reload the user details from the user file
							User u2=db.loadUserByPPSNumber(ppsn);
							//The user could be loaded
							if(u2!=null) {
								//Override the user
								user=u2;
								sendMessage("Welcome "+user.getName()+"!");
								//Log the login success 
								db.logTransaction(TransactionEvent.LoginSuccess, clientSocket.getInetAddress()+" - "+user.getPpsNumber());
							}else {
								sendMessage("There is an issue with this account. Could not log in this time.");
								//Log the login fail 
								db.logTransaction(TransactionEvent.LoginFail, clientSocket.getInetAddress()+" - "+user.getPpsNumber());
							}
						}else {
							sendMessage("This PPS number and password combination does not exists.");
							//Log the login fail 
							db.logTransaction(TransactionEvent.LoginFail, clientSocket.getInetAddress()+" - "+user.getPpsNumber());
						}
					//If register
					} else if (message.compareToIgnoreCase("2") == 0) {
						//Ask for the user details
						String ppsn = this.sendMessageAndReadString("Please enter the your PPS number");
						String pass = this.sendMessageAndReadString("Please enter your password");
						String name = this.sendMessageAndReadString("Please enter your name");
						String address = this.sendMessageAndReadString("Please enter your address");
						int age = this.sendMessageAndReadInt("Please enter your age");
						float weight = this.sendMessageAndReadFloat("Please enter your weight");
						float height = this.sendMessageAndReadFloat("Please enter your height");
						//Crate the user object
						user = new User(ppsn,name,address,age,weight,height);
						//Set the password for the user
						user.setPassword(pass);
						
						try {
							//Try to register the user and check if it was registered
							if(db.registerUser(user)) {
								sendMessage("PPS number \""+ppsn+"\" is successfully registered");
								//Log the register success 
								db.logTransaction(TransactionEvent.RegisterSuccess, clientSocket.getInetAddress()+" - "+user.toString());
							}else {
								sendMessage("Could not register this time. Please try again later.");
								//Log the register fail 
								db.logTransaction(TransactionEvent.RegisterFail, clientSocket.getInetAddress()+" - "+user.toString());
							}
						} catch (Exception e) {
							sendMessage(e.getMessage());
							//Log the register fail 
							db.logTransaction(TransactionEvent.RegisterFail, clientSocket.getInetAddress()+" - "+user.toString()+" -- "+e.getMessage());
						}
					
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
				// Exit when the user send "bye"
			} while (!message.equals("bye"));

			System.out.println(
					"Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sends a message and waits for a String response
	 * @param message
	 * @return String
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private String sendMessageAndReadString(String message) throws ClassNotFoundException, IOException {
		sendMessage(message);
		return (String)this.in.readObject();
	}
	
	/**
	 * Sends a message and waits for a int response
	 * @param message
	 * @return int
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private int sendMessageAndReadInt(String message) throws ClassNotFoundException, IOException {
		sendMessage(message);
		try {
			return Integer.parseInt((String)this.in.readObject());
		}catch(NumberFormatException e) {
			
		}
		return 0;
	}
	
	/**
	 * Sends a message and waits for a float response
	 * @param message
	 * @return float
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private float sendMessageAndReadFloat(String message) throws ClassNotFoundException, IOException {
		sendMessage(message);
		try {
			return Float.parseFloat((String)this.in.readObject());
		}catch(NumberFormatException e) {
			
		}
		return 0;
	}

}
