package jdbcconnectivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JDBCClient {

    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch(ClassNotFoundException e) {

            System.out.println("Oracle JDBC Driver not found!");
            e.printStackTrace();
            System.exit(0);

        }

        System.out.println("Oracle JDBC Driver registered!");

        int lport = 5050;
        int rport = 1521;
        String rhost = "st-cn0001.oulu.fi";
        String sshFileName = "ssh.dat";
        String dbFileName = "db.dat";
        String sshUname = null;
        String sshPassw = null;
        String dbUname = null;
        String dbPassw = null;
        String connStr = null;
        boolean useSsh = false;
        File file = new File(sshFileName);
        System.out.println(file.getAbsolutePath());

        if(file.exists()) {

            useSsh = true;
            connStr = "jdbc:oracle:thin:@localhost:" + lport + ":toldb11";

        } else {

            connStr = "jdbc:oracle:thin:@toldb.oulu.fi:1521:toldb11";

        }

        if(useSsh) {

            try(BufferedReader sshFile = new BufferedReader(new FileReader(sshFileName))) {

                // Username on first line, password on second line.
                sshUname = sshFile.readLine();
                sshPassw = sshFile.readLine();

            } catch(IOException ioe) {

                System.out.println("IOException");
                System.exit(0);

            }
        }

        try(BufferedReader dbFile = new BufferedReader(new FileReader(dbFileName))) {

            // Username on first line, password on second line.
            dbUname = dbFile.readLine();
            dbPassw = dbFile.readLine();

        } catch(IOException ioe) {

            System.out.println("IOException!");
            ioe.printStackTrace();
            System.exit(0);

        }

        Connection connection = null;
        Session session = null;

        try {

            if(useSsh) {

                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no"); // Avoid UnknownHostKey issue.
                JSch jsch = new JSch();
                session = jsch.getSession(sshUname, rhost, 22);
                session.setPassword(sshPassw);
                session.setConfig(config);
                session.connect();
                int port = session.setPortForwardingL(lport, rhost, rport);
                System.out.println("localhost:"+ port + " -> " + rhost + ":" + rport);

            }

            connection = DriverManager.getConnection(connStr, dbUname, dbPassw);

        } catch(Exception e) {

            System.out.println("Connection failed!");
            e.printStackTrace();
            System.exit(0);

        }

        if(connection != null) {

            System.out.println("Connection to database successful!");

        } else {

            System.out.println("Failed to make connection!");
            System.exit(0);

        }

        while(true) {

            System.out.print("\n\n");
            System.out.println("1: Show items.");
            System.out.println("2: Show customers.");
            System.out.println("3: Exit.");
            System.out.println("Make selection and press ENTER.");

            switch(getUserInput()) {

                case 1:
                    try {

                        showItems(connection);

                    } catch(SQLException e) {

                        e.printStackTrace();

                    }

                    break;

                case 2:
                    try {

                        showCustomers(connection);

                    } catch(SQLException e) {

                        e.printStackTrace();

                    }

                    break;

                case 3:
                    exitRoutine(connection);
                    break;

                default:
                    System.out.println("Something went wrong!");
                    exitRoutine(connection);

            }
        }
    }

    private static int getUserInput() {

        int choice = 0;

        while(true) {

            try {

                choice = scan.nextInt();

                if(choice != 1 && choice != 2 && choice != 3) {

                    scan.nextLine();
                    System.out.println("Please select 1, 2 or 3.");

                } else {

                    scan.nextLine();
                    break;

                }

            } catch(InputMismatchException ime) {

                System.out.println("Please select 1, 2 or 3.");
                scan.nextLine();

            }
        }

        return choice;
    }

    private static void showItems(Connection conn) throws SQLException {

        Statement stmt = null;
        String query = "SELECT * FROM YEAR";

        try {

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {

                String year = rs.getString("YEAR");

                System.out.println(year);

            }

        } catch(SQLException e) {

            e.printStackTrace();

        } finally {

            if(stmt != null) {

                stmt.close();

            }
        }
    }

    private static void showCustomers(Connection conn) throws SQLException {

    }

    private static void exitRoutine(Connection conn) {

        try {

            conn.close();
            System.exit(0);

        } catch(SQLException e) {

            System.out.println("Error while closing connection!");
            e.printStackTrace();

        }
    }
}