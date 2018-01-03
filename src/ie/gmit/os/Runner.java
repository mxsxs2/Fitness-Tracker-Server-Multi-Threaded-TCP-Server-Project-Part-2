package ie.gmit.os;

public class Runner {
	public static void main(String[] args) {
		//Create a new server and start it
		Server s=new Server();
		//s.runServer();
		
		UserDatabase ud =new UserDatabase();
		
		User user = new User("1000201R","Krisztian","Address",25,172,72);
		user.setPassword("Krisz");
		
		System.out.println("registered"+ud.isUserRegistered("1000201R"));
		System.out.println("login"+ud.isValidUserCredetials(user));
		try {
			System.out.println("register"+ud.registerUser(user));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("registered"+ud.isUserRegistered("1000201R"));
		System.out.println("login"+ud.isValidUserCredetials(user));
		
		
		
	}
}
