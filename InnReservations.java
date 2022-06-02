import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class InnReservations {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            InnReservations rs = new InnReservations();

            while (true) {
                System.out.println("Enter your choice: ");
                System.out.println("1. Show Rooms and Rates");
                System.out.println("2. Reserve a room");
                System.out.println("3. Reservation Change");
                System.out.println("4. Reservation Cancellation");
                System.out.println("5. Detailed Reservation Information");
                System.out.println("6. Show revenue");
                System.out.println("7. Exit the program");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

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

        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

            }
        }
        
        return;

    }

    private void prompt2() throws SQLException {

        System.out.println("Reserve a room\n");

        // Step 0: Load MySQL JDBC Driver
        // No longer required as of JDBC 2.0 / Java 6
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

            }
        }

    }

    private void prompt3() throws SQLException {

        System.out.println("Reservation Change\n");

        // Step 0: Load MySQL JDBC Driver
        // No longer required as of JDBC 2.0 / Java 6
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

            }
        }

    }

    private void prompt4() throws SQLException {

        System.out.println("Reservation Cancellation\n");

        // Step 0: Load MySQL JDBC Driver
        // No longer required as of JDBC 2.0 / Java 6
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

            }
        }

    }

    private void prompt5() throws SQLException {

        System.out.println("Detailed Reservation Information\n");

        // Step 0: Load MySQL JDBC Driver
        // No longer required as of JDBC 2.0 / Java 6
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

            }
        }

    }

    private void prompt6() throws SQLException {

        System.out.println("Show revenue\n");

        // Step 0: Load MySQL JDBC Driver
        // No longer required as of JDBC 2.0 / Java 6
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException ex) {
            System.err.println("Unable to load JDBC Driver");
            System.exit(-1);
        }

        // Step 1: Establish connection to RDBMS
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "select * from lab7_reservations";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

            }
        }

    }

}
