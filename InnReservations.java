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

        // try {
        //     Class.forName("com.mysql.jdbc.Driver");
        //     System.out.println("MySQL JDBC Driver loaded");
        // } catch (ClassNotFoundException ex) {
        //     System.err.println("Unable to load JDBC Driver");
        //     System.exit(-1);
        // }
        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            conn.setAutoCommit(false);
            sc.nextLine();
            String fName;
            while(true) {
                System.out.print("First Name: ");
                fName = sc.nextLine();
                if (fName.trim().length() > 0) {
                    break;
                } else {
                    System.out.println("Enter a valid first name.");
                }
            }
            //validates last name
            String lName;
            while(true) {
                System.out.print("Last Name: ");
                lName = sc.nextLine();
                if (lName.trim().length() > 0) {
                    break;
                } else {
                    System.out.println("Enter a valid last name.");
                }
            }
            //validates roomcode
            String roomCode;
            while(true) {
                System.out.print("RoomCode (enter \'any\' if no preference): ");
                roomCode = sc.nextLine().toUpperCase();
                if (roomCode.trim().length() > 0) {
                    if (roomCode.equals("ANY")) { // no preference
                        break;
                    }
                    PreparedStatement stmtRoom = conn.prepareStatement("SELECT * from lab7_reservations where Room=?");
                    stmtRoom.setString(1, roomCode);
                    ResultSet rsRoom = stmtRoom.executeQuery();
                    if (!rsRoom.next()) {
                        System.out.println("That room code wasn't found. Please try again.");
                    } else {
                        break;
                    }
                }
            }
            String bedType;
            while(true) {
                System.out.print("Bed type (enter \'any\' if no preference): ");
                bedType = sc.nextLine().toUpperCase();
                if (bedType.trim().length() > 0) {
                    if (bedType.equals("ANY")) { // no preference
                        break;
                    }
                    PreparedStatement stmtBed = conn.prepareStatement("SELECT * from lab7_rooms where bedType=?");
                    stmtBed.setString(1, bedType);
                    ResultSet rsBeds = stmtBed.executeQuery();
                    if (!rsBeds.next()) {
                        System.out.println("That bed type wasn't found. Please try again.");
                    } else {
                        break;
                    }
                }
            }
            Date CI; //checkin
            while(true) {
                System.out.print("Checkin Date [YYYY-MM-DD]: ");
                String checkIn = sc.nextLine();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                if (checkIn.trim().length() > 0) {
                    try {
                        java.sql.Date d = new Date(df.parse(checkIn).getTime());
                        CI = d;
                        break;
                    } catch (ParseException e) {
                        System.err.println("invalid date format.");
                    }
                } 
            }
            Date CO; //checkout
            while(true) {
                System.out.print("Checkout Date [YYYY-MM-DD]: ");
                String checkOut = sc.nextLine();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                if (checkOut.trim().length() > 0) {
                    try {
                        java.sql.Date d = new Date(df.parse(checkOut).getTime());
                        CO = d;
                        break;
                    } catch (ParseException e) {
                        System.err.println("invalid date format.");
                    }
                } 
            }
            long difference_In_Time  = CO.getTime() - CI.getTime();
            // if checkout is the same day as checkin or if checkin is after checkout
            if (difference_In_Time <= 0) {
                System.out.println("That is not a valid interval of checkin and checkout dates. Please try again\n");
                return;
            }
            System.out.print("Number of kids (integer): ");
            String kidsS = sc.nextLine();
            int oldkids;
            while(true) {
                if (kidsS.trim().length() > 0) { //if not whitespace
                    try {
                        int kids = Integer.parseInt(kidsS); // try to turn into an integer
                        if (kids < 0) {
                            System.out.println("That wasn't a valid integer value. Please try again.\n");

                        } else {
                            oldkids = kids; //oldkids will always have a value, want to use this later for error checking
                            break;
                        }
                    } catch (NumberFormatException e) { // handle not an int 
                        System.out.println("That wasn't an integer value. Please try again.\n");
                    }
                }
            }
            System.out.print("Number of adults (integer): ");
            String adults = sc.nextLine();
            int oldadults;
            while(true) {
                if (adults.trim().length() > 0) { 
                    try {
                        int adult = Integer.parseInt(adults); 
                        if (adult < 0) {
                            System.out.println("That wasn't a valid integer value. Please try again.\n");
                        } else {
                            oldadults = adult;
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("That wasn't an integer value. Please try again.\n");
                    }      
                } 
            }
            int totalOcc = oldadults+oldkids;

            StringBuilder sqlMaxOcc = new StringBuilder();
            sqlMaxOcc.append("with maxOccupancy as (");
            sqlMaxOcc.append("    select maxOcc, max(maxOcc) as m");
            sqlMaxOcc.append("    from lab7_rooms");
            sqlMaxOcc.append("    group by maxOcc) ");
            sqlMaxOcc.append(" select m ");
            sqlMaxOcc.append(" from maxOccupancy ");
            sqlMaxOcc.append(" where m = (select max(m) from maxOccupancy)");
            String sql = sqlMaxOcc.toString();
            try (Statement stmt = conn.createStatement(); ResultSet rsMax = stmt.executeQuery(sql)){
                rsMax.next();
                try {
                    int foundMaxOcc = rsMax.getInt("m"); 
                    //System.out.println(foundMaxOcc);
                    if (foundMaxOcc < totalOcc) {
                        System.out.println("The total amount of adults and kids exceeds any rooms maximum occupancy. Please try again.\n");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("We ran into a problem. Try again.\n");
                }     
            } catch (SQLException e) {
                System.out.println("We ran into a problem. Try again.\n");
                conn.rollback();
                return;
            }
            //TODO: pick an exact reservation (1 choice) or 5 most similar reservations
            

            //prompt user for cancel or choice
            System.out.print("Would you like to book a reservation? Type \'y\' for yes or \'c\' to cancel and return to the main menu. ");
            while(true) {
                String ans = sc.nextLine();
                if (ans.toLowerCase().charAt(0) == 'y') {
                    System.out.println("Great! Here are your reservation details:");
                    break;
                } else if (ans.toLowerCase().charAt(0) == 'c') {
                    System.out.println("We understand your decision to not book an Inn. Returning you to the main menu.\n");
                    return;
                } else {
                    System.out.println("That wasn't a valid choice. Please try again.");
                }
            }
            int resNum;
            while (true) {
                try {
                    System.out.print("Enter which reservation you would like to book(1-5): ");
                    resNum = sc.nextInt();
                    sc.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Enter an integer number.");
                    sc.nextLine();
                }
            }
            //print out reservation details, TODO: write function to format this
            switch (resNum) {
                case 1:
                    return;
                case 2:
                    return;
                case 3:
                    return;
                case 4:
                    return;
                case 5:
                    return;
                default: 
                    System.out.println("Invalid integer. Returning to main menu.\n");
            }
            //TODO: create an entry into kspark01.lab7_reservations

            conn.commit();
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
            // // dont need the room change
            // System.out.print("RoomCode: ");
            // String roomCode = sc.nextLine().toUpperCase();
            // if (roomCode.trim().length() > 0) {
            //     PreparedStatement stmtRoom = conn.prepareStatement("SELECT * from lab7_reservations where Room=?");
            //     stmtRoom.setString(1, roomCode);
            //     ResultSet rsRoom = stmtRoom.executeQuery();
            //     if (!rsRoom.next()) {
            //         System.out.println("That room code wasn't found. Nothing will update. Please try again.\n");
            //         return;
            //     }
            //     try (PreparedStatement updStmt = conn.prepareStatement("UPDATE lab7_reservations set Room=? where CODE=?")) {
            //         updStmt.setString(1, roomCode);
            //         updStmt.setInt(2, resCode);
            //         updStmt.executeUpdate();
            //         System.out.printf("Updated Room: %s \n", roomCode);
            //     } catch (SQLException e) {
            //         System.out.println("We ran into a problem. Try again.\n");
            //         conn.rollback();
            //         return;
            //     }
            // } else {
            //     System.out.println("No updated room.");
            // }

            // find roomcode
            String roomCode;
            try (PreparedStatement getRC = conn.prepareStatement("SELECT Room from lab7_reservations where CODE=?")){
                getRC.setInt(1, resCode);
                ResultSet rC = getRC.executeQuery();
                rC.next();
                roomCode = rC.getString("Room");
            
            } catch (SQLException e) {
                System.out.println("We ran into a problem. Try again.\n");
                conn.rollback();
                return;
            }

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

            
            long difference_In_Time  = CO.getTime() - CI.getTime();
            // if checkout is the same day as checkin or if checkin is after checkout
            if (difference_In_Time <= 0) {
                System.out.println("That is not a valid interval of checkin and checkout dates. Please try again\n");
                conn.rollback();
                return;
            }
            // check if there is a room available for the given dates, can checkout on the same checkin but not vice versa
            try (PreparedStatement verify = 
                conn.prepareStatement("select * from lab7_reservations as r1 " + 
                " where r1.CODE <> ? and exists (select 1 from lab7_reservations as r2 " +
                " where r2.CODE <> ? and r1.Room = r2.Room and r2.Room =? and  (" +
                "(? >= r2.CheckIn and ? < r2.Checkout) or " +
                "(? > r2.CheckIn and ? <= r2.Checkout)))")){
                    verify.setInt(1, resCode);
                    verify.setInt(2, resCode);
                    verify.setString(3, roomCode);
                    verify.setDate(4, CI);
                    verify.setDate(5, CI);
                    verify.setDate(6, CO);
                    verify.setDate(7, CO);
                    try (ResultSet rsV = verify.executeQuery()){
                        if(rsV.next()) {
                            System.out.println("The requested date interval has a conflict with another reservation. Please try again.\n");
                            return;
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                        conn.rollback();
                        return;
                    }

            } catch (SQLException e) {
                conn.rollback();
                return;
            }

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

        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            // Step 2: Construct SQL statement
            String sql = "SELECT * FROM kspark01.lab7_reservations";
            System.out.println("in.");

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

            }
        }

    }

    private void prompt6() throws SQLException {

        System.out.println("Show revenue\n");

        try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"), System.getenv("HP_JDBC_USER"),
                System.getenv("HP_JDBC_PW"))) {
            String sql = " with\n" + "    reservations as (\n" + "        select CODE, Room, CheckIn, CheckOut, Rate\n"
                    + "        from atran271.lab7_reservations\n" + "    ),\n" + "    rooms as (\n"
                    + "        select RoomCode, RoomName, basePrice\n" + "        from atran271.lab7_rooms\n"
                    + "    ),\n" + "    revenue as (\n" + "        select * from\n"
                    + "        reservations join rooms\n" + "        on reservations.Room = rooms.RoomCode\n"
                    + "    ),\n" + "    January as (\n" + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-02-01'), greatest(CheckIn, '2022-01-01')) * Rate)\n"
                    + "            as January\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-02-01'), greatest(CheckIn, '2022-01-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    February as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-03-01'), greatest(CheckIn, '2022-02-01')) * Rate)\n"
                    + "            as February\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-03-01'), greatest(CheckIn, '2022-02-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    March as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-04-01'), greatest(CheckIn, '2022-03-01')) * Rate)\n"
                    + "            as March\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-04-01'), greatest(CheckIn, '2022-03-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    April as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-05-01'), greatest(CheckIn, '2022-04-01')) * Rate)\n"
                    + "            as April\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-05-01'), greatest(CheckIn, '2022-04-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    May as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-06-01'), greatest(CheckIn, '2022-05-01')) * Rate)\n"
                    + "            as May\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-06-01'), greatest(CheckIn, '2022-05-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    June as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-07-01'), greatest(CheckIn, '2022-06-01')) * Rate)\n"
                    + "            as June\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-07-01'), greatest(CheckIn, '2022-06-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    July as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-08-01'), greatest(CheckIn, '2022-07-01')) * Rate)\n"
                    + "            as July\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-08-01'), greatest(CheckIn, '2022-07-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    August as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-09-01'), greatest(CheckIn, '2022-08-01')) * Rate)\n"
                    + "            as August\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-09-01'), greatest(CheckIn, '2022-08-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    September as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-10-01'), greatest(CheckIn, '2022-09-01')) * Rate)\n"
                    + "            as September\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-10-01'), greatest(CheckIn, '2022-09-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    October as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-11-01'), greatest(CheckIn, '2022-10-01')) * Rate)\n"
                    + "            as October\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-11-01'), greatest(CheckIn, '2022-10-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    November as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2022-12-01'), greatest(CheckIn, '2022-11-01')) * Rate)\n"
                    + "            as November\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2022-12-01'), greatest(CheckIn, '2022-11-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    December as (\n"
                    + "        select Room, RoomName,\n"
                    + "        sum(datediff(least(CheckOut, '2023-01-01'), greatest(CheckIn, '2022-12-01')) * Rate)\n"
                    + "            as December\n" + "        from revenue\n"
                    + "        where datediff(least(CheckOut, '2023-01-01'), greatest(CheckIn, '2022-12-01')) > 0\n"
                    + "        group by Room, RoomName\n" + "    ),\n" + "    room as (\n"
                    + "        select RoomCode, RoomName from rooms\n" + "    ),\n" + "    revenues as (\n"
                    + "        select room.RoomCode as RoomCode,\n"
                    + "            round(ifnull(January.January, 0)) as January,\n"
                    + "            round(ifnull(February.February, 0)) as February,\n"
                    + "            round(ifnull(March.March, 0)) as March,\n"
                    + "            round(ifnull(April.April, 0)) as April,\n"
                    + "            round(ifnull(May.May, 0)) as May,\n"
                    + "            round(ifnull(June.June, 0)) as June,\n"
                    + "            round(ifnull(July.July, 0)) as July,\n"
                    + "            round(ifnull(August.August, 0)) as August,\n"
                    + "            round(ifnull(September.September, 0)) as September,\n"
                    + "            round(ifnull(October.October, 0)) as October,\n"
                    + "            round(ifnull(November.November, 0)) as November,\n"
                    + "            round(ifnull(December.December, 0)) as December,\n"
                    + "            round(ifnull(January, 0)\n" + "            + ifnull(February, 0)\n"
                    + "            + ifnull(March, 0)\n" + "            + ifnull(April, 0)\n"
                    + "            + ifnull(May, 0)\n" + "            + ifnull(June, 0)\n"
                    + "            + ifnull(July, 0)\n" + "            + ifnull(August, 0)\n"
                    + "            + ifnull(September, 0)\n" + "            + ifnull(October, 0)\n"
                    + "            + ifnull(November, 0)\n" + "            + ifnull(December, 0))\n"
                    + "            as Total\n" + "        from room\n" + "        left join January\n"
                    + "        on room.RoomCode = January.Room\n" + "        left join February\n"
                    + "        on room.RoomCode = February.Room\n" + "        left join March\n"
                    + "        on room.RoomCode = March.Room\n" + "        left join April\n"
                    + "        on room.RoomCode = April.Room\n" + "        left join May\n"
                    + "        on room.RoomCode = May.Room\n" + "        left join June\n"
                    + "        on room.RoomCode = June.Room\n" + "        left join July\n"
                    + "        on room.RoomCode = July.Room\n" + "        left join August\n"
                    + "        on room.RoomCode = August.Room\n" + "        left join September\n"
                    + "        on room.RoomCode = September.Room\n" + "        left join October\n"
                    + "        on room.RoomCode = October.Room\n" + "        left join November\n"
                    + "        on room.RoomCode = November.Room\n" + "        left join December\n"
                    + "        on room.RoomCode = December.Room\n" + "    ),\n" + "    totals as (\n"
                    + "        select 'Totals' as RoomCode,\n" + "            round(sum(January)) as January,\n"
                    + "            round(sum(February)) as February,\n" + "            round(sum(March)) as March,\n"
                    + "            round(sum(April)) as April,\n" + "            round(sum(May)) as May,\n"
                    + "            round(sum(June)) as June,\n" + "            round(sum(July)) as July,\n"
                    + "            round(sum(August)) as August,\n"
                    + "            round(sum(September)) as September,\n"
                    + "            round(sum(October)) as October,\n"
                    + "            round(sum(November)) as November,\n"
                    + "            round(sum(December)) as December,\n" + "            round(sum(January)\n"
                    + "            + sum(February)\n" + "            + sum(March)\n" + "            + sum(April)\n"
                    + "            + sum(May)\n" + "            + sum(June)\n" + "            + sum(July)\n"
                    + "            + sum(August)\n" + "            + sum(September)\n" + "            + sum(October)\n"
                    + "            + sum(November)\n" + "            + sum(December))\n" + "            as Total\n"
                    + "        from revenues\n" + "    ),\n" + "    overview as (\n"
                    + "        select * from revenues\n" + "        union all\n" + "        select * from totals\n"
                    + "    )\n" + "select * from overview ";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                // Step 5: Receive results
                while (rs.next()) {
                    String roomcode = rs.getString("RoomCode");
                    int jan = rs.getInt("January");
                    int feb = rs.getInt("February");
                    int mar = rs.getInt("March");
                    int april = rs.getInt("April");
                    int may = rs.getInt("May");
                    int june = rs.getInt("June");
                    int july = rs.getInt("July");
                    int august = rs.getInt("August");
                    int sep = rs.getInt("September");
                    int oct = rs.getInt("October");
                    int nov = rs.getInt("November");
                    int dec = rs.getInt("December");
                    int total = rs.getInt("Total");

                    System.out.format(
                            "%-6s $%-5d $%-5d $%-5d $%-5d $%-5d $%-5d "
                                    + "$%-5d $%-5d $%-5d $%-5d $%-5d $%-5d $%-5d %n",
                            roomcode, jan, feb, mar, april, may, june, july, august, sep, oct, nov, dec, total);
                }
                    System.out.println();
            }
        }

    }

}
