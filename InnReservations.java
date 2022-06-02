import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Map;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.InputMismatchException;

/*
Introductory JDBC examples based loosely on the BAKERY dataset from CSC 365 labs.

-- MySQL setup:
drop table if exists hp_goods, hp_customers, hp_items, hp_receipts;
create table hp_goods as select * from BAKERY.goods;
create table hp_customers as select * from BAKERY.customers;
create table hp_items as select * from BAKERY.items;
create table hp_receipts as select * from BAKERY.receipts;

grant all on amigler.hp_goods to hasty@'%';
grant all on amigler.hp_customers to hasty@'%';
grant all on amigler.hp_items to hasty@'%';
grant all on amigler.hp_receipts to hasty@'%';

-- Shell init:
export CLASSPATH=$CLASSPATH:mysql-connector-java-8.0.16.jar:.
export HP_JDBC_URL=jdbc:mysql://db.labthreesixfive.com/winter2020?autoReconnect=true\&useSSL=false
export HP_JDBC_USER=jmustang
export HP_JDBC_PW=...
 */
public class InnReservations {

    Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
	try {
	    InnReservations hp = new InnReservations();
        hp.demoNum();            
            
            
	} catch (SQLException e) {
	    System.err.println("SQLException: " + e.getMessage());
	} catch (Exception e2) {
            System.err.println("Exception: " + e2.getMessage());
        }
    }

    private void demoNum() throws SQLException {
        boolean end = false;
        int option = -1;

        while (!end) {
            printMenu();
            if (sc.hasNextInt()) {
                option = sc.nextInt();
            } else {
                sc.next();
                printInputError();
                continue;
            }

            switch (option) {
                case 1:
                    func1();
                    break;
                case 2:
                    func2();
                    break;
                case 3:
                    func3();
                    break;
                case 4:
                    func4();
                    break;
                case 5:
                    func5();
                    break;
                case 6:
                    func6();
                    break;
                case 7:
                    end = true;
                    break;
                default:
                    printInputError();
                    break;
            }
        }
        sc.close();
    }

    private void printMenu() {
        System.out.println("Please choose one of the following options: ");
        System.out.println("1: Rooms and Rates");
        System.out.println("2: Reservations");
        System.out.println("3: Reservation Change");
        System.out.println("4: Reservation Cancellation");
        System.out.println("5: Detailed Reservation Information");
        System.out.println("6: Revenue");
        System.out.println("7: Exit");
        System.out.print("What would you like to do? Select an option: ");
    }

    private void func1() throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * from lab7_rooms order by RoomName");

        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
        System.getenv("HP_JDBC_USER"),
        System.getenv("HP_JDBC_PW"))) {
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql.toString())) {
                while (rs.next()) {
                    System.out.println(rs.getString("RoomCode"));
                    System.out.println(rs.getString("RoomName"));

                }
            } catch (SQLException e) { e.printStackTrace(); }
        } 
    }

    private void func2() {
        
    }

    private void func3() throws SQLException{
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
        System.getenv("HP_JDBC_USER"),
        System.getenv("HP_JDBC_PW"))) {
            conn.setAutoCommit(false);

            int resCode = -1; 
            while (true) {
                try {
                    System.out.print("Enter reservation code: ");
                    resCode = sc.nextInt(); 
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Enter an integer reservation code");
                    sc.nextLine();
                }  
            }
            PreparedStatement stmt = conn.prepareStatement("SELECT * from lab7_reservations where CODE=?");
            stmt.setInt(1, resCode);

            ResultSet rs = stmt.executeQuery(); 
            if (!rs.next()) {
                System.out.println("That reservation wasn't found.");
                return;
            }

            System.out.println("Would you like to change any of the following information? Leave blank if not.");
            System.out.print("First Name: ");
            String fName = sc.nextLine();
            System.out.print("Last Name: ");
            String lName = sc.nextLine();
            System.out.print("Begin Date: ");
            String checkIn = sc.nextLine();
            System.out.print("End Date: ");
            String checkOut = sc.nextLine();
            System.out.print("Number of kids: ");
            int kids = sc.nextInt(); 
            System.out.print("Number of adults: ");
            int adults = sc.nextInt(); 

        }
    }

    private void func4() throws SQLException{
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
        System.getenv("HP_JDBC_USER"),
        System.getenv("HP_JDBC_PW"))) {
            conn.setAutoCommit(false);

            int resCode = -1; 
            while (true) {
                try {
                    System.out.print("Enter reservation code: ");
                    resCode = sc.nextInt(); 
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Enter an integer reservation code");
                    sc.nextLine();
                }  
            }
            PreparedStatement stmt = conn.prepareStatement("SELECT * from lab7_reservations where CODE=?");
            stmt.setInt(1, resCode);

            ResultSet rs = stmt.executeQuery(); 
            if (!rs.next()) {
                System.out.println("That reservation wasn't found.");
                return;
            } 

            System.out.println("Here is your reservation:\nRoom: " + rs.getString("Room"));
            System.out.println("CheckIn: " + rs.getDate("CheckIn"));
            System.out.println("CheckOut: " + rs.getDate("CheckOut"));

            System.out.print("Would you like to cancel your reservation? (y/n): ");
            sc.nextLine();
            String answer = sc.nextLine();

            if (answer.toLowerCase().charAt(0) == 'y') {
                try (PreparedStatement delStmt = conn.prepareStatement("DELETE from lab7_reservations where CODE=?")) {
                    delStmt.setInt(1, resCode);
                    delStmt.executeUpdate();
                    conn.commit();
                    System.out.println("Reservation cancelled");
                } catch (SQLException e) {
                    System.out.println("We ran into a problem.");
                    conn.rollback();
                }
            } else {
                System.out.println("Reservation not cancelled.");
            }

        } 
    }

    private void func5() {
        
    }

    private void func6() {

    }

    private void printInputError() {
        System.out.println("\n Input an integer between 1-6");
    }



    // Demo1 - Establish JDBC connection, execute DDL statement
    private void demo1() throws SQLException {

        System.out.println("demo1: Add AvailUntil column to hp_goods table\r\n");
        
	// Step 0: Load MySQL JDBC Driver
	// No longer required as of JDBC 2.0  / Java 6
	try{
	    Class.forName("com.mysql.jdbc.Driver");
	    System.out.println("MySQL JDBC Driver loaded");
	} catch (ClassNotFoundException ex) {
	    System.err.println("Unable to load JDBC Driver");
	    System.exit(-1);
	}

	// Step 1: Establish connection to RDBMS
	try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
							   System.getenv("HP_JDBC_USER"),
							   System.getenv("HP_JDBC_PW"))) {
	    // Step 2: Construct SQL statement
	    String sql = "ALTER TABLE hp_goods ADD COLUMN AvailUntil DATE";

	    // Step 3: (omitted in this example) Start transaction

	    try (Statement stmt = conn.createStatement()) {

		// Step 4: Send SQL statement to DBMS
		boolean exRes = stmt.execute(sql);
		
		// Step 5: Handle results
		System.out.format("Result from ALTER: %b %n", exRes);
	    }

	    // Step 6: (omitted in this example) Commit or rollback transaction
	}
	// Step 7: Close connection (handled by try-with-resources syntax)
    }
    

    // Demo2 - Establish JDBC connection, execute SELECT query, read & print result
    private void demo2() throws SQLException {

        System.out.println("demo2: List content of hp_goods table\r\n");
        
	// Step 1: Establish connection to RDBMS
	try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
							   System.getenv("HP_JDBC_USER"),
							   System.getenv("HP_JDBC_PW"))) {
	    // Step 2: Construct SQL statement
	    String sql = "SELECT * FROM hp_goods";

	    // Step 3: (omitted in this example) Start transaction

	    // Step 4: Send SQL statement to DBMS
	    try (Statement stmt = conn.createStatement();
		 ResultSet rs = stmt.executeQuery(sql)) {

		// Step 5: Receive results
		while (rs.next()) {
		    String flavor = rs.getString("Flavor");
		    String food = rs.getString("Food");
		    float price = rs.getFloat("price");
		    System.out.format("%s %s ($%.2f) %n", flavor, food, price);
		}
	    }

	    // Step 6: (omitted in this example) Commit or rollback transaction
	}
	// Step 7: Close connection (handled by try-with-resources syntax)
    }


    // Demo3 - Establish JDBC connection, execute DML query (UPDATE)
    // -------------------------------------------
    // Never (ever) write database code like this!
    // -------------------------------------------
    private void demo3() throws SQLException {

        System.out.println("demo3: Populate AvailUntil column using string concatenation\r\n");
        
	// Step 1: Establish connection to RDBMS
	try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
							   System.getenv("HP_JDBC_USER"),
							   System.getenv("HP_JDBC_PW"))) {
	    // Step 2: Construct SQL statement
	    Scanner scanner = new Scanner(System.in);
	    System.out.print("Enter a flavor: ");
	    String flavor = scanner.nextLine();
	    System.out.format("Until what date will %s be available (YYYY-MM-DD)? ", flavor);
	    String availUntilDate = scanner.nextLine();

	    // -------------------------------------------
	    // Never (ever) write database code like this!
	    // -------------------------------------------
	    String updateSql = "UPDATE hp_goods SET AvailUntil = '" + availUntilDate + "' " +
		               "WHERE Flavor = '" + flavor + "'";

	    // Step 3: (omitted in this example) Start transaction
	    
	    try (Statement stmt = conn.createStatement()) {
		
		// Step 4: Send SQL statement to DBMS
		int rowCount = stmt.executeUpdate(updateSql);
		
		// Step 5: Handle results
		System.out.format("Updated all '%s' flavored pastries (%d records) %n", flavor, rowCount);		
	    }

	    // Step 6: (omitted in this example) Commit or rollback transaction
	    
	}
	// Step 7: Close connection (handled implcitly by try-with-resources syntax)
    }


    // Demo4 - Establish JDBC connection, execute DML query (UPDATE) using PreparedStatement / transaction    
    private void demo4() throws SQLException {

        System.out.println("demo4: Populate AvailUntil column using PreparedStatement\r\n");
        
	// Step 1: Establish connection to RDBMS
	try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
							   System.getenv("HP_JDBC_USER"),
							   System.getenv("HP_JDBC_PW"))) {
	    // Step 2: Construct SQL statement
	    Scanner scanner = new Scanner(System.in);
	    System.out.print("Enter a flavor: ");
	    String flavor = scanner.nextLine();
	    System.out.format("Until what date will %s be available (YYYY-MM-DD)? ", flavor);
	    LocalDate availDt = LocalDate.parse(scanner.nextLine());
	    
	    String updateSql = "UPDATE hp_goods SET AvailUntil = ? WHERE Flavor = ?";

	    // Step 3: Start transaction
	    conn.setAutoCommit(false);
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
		
		// Step 4: Send SQL statement to DBMS
		pstmt.setDate(1, java.sql.Date.valueOf(availDt));
		pstmt.setString(2, flavor);
		int rowCount = pstmt.executeUpdate();
		
		// Step 5: Handle results
		System.out.format("Updated %d records for %s pastries%n", rowCount, flavor);

		// Step 6: Commit or rollback transaction
		conn.commit();
	    } catch (SQLException e) {
		conn.rollback();
	    }

	}
	// Step 7: Close connection (handled implcitly by try-with-resources syntax)
    }



    // Demo5 - Construct a query using PreparedStatement
    private void demo5() throws SQLException {

        System.out.println("demo5: Run SELECT query using PreparedStatement\r\n");
        
	// Step 1: Establish connection to RDBMS
	try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
							   System.getenv("HP_JDBC_USER"),
							   System.getenv("HP_JDBC_PW"))) {
	    Scanner scanner = new Scanner(System.in);
	    System.out.print("Find pastries with price <=: ");
	    Double price = Double.valueOf(scanner.nextLine());
	    System.out.print("Filter by flavor (or 'Any'): ");
	    String flavor = scanner.nextLine();

	    List<Object> params = new ArrayList<Object>();
	    params.add(price);
	    StringBuilder sb = new StringBuilder("SELECT * FROM hp_goods WHERE price <= ?");
	    if (!"any".equalsIgnoreCase(flavor)) {
		sb.append(" AND Flavor = ?");
		params.add(flavor);
	    }
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(sb.toString())) {
		int i = 1;
		for (Object p : params) {
		    pstmt.setObject(i++, p);
		}

		try (ResultSet rs = pstmt.executeQuery()) {
		    System.out.println("Matching Pastries:");
		    int matchCount = 0;
		    while (rs.next()) {
			System.out.format("%s %s ($%.2f) %n", rs.getString("Flavor"), rs.getString("Food"), rs.getDouble("price"));
			matchCount++;
		    }
		    System.out.format("----------------------%nFound %d match%s %n", matchCount, matchCount == 1 ? "" : "es");
		}
	    }

	}
    }
    

}
