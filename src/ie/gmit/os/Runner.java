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
		
		
		Record r1 = new Record(RecordType.Fitness,FitnessModeAndMealType.Running,"25 min");
		Record r2 = new Record(RecordType.Meal,FitnessModeAndMealType.Cycling,"chicken and egg");
		Record r3 = new Record(RecordType.Meal,FitnessModeAndMealType.Walking,"----------------------2");
		ud.addRecord(r1, user);
		ud.addRecord(r2, user);
		//ud.addRecord(r1, user);
		ud.addRecord(r3, user);
		//ud.deleteRecordAtPosition(user, 2);
		ud.getLatestRecords(user, 10, null).forEach(System.out::println);
		//ud.deleteRecordAtPosition(user, 66);
		ud.getLatestRecords(user, 10, RecordType.Meal).forEach(System.out::println);
		System.out.println("hello");
		
		
		
	}
}
