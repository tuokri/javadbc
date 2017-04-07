import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: when ran via CLI, connect to database, open inSocket and outSocket, wait for start-message, send greetings to home, wait for response ... do stuff

public class JDBCResponder {

    private static final int portMin = 5000;
    private static final int portMax = 15000;

    public static void main(String[] args) {

        int portAmount = portMax - portMin + 1;
        int[] ports = new int[portAmount];
        for(int i = 0; i < portAmount; i++) {

            ports[i] = portMin + i;

        }

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

            System.out.println(e);
            System.exit(0);

        }

        ServerSocket server = null;
        Socket client = null;
        ResultSet rs = null;
        try {

            Connection connection = DriverManager.getConnection(dbConnStr, dbUname, dbPassw);
            System.out.println("JDBCResponder: DBCONNOK");

            for (int port : ports) {

                try {

                    server = new ServerSocket(port);
                    System.out.println("JDBCResponder: PORT " + port);
                    break;

                } catch(Exception e) {

                    // Port was closed.

                }
            }

            byte[] messageByte = new byte[1000];
            boolean end = false;
            String messageString = "";

            client = server.accept();

            DataInputStream clientIn = new DataInputStream(client.getInputStream());
            int bytesRead = 0;

            messageByte[0] = clientIn.readByte();
            messageByte[1] = clientIn.readByte();
            ByteBuffer byteBuffer = ByteBuffer.wrap(messageByte, 0, 2);

            int bytesToRead = byteBuffer.getShort();

            while(!end) {

                bytesRead = clientIn.read(messageByte);
                messageString += new String(messageByte, 0, bytesRead);
                if(messageString.length() == bytesToRead) {

                    end = true;

                }
            }

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
                System.out.println(e.toString());

            }

        } catch(Exception e) {

            System.out.println("JDBCResponder: Error!");
            System.out.println(e.toString());

        } finally {

            try {

                if(server != null) server.close();
                if(client != null) client.close();

            } catch(Exception e) {

                e.printStackTrace();

            }
        }
    }
}