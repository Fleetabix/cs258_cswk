import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.*;

class Assignment {

	static final List<String> mon = Arrays.asList("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec");

  private static String readEntry(String prompt) 
	{
		try 
		{
			StringBuffer buffer = new StringBuffer();
			System.out.print(prompt);
			System.out.flush();
			int c = System.in.read();
			while(c != '\n' && c != -1) {
				buffer.append((char)c);
				c = System.in.read();
			}
			return buffer.toString().trim();
		} 
		catch (IOException e) 
		{
			return "";
		}
 	}

	/**
	* @param conn An open database connection 
	* @param productIDs An array of productIDs associated with an order
  * @param quantities An array of quantities of a product. The index of a quantity correspeonds with an index in productIDs
	* @param orderDate A string in the form of 'DD-Mon-YY' that represents the date the order was made
	* @param staffID The id of the staff member who sold the order
	*/
	public static void option1(Connection conn, int[] productIDs, int[] quantities, String orderDate, int staffID)
	{
		// Incomplete - Code for option 1 goes here
	}

	/**
	* @param conn An open database connection 
	* @param productIDs An array of productIDs associated with an order
  * @param quantities An array of quantities of a product. The index of a quantity correspeonds with an index in productIDs
	* @param orderDate A string in the form of 'DD-Mon-YY' that represents the date the order was made
	* @param collectionDate A string in the form of 'DD-Mon-YY' that represents the date the order will be collected
	* @param fName The first name of the customer who will collect the order
	* @param LName The last name of the customer who will collect the order
	* @param staffID The id of the staff member who sold the order
	*/
	public static void option2(Connection conn, int[] productIDs, int[] quantities, String orderDate, String collectionDate, String fName, String LName, int staffID)
	{
		// Incomplete - Code for option 2 goes here
	}

	/**
	* @param conn An open database connection 
	* @param productIDs An array of productIDs associated with an order
  * @param quantities An array of quantities of a product. The index of a quantity correspeonds with an index in productIDs
	* @param orderDate A string in the form of 'DD-Mon-YY' that represents the date the order was made
	* @param deliveryDate A string in the form of 'DD-Mon-YY' that represents the date the order will be delivered
	* @param fName The first name of the customer who will receive the order
	* @param LName The last name of the customer who will receive the order
	* @param house The house name or number of the delivery address
	* @param street The street name of the delivery address
	* @param city The city name of the delivery address
	* @param staffID The id of the staff member who sold the order
	*/
	public static void option3(Connection conn, int[] productIDs, int[] quantities, String orderDate, String deliveryDate, String fName, String LName,
				   String house, String street, String city, int staffID)
	{
		// Incomplete - Code for option 3 goes here
	}

	/**
	* @param conn An open database connection 
	*/
	public static void option4(Connection conn)
	{
		// Incomplete - Code for option 4 goes here
	}

	/**
	* @param conn An open database connection 
	* @param date The target date to test collection deliveries against
	*/
	public static void option5(Connection conn, String date)
	{
		// Incomplete - Code for option 5 goes here
	}

	/**
	* @param conn An open database connection 
	*/
	public static void option6(Connection conn)
	{
		// Incomplete - Code for option 6 goes here
	}

	/**
	* @param conn An open database connection 
	*/
	public static void option7(Connection conn)
	{
		// Incomplete - Code for option 7 goes here
	}

	/**
	* @param conn An open database connection 
	* @param year The target year we match employee and product sales against
	*/
	public static void option8(Connection conn, int year)
	{
		// Incomplete - Code for option 8 goes here
	}

	public static Connection getConnection()
	{
		String user;
		String passwrd;
		Connection conn;

		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch (ClassNotFoundException x) 
		{
			System.out.println ("Driver could not be loaded");
		}

		user = readEntry("Enter database account:");
		passwrd = readEntry("Enter a password:");
		try
		{
			conn = DriverManager.getConnection
			// ("jdbc:oracle:thin:@daisy.warwick.ac.uk:1521:daisy",user,passwrd);
			("jdbc:oracle:thin:@localhost:7100:daisy", user, passwrd);

			return conn;
		}
		catch(SQLException e)
		{
			System.out.println("Error retrieving connection");
			return null;
		}
	}

	public static void main(String args[]) throws SQLException, IOException
	{
		// You should only need to fetch the connection details once
		Connection conn = getConnection();

		// Incomplete
		// Code to present a looping menu, read in input data and call the appropriate option menu goes here
		// You may use readEntry to retrieve input data
		int choice;

		while((choice = menu()) != 0) {
			switch(choice) {
				case 1 : ;
					option1();
					break;
			}
		}

		conn.close();
	}

	public static void option1() {
		ArrayList<Integer> productID = new ArrayList<>();
		ArrayList<Integer> productQuantity = new ArrayList<>();
		String date;
		int staffID;
		String input;

		do {
			while(!isNumeric(input = readEntry("Enter a product ID:"))) {
				
				System.out.println("Please enter a numeric product ID");
			}
			productID.add(Integer.valueOf(input));

			while(!isNumeric(input = readEntry("Enter the quantity sold:")) ||
				Integer.valueOf(input) < 1) {

				System.out.println("Please enter a numeric product quantity");
			}
			productQuantity.add(Integer.valueOf(input));

		} while(readEntry("Is there another product in the order") == "Y");

		while(!isDate(input = readEntry("Enter the date sold:"))) {
			System.out.println("Please enter a valid date");		                                                                                                                                                                                                                                             
		}
		date = input;

		while(!isNumeric(input = readEntry("Enter your staff ID:"))){
			System.out.println("Please enter a numeric staff ID");
		}
		staffID = Integer.valueOf(input);

		option1(getConnection(), toPrimitive(productID), toPrimitive(productQuantity), date, staffID);

	}

	public static int menu() {
		System.out.println("Menu");
		System.out.println("(1) In-Store Purchases");
		System.out.println("(2) Collection");
		System.out.println("(3) Delivery");
		System.out.println("(4) Biggest Sellers");
		System.out.println("(5) Reserved Stock");
		System.out.println("(6) Staff Life-Time Success");
		System.out.println("(7) Staff Contribution");
		System.out.println("(8) Employees of the Year");
		System.out.println("(0) Quit");
		String choice = readEntry("Enter your choice:");
		if (choice.length() == 1 && inRange(choice.codePointAt(0), 48, 56)) return Integer.valueOf(choice);

		else return -1;  
	}

	public static boolean inRange(int test, int low, int high) {
		return !(test < low || test > high);
	}

	public static boolean isNumeric(String s) {
		for (char c : s.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDate(String s) {
		if(Pattern.matches("^\\d\\d-[a-zA-Z]{3}-\\d\\d$", s)) {
			String month = s.substring(3, 6).toLowerCase();
			
			return mon.contains(month);
		}
		return false;
	}

	public static int[] toPrimitive(ArrayList<Integer> integers) {
		
		int[] res = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		
		for (int i = 0; i < res.length; i++) {
			res[i] = iterator.next().intValue();
		}

		return res;
	}
}
