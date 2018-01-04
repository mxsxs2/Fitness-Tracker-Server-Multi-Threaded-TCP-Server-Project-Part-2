package ie.gmit.os;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Details of a user
 * 
 * @author Mxsxs2
 *
 */
public class User {
	// unique identifier of the user
	private String ppsNumber;
	// Password of the user
	private String password;
	// Name of the user
	private String name;
	// Address of the user
	private String address;
	// Age of the user
	private int age;
	// Weight of the user
	private float weight;
	// Height of the user
	private float height;
	// A list of previous transactions
	private ArrayList<String> transactions;
	// A ist of the user's records
	private ArrayList<Record> records;

	/**
	 * Constructor with the user's basic data
	 * 
	 * @param ppsNumber
	 * @param name
	 * @param address
	 * @param age
	 * @param weight
	 * @param height
	 */
	public User(String ppsNumber, String name, String address, int age, float weight, float height) {
		super();
		this.ppsNumber = ppsNumber;
		this.name = name;
		this.address = address;
		this.age = age;
		this.weight = weight;
		this.height = height;
	}

	/**
	 * Returns the encrypted version of the password
	 * 
	 * @return password
	 */
	public String getPassword() {
		// https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			// Add salted password bytes to digest
			md.update((this.password + this.ppsNumber).getBytes());
			// Get the hash's bytes
			byte[] bytes = md.digest();
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			// Get complete hashed password in hex format
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPpsNumber() {
		return ppsNumber;
	}

	public void setPpsNumber(String ppsNumber) {
		this.ppsNumber = ppsNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public ArrayList<String> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<String> transactions) {
		this.transactions = transactions;
	}

	public ArrayList<Record> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	}

	/**
	 * Generates a comma separated CSV file line from the user details
	 * 
	 * @return
	 */
	public String toCSVString() {
		return this.ppsNumber + "," + this.getPassword() + "," + this.name + "," + this.address + "," + this.age + ","
				+ this.weight + "," + this.height;
	}

	@Override
	public String toString() {
		return "User [ppsNumber=" + ppsNumber + ", password=" + this.getPassword() + ", name=" + name + ", address="
				+ address + ", age=" + age + ", weight=" + weight + ", height=" + height + "]";
	}

	/**
	 * User factory from the user credentials
	 * 
	 * @param ppsNumber
	 * @param password
	 * @return User
	 */
	public static User createUserFromCredentials(String ppsNumber, String password) {
		// Create new user
		User u = new User(ppsNumber, "", "", 0, 0, 0);
		// Set the user password
		u.setPassword(password);
		// Return the user
		return u;
	}

	/**
	 * User factory from a csv string. Returns null if the given string cannot be
	 * parsed into a User object. The method does not load the password of the user.
	 * 
	 * @param userCSVString
	 * @param delimiter
	 * @return User
	 */
	public static User createUserFromCSVLine(String userCSVString, String delimiter) {
		// If there is any lines
		if (userCSVString.trim().length() > 0) {
			// Get the attributes of the string
			String[] attr = userCSVString.trim().split(delimiter);
			// Check if there is any result and if the pps number is filled
			if (attr.length > 0 && attr[0].trim().length() > 0)
				try {
				// Create a new user out of the csv string's parts
				return new User(attr[0].trim(), // PPS Number
						attr.length > 2 ? attr[2].trim() : "", // Name
						attr.length > 3 ? attr[3].trim() : "", // Address
						attr.length > 4 ? Integer.parseInt(attr[4]) : 0, // Age
						attr.length > 5 ? Float.parseFloat(attr[5]) : 0, // Weight
						attr.length > 6 ? Float.parseFloat(attr[6]) : 0); // Height
				}catch(NumberFormatException e){
				
				}
		}

		return null;
	}

}
