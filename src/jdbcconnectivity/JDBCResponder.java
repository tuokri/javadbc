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
            dbUname = args[1];
            dbPassw = args[2];

        } catch(Exception e) {

            e.printStackTrace();
            System.exit(0);

        }

        ResultSet rs = null;
        try {

            // "(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=toldb.oulu.fi) (PORT=1521))(CONNECT_DATA=(SID=toldb11)))"
            Connection connection = DriverManager.getConnection(dbConnStr, dbUname, dbPassw);

            try {

                query = "SELECT * FROM YEAR";
                Statement stmt = connection.createStatement();
                System.out.println("QUERY : " + query);
                rs = stmt.executeQuery(query);

                while(rs.next()) {

                    System.out.println(rs.getString("YEAR"));

                }

            } catch(SQLException e) {

                System.out.println("JDBCResponder: SQL error!");
                e.printStackTrace();

            }

        } catch(Exception e) {

            System.out.println("JDBCResponder: Error!");
            e.printStackTrace();

        }
    }
}