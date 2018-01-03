package ie.gmit.os;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	//The server port to listen to
	private int port=2004;
	//The maximum allowed connections at the same time
	private int maxConnections=100;
	
	
	/**
	 * Starts the server at a specified port.
	 * When a new connection is started it creates a new thread for that connection
	 */
	public void runServer() {
		//Create the socket
		try (ServerSocket m_ServerSocket = new ServerSocket(this.port, 10);){
			//Write out basic server info
			System.out.println("The server is running");
			System.out.println("- Listening port: "+this.port);
			System.out.println("- Maximum allowed connectons:"+this.maxConnections);
			
			//Start a new thread pool
			ExecutorService threadpool = Executors.newFixedThreadPool(this.maxConnections);
			
			//Number of connections/connection id
			int id = 0;
			//Start an infinite loop
			while (true) {
				//Check for connection and accept
				Socket clientSocket = m_ServerSocket.accept();
				//Submit the new connection to the pool
				threadpool.submit(new ConnectionProcessor(clientSocket, id++));
			}
		} catch (IOException e) {
			System.out.println("The server coul not be started: "+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}


