package jdbcconnectivity;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
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
        int portAtHome = 0;
        Properties config = new Properties();

        try(BufferedReader configFile = new BufferedReader(new FileReader("config.properties"))) {

            config.load(configFile);
            sshHostOulu = config.getProperty("sshHostOulu");
            sshUnameOulu = config.getProperty("sshUnameOulu");
            sshPasswOulu = config.getProperty("sshPasswOulu");
            portAtHome = Integer.parseInt(config.getProperty("portAtHome"));
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
            String result = null;

            while(true) {

                System.out.print("\n\n");
                System.out.println("1: Show items.");
                System.out.println("2: Show customers.");
                System.out.println("3: Exit.");
                System.out.println("Make selection and press ENTER.");

                switch(getUserInput()) {

                    case 1:
                        result = remoteQuery(command, "SELECT * FROM YEAR", channel, portAtHome);
                        System.out.println(result);
                        break;

                    case 2:
                        result = remoteQuery(command, "SELECT * FROM YEAR", channel, portAtHome);
                        System.out.println(result);
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

    private static String remoteQuery(String command, String query, Channel channel, int port) {

        StringBuilder resultBuilder = new StringBuilder();
        StringBuilder channelResponseBuilder = new StringBuilder();
        byte[] tmp = new byte[1024];

        try {

            String finalCommand = command + " " + "\"" + query + "\"";
            System.out.println("finalCommand: " + finalCommand);
            ((ChannelExec) channel).setCommand(finalCommand);
            InputStream channelIn = channel.getInputStream();
            channel.connect();

            // Read channel response.
            while(true) {

                while(channelIn.available() > 0) {

                    int i = channelIn.read(tmp, 0, 1024);
                    if(i < 0) break;
                    channelResponseBuilder.append((new String(tmp, 0, i)));
                    System.out.println("Building response!");

                }

                if(channel.isClosed()) {

                    if(channelIn.available() > 0) continue;
                    System.out.println("Channel exit-status: " + channel.getExitStatus());
                    System.out.println("Channel response: " + channelResponseBuilder.toString());
                    break;

                }

                try {

                    Thread.sleep(150);

                } catch(Exception ee) {

                    ee.printStackTrace();

                }
            }

            /*
            // Read SQL query response.
            // Open ServerSocket at portAtHome.
            System.out.println("Opening listener socket at " + port + ".");
            try(ServerSocket listener = new ServerSocket(port)) {

                while(true) {

                    try(Socket socket = listener.accept()) {

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line = null;

                        while((line = in.readLine()) != null) {

                            System.out.println(line);

                        }


                    } catch(Exception e) {

                        System.out.println("Exception!");
                        e.printStackTrace();

                    }
                }

            } catch(Exception e) {

                System.out.println("Exception!");
                e.printStackTrace();

            }
            */

        } catch(Exception e) {

            System.out.println("Exception!");
            e.printStackTrace();

        }

        return resultBuilder.toString();
    }
}