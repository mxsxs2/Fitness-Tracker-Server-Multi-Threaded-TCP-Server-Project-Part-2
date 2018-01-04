package ie.gmit.os;

public class Runner {
	public static void main(String[] args) {
		//Create a new server and start it
		Server s=new Server();
		//s.runServer();
		
		UserDatabase ud =new UserDatabase();
		
		User user = new User("123456","Krisztian","Address",25,172,72);
		user.setPassword("Krisz");
		
		System.out.println("registered"+ud.isUserRegistered("123456"));
		System.out.println("login"+ud.isValidUserCredetials(user));
		try {
			System.out.println("register"+ud.registerUser(user));
		} catch (Exception e) {
			ud.logTransaction(TransactionEvent.RegisterFail, user.toString()+" -- "+e.getMessage());
			System.out.println(e.getMessage());
		}
		System.out.println("registered"+ud.isUserRegistered("123456"));
		System.out.println("login"+ud.isValidUserCredetials(user));
		
		
		Record r1 = new Record(RecordType.Fitness,FitnessModeAndMealType.Cycling,"10 min");
		Record r2 = new Record(RecordType.Meal,FitnessModeAndMealType.Walking,"eat a big potato");
		
		ud.addRecord(r1, user);
		ud.addRecord(r2, user);
		ud.addRecord(r1, user);
		
		ud.deleteRecordAtPosition(user, 2);
		
		
		
	}
}
