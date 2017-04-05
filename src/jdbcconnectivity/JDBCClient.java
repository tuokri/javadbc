package jdbcconnectivity;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

class JDBCClient {

    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        String sshUnameOulu = null;
        String sshPasswOulu = null;
        String sshHostOulu = null;
        String command = null;
        Properties config = new Properties();

        try(BufferedReader configFile = new BufferedReader(new FileReader("config.properties"))) {

            config.load(configFile);
            sshHostOulu = config.getProperty("sshHostOulu");
            sshUnameOulu = config.getProperty("sshUnameOulu");
            sshPasswOulu = config.getProperty("sshPasswOulu");
            command = config.getProperty("command");

        } catch(IOException ioe) {

            System.out.println("IOException");
            ioe.printStackTrace();
            System.exit(0);

        }

        Session session = null;

        try {

            Properties jschConfig = new Properties();
            jschConfig.put("StrictHostKeyChecking", "no"); // Avoid UnknownHostKey issue.
            JSch jsch = new JSch();
            session = jsch.getSession(sshUnameOulu, sshHostOulu, 22);
            session.setPassword(sshPasswOulu);
            session.setConfig(jschConfig);
            session.connect();
            Channel channel = session.openChannel("exec");

            while(true) {

                System.out.print("\n\n");
                System.out.println("1: Show items.");
                System.out.println("2: Show customers.");
                System.out.println("3: Exit.");
                System.out.println("Make selection and press ENTER.");

                switch(getUserInput()) {

                    case 1:
                        String result = remoteQuery(command, "SELECT * FROM YEAR", channel);
                        System.out.println(result);
                        break;

                    // send query
                    // wait for response
                    // print response
                    case 2:
                        break;

                    case 3:
                        return;

                    default:
                        System.out.println("Something went wrong!");
                        break;

                }
            }

        } catch(Exception e) {

            System.out.println("Connection error!");
            e.printStackTrace();
            System.exit(0);

        } finally {

            if(session != null) session.disconnect();

        }
    }

    private static int getUserInput() {

        int choice;

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

    private static String remoteQuery(String command, String query, Channel channel) {

        StringBuilder resultBuilder = new StringBuilder();
        byte[] tmp = new byte[1024];

        try {

            String finalCommand = command + " " + query;
            ((ChannelExec) channel).setCommand(finalCommand);
            InputStream in = channel.getInputStream();
            channel.connect();

            while(true) {

                while(in.available() > 0) {

                    int i = in.read(tmp, 0, 1024);
                    if(i < 0) break;
                    resultBuilder.append((new String(tmp, 0, i)));

                }

                if(channel.isClosed()) {

                    if(in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;

                }

                try {

                    Thread.sleep(150);

                } catch(Exception ee) {

                    ee.printStackTrace();

                }
            }

        } catch(Exception e) {

            e.printStackTrace();

        }

        return resultBuilder.toString();
    }
}