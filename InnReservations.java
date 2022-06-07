import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
import java.util.Locale;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.sql.Date;
import java.text.*;

public class InnReservations {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            InnReservations rs = new InnReservations();

            while (true) {
                rs.printMenu();
                int choice = sc.nextInt();

                switch (choice) {
                case 1:
                    rs.prompt1();
                    break;
                case 2:
                    rs.prompt2();
                    break;
                case 3:
                    rs.prompt3();
                    break;
                case 4:
                    rs.prompt4();
                    break;
                case 5:
                    rs.prompt5();
                    break;
                case 6:
                    rs.prompt6();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid Input");
                }

            }

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        } catch (Exception e2) {
            System.err.println("Exception: " + e2.getMessage());
        }

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

    private void prompt1() throws SQLException {

        System.out.println("Show Rooms and Rates\n");

        // Step 0: Load MySQL JDBC Driver
        // No longer required as of JDBC 2.0 / Java 6
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

       
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * from lab7_rooms order by RoomName");

        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql.toString())) {
                while (rs.next()) {
                    System.out.println(rs.getString("RoomCode"));
                    System.out.println(rs.getString("RoomName"));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } 

    }

    private void prompt2() throws SQLException {

        System.out.println("Reserve a room\n");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

    }

    private void prompt3() throws SQLException {

        System.out.println("Reservation Change\n");

        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            conn.setAutoCommit(false);

            int resCode = -1;
            while (true) {
                try {
                    System.out.print("Enter reservation code: ");
                    resCode = sc.nextInt();
                    sc.nextLine();
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
                System.out.println("That reservation wasn't found.\n");
                return;
            }

            System.out.println("Would you like to change any of the following information? Leave blank if not.");
            System.out.print("First Name: ");
            String fName = sc.nextLine();
            if (fName.trim().length() > 0) { //checks if it was blank, if not update it
                try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set FirstName=? where CODE=?")) {
                    updStmt.setString(1, fName);
                    updStmt.setInt(2, resCode);
                    updStmt.executeUpdate();
                    System.out.printf("Updated First Name: %s \n", fName);
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
            } else {
                System.out.println("No updated first name.");
            }
            System.out.print("Last Name: ");
            String lName = sc.nextLine();
            if (lName.trim().length() > 0) { //checks if it was blank, if not update it
                try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set LastName=? where CODE=?")) {
                    updStmt.setString(1, lName);
                    updStmt.setInt(2, resCode);
                    updStmt.executeUpdate();
                    System.out.printf("Updated Last Name: %s \n", lName);
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
            } else {
                System.out.println("No updated last name.");
            }
            System.out.print("RoomCode: ");
            String roomCode = sc.nextLine().toUpperCase();
            if (roomCode.trim().length() > 0) {
                PreparedStatement stmtRoom = conn.prepareStatement("SELECT * from lab7_reservations where Room=?");
                stmtRoom.setString(1, roomCode);
                ResultSet rsRoom = stmtRoom.executeQuery();
                if (!rsRoom.next()) {
                    System.out.println("That room code wasn't found. Nothing will update. Please try again.\n");
                    return;
                }
                try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set Room=? where CODE=?")) {
                    updStmt.setString(1, roomCode);
                    updStmt.setInt(2, resCode);
                    updStmt.executeUpdate();
                    System.out.printf("Updated Room: %s \n", roomCode);
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
            } else {
                System.out.println("No updated room.");
            }

            //make sure that datediff(checkout, checkin) > 0
            //either with both new dates or one new date with an old date
            //open room from those dates
            System.out.print("Checkin Date [YYYY-MM-DD]: ");
            String checkIn = sc.nextLine();
            Date CI; //new or original checkin date
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            if (checkIn.trim().length() > 0) {
                try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set CheckIn=? where CODE=?")) {
                    try {
                        java.sql.Date d = new Date(df.parse(checkIn).getTime());
                        updStmt.setDate(1, d);
                        updStmt.setInt(2, resCode);
                        updStmt.executeUpdate();
                        CI = d;
                    } catch (ParseException e) {
                        System.err.println("invalid date format.");
                        return;
                    }
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
            } else {
                try (PreparedStatement getCI = conn.prepareStatement("SELECT CheckIn from lab7_reservations where CODE=?")){
                    getCI.setInt(1, resCode);
                    ResultSet rsCI = getCI.executeQuery();
                    rsCI.next();
                    CI = rsCI.getDate("CheckIn");
                
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
                System.out.println("No updated checkin.");
            }
            //prints a day off, just add one to it
            System.out.println(df.format(CI));

            System.out.print("Checkout Date [YYYY-MM-DD]: ");
            String checkOut = sc.nextLine();
            Date CO; //new or original checkout date
            if (checkOut.trim().length() > 0) {
                try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set Checkout=? where CODE=?")) {
                    try {
                        Date d = new Date(df.parse(checkOut).getTime());
                        updStmt.setDate(1, d);
                        updStmt.setInt(2, resCode);
                        updStmt.executeUpdate();
                        CO = d;
                    } catch (ParseException e) {
                        System.err.println("invalid date format.");
                        return;
                    }
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
            } else {
                try (PreparedStatement getCO = conn.prepareStatement("SELECT Checkout from lab7_reservations where CODE=?")){
                    getCO.setInt(1, resCode);
                    ResultSet rsCO = getCO.executeQuery();
                    rsCO.next();
                    CO = rsCO.getDate("Checkout");
                
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
                System.out.println("No updated checkout.");
            }
            System.out.println(CO);
            // TODO: check if the checkin is before the checkout
            // TODO: check if there is a room available for the given dates

            System.out.print("Number of kids (integer): ");
            String kidsS = sc.nextLine();
            int oldkids;

            if (kidsS.trim().length() > 0) { //if not whitespace
                try {
                    int kids = Integer.parseInt(kidsS); // try to turn into an integer
                    if (kids < 0) {
                        System.out.println("That wasn't a valid integer value. Please try again.\n");
                        return;
                    } else {
                        oldkids = kids; //oldkids will always have a value, want to use this later for error checking
                    }
                } catch (NumberFormatException e) { // handle not an int 
                    System.out.println("That wasn't an integer value. Please try again.\n");
                    return;
                }          
                try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set Kids=? where CODE=?")) {
                    updStmt.setInt(1, oldkids);
                    updStmt.setInt(2, resCode);
                    updStmt.executeUpdate();
                    System.out.printf("Updated Amount of kids: %d \n", oldkids);
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
    
            } else { //if it is whitespace, get original number of kids
                try (PreparedStatement getKids = conn.prepareStatement("SELECT Kids from lab7_reservations where CODE=?")){
                    getKids.setInt(1, resCode);
                    ResultSet rsKids = getKids.executeQuery();
                    rsKids.next();
                    oldkids = rsKids.getInt("Kids");
                
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
                System.out.println("No updated kids.");
            }

            // use oldkids here, do similar with adults
            System.out.print("Number of adults (integer): ");
            String adults = sc.nextLine();
            int oldadults;

            if (adults.trim().length() > 0) { //if not whitespace
                try {
                    int adult = Integer.parseInt(adults); //try to turn into an integer
                    if (adult < 0) {
                        System.out.println("That wasn't a valid integer value. Please try again.\n");
                        return;
                    } else {
                        oldadults = adult; 
                    }
                } catch (NumberFormatException e) { // handle not an int 
                    System.out.println("That wasn't an integer value. Please try again.\n");
                    return;
                }          
                try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set Adults=? where CODE=?")) {
                    updStmt.setInt(1, oldadults);
                    updStmt.setInt(2, resCode);
                    updStmt.executeUpdate();
                    System.out.printf("Updated Amount of adults: %d \n", oldadults);
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
    
            } else {
                try (PreparedStatement getAdult = conn.prepareStatement("SELECT Adults from lab7_reservations where CODE=?")){
                    getAdult.setInt(1, resCode);
                    ResultSet rsAdult = getAdult.executeQuery();
                    rsAdult.next();
                    oldadults = rsAdult.getInt("Adults");
                
                } catch (SQLException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                    conn.rollback();
                    return;
                }
                System.out.println("No updated adults.");
            }
            int totalOcc = oldadults+oldkids;
            int maxOccupancy = -1;
            String getMaxOcc = "select maxOcc from lab7_rooms join lab7_reservations on Room = RoomCode where CODE = ?";
            
            //want to compare totalOcc to maxOccupancy
            try (PreparedStatement maxStmt = conn.prepareStatement(getMaxOcc) ) {
                maxStmt.setInt(1, resCode);
                ResultSet rsmaxOcc = maxStmt.executeQuery();
                rsmaxOcc.next();
                maxOccupancy = rsmaxOcc.getInt("maxOcc");
            } catch (SQLException e) {
                System.out.println("We ran into a problem. Try again.\n");
                conn.rollback();
                return;
            }
            if (totalOcc > maxOccupancy) {
                System.out.printf("The amount of guests you want, %d, exceeds the maximum occupants for the room, %d. Please try again.\n\n", totalOcc, maxOccupancy);
                conn.rollback();
                return;
            }
            //commit all updates after error checking
            conn.commit();
            System.out.println("Your reservation has been successfully updated!\n");
        }
        
    }

    private void prompt4() throws SQLException {

        System.out.println("Reservation Cancellation\n");

        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
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
                    System.out.println("Reservation cancelled");
                } catch (SQLException e) {
                    System.out.println("We ran into a problem.");
                    conn.rollback();
                }
            } else {
                System.out.println("Reservation not cancelled.");
            }
            conn.commit();
        }
        
        System.out.print("\n");

    }

    private void prompt5() throws SQLException {

        System.out.println("Detailed Reservation Information\n");
        /*
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";
            // Step 3: (omitted in this example) Start transaction
            try (Statement stmt = conn.createStatement()) {
            }
        }
        */

    }

    private void prompt6() throws SQLException {

        System.out.println("Show revenue\n");
        /*
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";
            // Step 3: (omitted in this example) Start transaction
            try (Statement stmt = conn.createStatement()) {
            }
        }
        */

    }

}