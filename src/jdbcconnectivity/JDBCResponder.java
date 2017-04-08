import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

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

        String rsString = null;
        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;

        try {

            conn = DriverManager.getConnection(dbConnStr, dbUname, dbPassw);
            // System.out.println("JDBCResponder: Connected to database.");

            try {

                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();
                int rowCount = 1;

                while(rs.next()) {

                    System.out.println("Row " + rowCount++ + ":  ");

                    for (int i = 1; i <= numberOfColumns; i++) {


                        String colName = rsmd.getColumnName(i);
                        System.out.print("\t" + colName + ":\t");

                        int type = rsmd.getColumnType(i);
                        if(type == Types.VARCHAR || type == Types.CHAR) {

                            System.out.println(rs.getString(i));

                        } else if(type == Types.DATE) {

                            System.out.println(rs.getDate(i));

                        } else if(type == Types.REAL) {

                            System.out.println(rs.getDouble(i));

                        } else {

                            System.out.println(rs.getInt(i));

                        }
                    }

                    System.out.println("");
                }

                System.out.println(rsString);

            } catch(SQLException e) {

                System.out.println("JDBCResponder: SQL error.");
                System.out.println(e);

            }

        } catch(Exception e) {

            System.out.println("JDBCResponder: Error.");
            System.out.println(e);

        } finally {

            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(conn);

        }
    }
}
