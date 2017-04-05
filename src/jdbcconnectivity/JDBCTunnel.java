package jdbcconnectivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JDBCTunnel {

    public static void main(String[] args) {

        try(BufferedReader dbFile = new BufferedReader(new FileReader("config.properties"))) {


        } catch(IOException ioe) {

            System.out.println("IOException!");
            ioe.printStackTrace();
            System.exit(0);

        }


    }

}