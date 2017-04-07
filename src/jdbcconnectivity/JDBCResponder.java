import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCResponder {

    public static void main(String[] args) {

        String query = null;
        String dbConnStr = null;
        String dbUname = null;
        String dbPassw = null;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch(ClassNotFoundException e) {

            System.out.println("JDBCResponder: Oracle JDBC Driver not found.");
            e.printStackTrace();
            System.exit(0);

        }

        //  System.out.println("JDBCResponder: Oracle JDBC Driver registered.");

        try {

            dbConnStr = args[0];
            dbUname = args[1];
            dbPassw = args[2];
            query = args[3];

        } catch(Exception e) {

            System.out.println(e);
            System.exit(0);

        }

        ResultSet rs = null;
        String rsString = null;
        try {

            Connection connection = DriverManager.getConnection(dbConnStr, dbUname, dbPassw);
            // System.out.println("JDBCResponder: Connected to database.");

            try {

                Statement stmt = connection.createStatement();

                rs = stmt.executeQuery(query);
                // System.out.println("JDBCResponder: Executing query...");
                while(rs.next()) {

                    rsString += rs.getString(1);

                }

                System.out.println(rsString);

            } catch(SQLException e) {

                System.out.println("JDBCResponder: SQL error.");
                System.out.println(e);

            }

        } catch(Exception e) {

            System.out.println("JDBCResponder: Error.");
            System.out.println(e);

        }
    }
}