import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCResponder {

    public static void main(String[] args) {

        String query = null;

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

        try(BufferedReader configFile = new BufferedReader(new FileReader("config.properties"))) {

            config.load(configFile);
            dbConnStr = config.getProperty("dbConnStr");
            dbUname = config.getProperty("dbUname");
            dbPassw = config.getProperty("dbPassw");

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
    }
}