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
