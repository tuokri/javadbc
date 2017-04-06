package jdbcconnectivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

class JDBCResponder {

    public static void main(String[] args) {

        String query = null;

        if(args.length == 0) {

            System.out.println("No command line argument specified. Exiting.");
            System.exit(0);

        } else {

            query = args[0];

        }

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch(ClassNotFoundException e) {

            System.out.println("Oracle JDBC Driver not found!");
            e.printStackTrace();
            System.exit(0);

        }

        System.out.println("Oracle JDBC Driver registered!");

        Properties config = new Properties();
        String dbConnStr = null;
        String dbUname = null;
        String dbPassw = null;
        String homeAddress = null;
        int portAtHome = 0;

        try(BufferedReader configFile = new BufferedReader(new FileReader("config.properties"))) {

            config.load(configFile);
            dbConnStr = config.getProperty("dbConnStr");
            dbUname = config.getProperty("dbUname");
            dbPassw = config.getProperty("dbPassw");
            homeAddress = config.getProperty("homeAddress");
            portAtHome = Integer.parseInt(config.getProperty("portAtHome"));

        } catch(IOException ioe) {

            System.out.println("IOException!");
            ioe.printStackTrace();
            System.exit(0);

        }

        System.out.println("Executing query.");
        ResultSet rs = null;
        try{

            Connection connection = DriverManager.getConnection(dbConnStr, dbUname, dbPassw);

            try{

                Statement stmt = connection.createStatement();
                // Query is provided as command line argument.
                System.out.println("QUERY : " + query);
                rs = stmt.executeQuery(query);

            } catch(SQLException e) {

                System.out.println("SQL error!");
                e.printStackTrace();

            }

        } catch(Exception e) {

            System.out.println("Error!");
            e.printStackTrace();

        }

        try {

            while(rs.next()) {

                String yearName = rs.getString("YEAR");
                System.out.println(yearName);

            }

        } catch(SQLException e) {

            System.out.println("SQLException!");
            e.printStackTrace();

        }


        /*
        System.out.println("Phoning home.");
        try(Socket socket = new Socket(homeAddress, portAtHome)) {

            System.out.println("SOCKET: " + homeAddress + ":" + portAtHome);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while(rs.next()) {

                System.out.println("WRITING OUT!");

                String yearName = rs.getString("YEAR");

                String resultString = new String(yearName + " lul ");

                out.writeBytes(resultString);

            }

            out.writeByte('\n');

        } catch(Exception e) {

            System.out.println("Error!");
            e.printStackTrace();

        }
        */
    }

    private static void showItems(Connection conn) throws SQLException {

    }

    private static void showCustomers(Connection conn) throws SQLException {

    }
}