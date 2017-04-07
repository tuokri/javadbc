import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: when ran via CLI, connect to database, open inSocket and outSocket, wait for start-message, send greetings to home, wait for response ... do stuff

public class JDBCResponder {

    public static void main(String[] args) {

        String query = null;
        String dbConnStr = null;
        String dbUname = null;
        String dbPassw = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch(ClassNotFoundException e) {

            System.out.println("JDBCResponder: Oracle JDBC Driver not found!");
            e.printStackTrace();
            System.exit(0);

        }

        System.out.println("JDBCResponder: Oracle JDBC Driver registered!");

        try {

            dbConnStr = args[0];
            dbPassw = args[1].toString();

        } catch(Exception e) {

            e.printStackTrace();
            System.exit(0);

        }

        System.out.println("JDBCResponder: Executing query.");
        ResultSet rs = null;
        try {

            Connection connection = DriverManager.getConnection(dbConnStr, dbUname, dbPassw);

            try {

                Statement stmt = connection.createStatement();
                // Query is provided as command line argument.
                System.out.println("QUERY : " + query);
                rs = stmt.executeQuery(query);

            } catch(SQLException e) {

                System.out.println("JDBCResponder: SQL error!");
                e.printStackTrace();

            }

        } catch(Exception e) {

            System.out.println("JDBCResponder: Error!");
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