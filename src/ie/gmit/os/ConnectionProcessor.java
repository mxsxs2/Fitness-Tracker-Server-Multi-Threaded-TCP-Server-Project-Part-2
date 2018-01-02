package ie.gmit.os;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/**
 * Class used to process an incoming connection to the server
 * @author Mxsxs2
 *
 */
public class ConnectionProcessor implements Runnable {
	//The communication socket
	Socket clientSocket;
	//Message holder
	String message;
	//Current clients id
	int clientID = -1;
	//Processor status
	boolean running = true;
	//Input stream
	ObjectOutputStream out;
	//Input stream
	ObjectInputStream in;

	/**
	 * Initialise the connection processor by the Socket and the Connection od
	 * @param Socket 
	 * @param Connection id
	 */
	ConnectionProcessor(Socket s, int i) {
		clientSocket = s;
		clientID = i;
	}

	/**
	 * Send a new message to the client
	 * @param Message
	 */
	void sendMessage(String msg) {
		try {
			//Send the message
			out.writeObject(msg);
			//Flush the stream
			out.flush();
			System.out.println("server> " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		System.out.println("Accepted Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		try {
			//create a new output stream to socket
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			//Flush the output stream
			out.flush();
			//Create a new input stream from socket
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Accepted Client : ID - " + clientID + " : Address - "
					+ clientSocket.getInetAddress().getHostName());
			//Send success message
			sendMessage("Connection successful");
			//Loop until connection is closed by client
			//TODO: Implement time out
			do {
				try {
					//TODO: Implement "fitness tracker" mechanism here
					System.out.println("client>" + clientID + "  " + message);
					//Send echo to the client
					sendMessage("server got the following: " + message);
					//Read in message from client
					message = (String) in.readObject();
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			//Exit when the user send "bye"
			} while (!message.equals("bye"));

			System.out.println("Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
