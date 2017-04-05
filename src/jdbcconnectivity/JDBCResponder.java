package jdbcconnectivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class JDBCResponder {

    public static void main(String[] args) {

        Properties config = new Properties();

        String sshUnameOulu = null;
        String sshPasswOulu = null;
        String sshHostOulu = null;

        try(BufferedReader configFile = new BufferedReader(new FileReader("config.properties"))) {

            config.load(configFile);
            sshUnameOulu = config.getProperty("sshUnameOulu");
            sshPasswOulu = config.getProperty("sshPasswOulu");
            sshHostOulu = config.getProperty("sshHostOulu");

        } catch(IOException ioe) {

            System.out.println("IOException!");
            ioe.printStackTrace();
            System.exit(0);

        }
    }
}
