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
			// Send success message
			sendMessage("Connection successful");
			// Loop until connection is closed by client
			// TODO: Implement time out
			//Create a new Database 
			Database db=new UserCSVFileDatabase();
			//Create an empty user object
			User user;
			
			
			do {
				try {
					//Send the initial menu message
					sendMessage("Press 1 to login\n Press 2 to register \nPress 3 to exit");
					message = (String) in.readObject();
					
					//If login
					if (message.compareToIgnoreCase("1") == 0) {
						//Ask for the credentials
						sendMessage("Please enter the login PPS number");
						String ppsn = (String)in.readObject();
						sendMessage("Please enter the password");
						String pass = (String)in.readObject();
						
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
							}else {
								sendMessage("There is an issue with this account. Could not log in this time.");
							}
						}else {
							sendMessage("This PPS number and password combination does not exists.");
						}
					//If register
					} else if (message.compareToIgnoreCase("2") == 0)

					{
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

	private void buildUI(ObjectInputStream in, ObjectOutputStream out,Database db, User user) throws ClassNotFoundException, IOException {
		
		
		
	}

}
