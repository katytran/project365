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
