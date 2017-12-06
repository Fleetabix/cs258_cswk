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
	public static void option1(Connection conn, int[] productIDs, int[] quantities, String orderDate, int staffID) throws SQLException
	{
		//Insert into ORDERS table
		System.out.println(orderDate);
		String stmt = "INSERT INTO ORDERS (OrderType, OrderCompleted, OrderPlaced) ";
		stmt += "VALUES('InStore', 1, ?)";
		PreparedStatement p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setString(1, orderDate);
		p.executeUpdate();

		//Return newly created orderID
		stmt = "SELECT MAX (OrderID) FROM ORDERS";
		Statement s = conn.createStatement();
		ResultSet rs =  s.executeQuery(stmt);
		rs.next();
		int orderID = rs.getInt(1);
		System.out.println(orderID);
		//Insert into ORDER_PRODUCTS for all ordered products
		stmt = "INSERT INTO ORDER_PRODUCTS VALUES(?, ?, ?)";
		//Adjust quantities of products accordingly
		String stmt1 = "UPDATE INVENTORY SET ProductStockAmount = ProductStockAmount - ? ";
		stmt1 += "WHERE ProductID = ?";
		p = conn.prepareStatement(stmt);
		PreparedStatement p1 = conn.prepareStatement(stmt1);
		p.clearParameters();
		p.setInt(1, orderID);
		p1.clearParameters();
		for (int i = 0; i < productIDs.length; i++) {
			System.out.println("Still running");
			p.setInt(2, productIDs[i]);
			p.setInt(3, quantities[i]);
			p.executeUpdate();

			System.out.println("Still running");
			p1.setInt(1, quantities[i]);
			p1.setInt(2, productIDs[i]);
			p1.executeUpdate();
		}
		System.out.println("Still running");
		//Update staff order table
		stmt = "INSERT INTO STAFF_ORDERS VALUES(?, ?)";
		p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setInt(1, staffID);
		p.setInt(2, orderID);
		p.executeUpdate();

		printQuantities(conn, productIDs);
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
	public static void option2(Connection conn, int[] productIDs, int[] quantities, String orderDate, String collectionDate, String fName, String lName, int staffID) throws SQLException
	{
		//Insert into ORDERS table
		System.out.println(orderDate);
		String stmt = "INSERT INTO ORDERS (OrderType, OrderCompleted, OrderPlaced) ";
		stmt += "VALUES('Collection', 0, ?)";
		PreparedStatement p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setString(1, orderDate);
		p.executeUpdate();

		//Return newly created orderID
		stmt = "SELECT MAX (OrderID) FROM ORDERS";
		Statement s = conn.createStatement();
		ResultSet rs =  s.executeQuery(stmt);
		rs.next();
		int orderID = rs.getInt(1);
		System.out.println(orderID);
		//Insert into ORDER_PRODUCTS for all ordered products
		stmt = "INSERT INTO ORDER_PRODUCTS VALUES(?, ?, ?)";
		//Adjust quantities of products accordingly
		String stmt1 = "UPDATE INVENTORY SET ProductStockAmount = ProductStockAmount - ? ";
		stmt1 += "WHERE ProductID = ?";
		p = conn.prepareStatement(stmt);
		PreparedStatement p1 = conn.prepareStatement(stmt1);
		p.clearParameters();
		p.setInt(1, orderID);
		p1.clearParameters();
		// Execute updates to order_products and adjust stock
		for (int i = 0; i < productIDs.length; i++) {
			System.out.println("Still running");
			p.setInt(2, productIDs[i]);
			p.setInt(3, quantities[i]);
			p.executeUpdate();

			p1.setInt(1, quantities[i]);
			p1.setInt(2, productIDs[i]);
			p1.executeUpdate();
		}

		//Update Collections table
		stmt = "INSERT INTO COLLECTIONS VALUES(?, ?, ?, ?)";
		p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setInt(1, orderID);
		p.setString(2, fName);
		p.setString(3, lName);
		p.setString(4, collectionDate);

		System.out.println("Still running");
		//Update staff order table
		stmt = "INSERT INTO STAFF_ORDERS VALUES(?, ?)";
		p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setInt(1, staffID);
		p.setInt(2, orderID);
		p.executeUpdate();

		// Print out updated quantities
		printQuantities(conn, productIDs);
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
	public static void option3(Connection conn, int[] productIDs, int[] quantities, String orderDate, String deliveryDate, String fName, String lName,
				   String house, String street, String city, int staffID) throws SQLException
	{
		//Insert into ORDERS table
		System.out.println(orderDate);
		String stmt = "INSERT INTO ORDERS (OrderType, OrderCompleted, OrderPlaced) ";
		stmt += "VALUES('Collection', 1, ?)";
		PreparedStatement p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setString(1, orderDate);
		p.executeUpdate();

		//Return newly created orderID
		stmt = "SELECT MAX (OrderID) FROM ORDERS";
		Statement s = conn.createStatement();
		ResultSet rs =  s.executeQuery(stmt);
		rs.next();
		int orderID = rs.getInt(1);
		System.out.println(orderID);
		//Insert into ORDER_PRODUCTS for all ordered products
		stmt = "INSERT INTO ORDER_PRODUCTS VALUES(?, ?, ?)";
		//Adjust quantities of products accordingly
		String stmt1 = "UPDATE INVENTORY SET ProductStockAmount = ProductStockAmount - ? ";
		stmt1 += "WHERE ProductID = ?";
		p = conn.prepareStatement(stmt);
		PreparedStatement p1 = conn.prepareStatement(stmt1);
		p.clearParameters();
		p.setInt(1, orderID);
		p1.clearParameters();
		// Execute updates to order_products and adjust stock
		for (int i = 0; i < productIDs.length; i++) {
			System.out.println("Still running");
			p.setInt(2, productIDs[i]);
			p.setInt(3, quantities[i]);
			p.executeUpdate();

			p1.setInt(1, quantities[i]);
			p1.setInt(2, productIDs[i]);
			p1.executeUpdate();
		}

		//Update Deliveries table
		stmt = "INSERT INTO DELIVERIES VALUES(?, ?, ?, ?, ?, ?, ?)";
		p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setInt(1, orderID);
		p.setString(2, fName);
		p.setString(3, lName);
		p.setString(4, house);
		p.setString(5, street);
		p.setString(6, city);
		p.setString(7, deliveryDate);


		//Update staff order table
		stmt = "INSERT INTO STAFF_ORDERS VALUES(?, ?)";
		p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setInt(1, staffID);
		p.setInt(2, orderID);
		p.executeUpdate();

		// Print out updated quantities
		printQuantities(conn, productIDs);
	}

	/**
	* @param conn An open database connection 
	*/
	public static void option4(Connection conn) throws SQLException
	{
		// SQL Processing to sum together all occurences of a productID then multiply by value
		String stmt = "SELECT DISTINCT ProductID, ProductDesc, ";
		stmt += "SUM(productQuantity) ";
		stmt += "OVER(PARTITION BY ProductID) * ProductPrice AS TotalValueSold ";
		stmt += "FROM INVENTORY NATURAL JOIN ORDER_PRODUCTS ";
		stmt += "ORDER BY TotalValueSold DESC";

		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(stmt);

		System.out.printf("ProductID,\tProductDesc,\t\t\tTotalValueSold\n");
		while (rs.next()) {
			System.out.printf("%d\t\t%-30s\t£%-10.2f\n", rs.getInt(1), rs.getString(2), rs.getFloat(3));
		}
	}

	/**
	* @param conn An open database connection 
	* @param date The target date to test collection deliveries against
	*/
	public static void option5(Connection conn, String date)
	{
		
		//Find records that need to be removed
		String stmt = "SELECT OrderID FROM ORDERS NATURAL JOIN COLLECTIONS ";
		stmt += "WHERE OrderCompleted = 0 AND OrderType = 'Collection' ";
		stmt += "AND CollectionDate < (TO_DATE(?, 'DD-MON-YY') - 8)";

		PreparedStatement p = conn.prepareStatement(stmt);
		p.clearParameters();
		p.setString(1, date);
		ResultSet rs = p.executeQuery();

		// Statement to re-adjust stock before deletion
		stmt = "UPDATE INVENTORY SET ProductStockAmount = ProductStockAmount + ";
		stmt += "(SELECT productQuantity FROM ORDER_PRODUCTS WHERE ";
		stmt += "ORDER_PRODUCTS.ProductID = INVENTORY.ProductID AND OrderID = ?) ";
		stmt += "WHERE ProductID IN (SELECT ProductID FROM ORDER_PRODUCTS WHERE ";
		stmt += "OrderID = ?)";




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
		String input;

		while((choice = menu()) != 0) {
			switch(choice) {
				case 1 : ;
					option1(conn);
					break;
				case 2 : ;
					option2(conn);
					break;
				case 3 : ;
					option3(conn);
					break;
				case 4 : ;
					try { option4(conn); }
					catch (SQLException e) {
						throw new SQLException(e);
					}
					break;
				case 5 : ;
					while(!isDate(input = readEntry("Enter the date: "))) {
						System.out.println("Please enter a valid date");		                                                                                                                                                                                                                                             
					}
					try {option5(conn, input)}
					catch (SQLException e) {
						throw new SQLException(e);
					}
			}
		}

		conn.close();
	}

	public static void option1(Connection conn) throws SQLException{
		ArrayList<Integer> productID = new ArrayList<>();
		ArrayList<Integer> productQuantity = new ArrayList<>();
		String date;
		int staffID;
		String input;

		do {
			while(!isNumeric(input = readEntry("Enter a product ID: "))) {
				
				System.out.println("Please enter a numeric product ID");
			}
			productID.add(Integer.valueOf(input));

			while(!isNumeric(input = readEntry("Enter the quantity sold: ")) ||
				Integer.valueOf(input) < 1) {

				System.out.println("Please enter a numeric product quantity: ");
			}
			productQuantity.add(Integer.valueOf(input));

		} while(readEntry("Is there another product in the order: ").compareTo("Y") == 0);

		while(!isDate(input = readEntry("Enter the date sold: "))) {
			System.out.println("Please enter a valid date");		                                                                                                                                                                                                                                             
		}
		date = input;

		while(!isNumeric(input = readEntry("Enter your staff ID: "))){
			System.out.println("Please enter a numeric staff ID");
		}
		staffID = Integer.valueOf(input);
		try {
			option1(conn, toPrimitive(productID), toPrimitive(productQuantity), date, staffID);
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public static void option2(Connection conn) throws SQLException{
		ArrayList<Integer> productID = new ArrayList<>();
		ArrayList<Integer> productQuantity = new ArrayList<>();
		String date;
		String collectionDate;
		String fName;
		String lName;
		int staffID;
		String input;
		//Collect individual product information
		do {
			while(!isNumeric(input = readEntry("Enter a product ID: "))) {
				
				System.out.println("Please enter a numeric product ID");
			}
			productID.add(Integer.valueOf(input));

			while(!isNumeric(input = readEntry("Enter the quantity sold: ")) ||
				Integer.valueOf(input) < 1) {

				System.out.println("Please enter a numeric product quantity: ");
			}
			productQuantity.add(Integer.valueOf(input));

		} while(readEntry("Is there another product in the order: ") == "Y");

		// Collect selling date
		while(!isDate(input = readEntry("Enter the date sold: "))) {
			System.out.println("Please enter a valid date");		                                                                                                                                                                                                                                             
		}
		date = input;

		// Collect collection date
		while(!isDate(input = readEntry("Enter the date of collection: "))) {
			System.out.println("Please enter a valid date");		                                                                                                                                                                                                                                             
		}
		collectionDate = input;

		// Collect first name
		while(isEmpty(input = readEntry("Enter the first name of the collector: "))) {
			System.out.println("Please enter a name");		                                                                                                                                                                                                                                             
		}
		fName = input;

		// Collect last name
		while(isEmpty(input = readEntry("Enter the last name of the collector: "))) {
			System.out.println("Please enter a name");		                                                                                                                                                                                                                                             
		}
		lName = input;

		// Collect staff ID
		while(!isNumeric(input = readEntry("Enter your staff ID: "))){
			System.out.println("Please enter a numeric staff ID");
		}
		staffID = Integer.valueOf(input);

		// Execute storage operation
		try {
			option2(conn, toPrimitive(productID), toPrimitive(productQuantity), date, collectionDate, fName, lName, staffID);
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
	}

		public static void option3(Connection conn) throws SQLException{
		ArrayList<Integer> productID = new ArrayList<>();
		ArrayList<Integer> productQuantity = new ArrayList<>();
		String date;
		String deliveryDate;
		String fName;
		String lName;
		String house;
		String street;
		String city;
		int staffID;
		String input;
		//Collect individual product information
		do {
			while(!isNumeric(input = readEntry("Enter a product ID: "))) {
				
				System.out.println("Please enter a numeric product ID");
			}
			productID.add(Integer.valueOf(input));

			while(!isNumeric(input = readEntry("Enter the quantity sold: ")) ||
				Integer.valueOf(input) < 1) {

				System.out.println("Please enter a numeric product quantity: ");
			}
			productQuantity.add(Integer.valueOf(input));

		} while(readEntry("Is there another product in the order: ") == "Y");

		// Collect selling date
		while(!isDate(input = readEntry("Enter the date sold: "))) {
			System.out.println("Please enter a valid date");		                                                                                                                                                                                                                                             
		}
		date = input;

		// Collect collection date
		while(!isDate(input = readEntry("Enter the date of delivery: "))) {
			System.out.println("Please enter a valid date");		                                                                                                                                                                                                                                             
		}
		deliveryDate = input;

		// Collect first name
		while(isEmpty(input = readEntry("Enter the first name of the recipient: "))) {
			System.out.println("Please enter a name");		                                                                                                                                                                                                                                             
		}
		fName = input;

		// Collect last name
		while(isEmpty(input = readEntry("Enter the last name of the recipient: "))) {
			System.out.println("Please enter a name");		                                                                                                                                                                                                                                             
		}
		lName = input;

		// Collect house name/number
		while(isEmpty(input = readEntry("Enter the house name/no.: "))) {
			System.out.println("Please enter a house name or number");		                                                                                                                                                                                                                                             
		}
		house = input;

		// Collect street
		while(isEmpty(input = readEntry("Enter the street: "))) {
			System.out.println("Please enter a street");		                                                                                                                                                                                                                                             
		}
		street = input;

		// Collect city
		while(isEmpty(input = readEntry("Enter the city: "))) {
			System.out.println("Please enter a city");		                                                                                                                                                                                                                                             
		}
		city = input;

		// Collect staff ID
		while(!isNumeric(input = readEntry("Enter your staff ID: "))){
			System.out.println("Please enter a numeric staff ID");
		}
		staffID = Integer.valueOf(input);

		// Execute storage operation
		try {
			option3(conn, toPrimitive(productID), toPrimitive(productQuantity), date, deliveryDate, 
				fName, lName, house, street, city, staffID);
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
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
		if (s.length() == 0) return false;

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

	public static boolean isEmpty(String s) {
		return s.length() == 0;
	}



	public static void printQuantities(Connection conn, int[] productIDs) throws SQLException {
		// Statement to select ID and quantity with given IDs
		String stmt = "SELECT ProductID, ProductStockAmount FROM INVENTORY WHERE ProductID IN (";
		//Define suitable number of bind characters
		for (int i = 0; i < productIDs.length; i++) {
			stmt += "?, ";
		}
		//Tidy statement string
		stmt = stmt.substring(0, stmt.length() - 2);
		stmt += ")";
		//Create prepared statement
		PreparedStatement p = conn.prepareStatement(stmt);
		p.clearParameters();
		//Bind each given id
		for (int i = 1; i <= productIDs.length; i++) {
			p.setInt(i, productIDs[i - 1]);		
		}
		//Execute query to generate result set
		ResultSet rs = p.executeQuery();
		//Print table body
		while(rs.next()){
			System.out.printf("Product ID %d is now at %d.\n", rs.getInt(1), rs.getInt(2));
		}


	}
}
