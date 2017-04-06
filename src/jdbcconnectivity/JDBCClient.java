import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import javax.swing.*;
import java.io.InputStream;
import java.util.Scanner;

public class JDBCClient {

    private static final int bufferSize = 2048;
    private static final Scanner sc = new Scanner(System.in);
    private static final String[] hosts = {"st-cn0001.oulu.fi", "st-cn0002.oulu.fi", "st-cn0003.oulu.fi"};
    private static final String info = "You need to use your ITEE login, not Paju login.\n" +
            "Your ITEE credentials are the ones used in ITEE computer classes.";

    public static void main(String[] args) {

        JSch jsch = new JSch();
        String sshUname;
        String sshHost;

        try {

            JFrame welcomeFrame = new JFrame("Info Frame");
            JOptionPane.showMessageDialog(null, info, "Info", JOptionPane.INFORMATION_MESSAGE);

            JFrame sshUnameFrame = new JFrame("Enter ITEE Username Prompt");
            sshUname = JOptionPane.showInputDialog(sshUnameFrame, "Enter ITEE username.",
                    System.getProperty("user.name"));
            if(sshUname == null) System.exit(0);

            JFrame sshHostFrame = new JFrame("Select ITEE Host Prompt");
            sshHost = (String) JOptionPane.showInputDialog(sshHostFrame,
                    "Select ITEE host. If you are uncertain, choose the first option.", "ITEE Host",
                    JOptionPane.QUESTION_MESSAGE, null, hosts, hosts[0]);
            if(sshHost == null) System.exit(0);

            Session session = jsch.getSession(sshUname, sshHost, 22);
            UserInfo userInfo = new SshUserInfo();
            session.setUserInfo(userInfo);
            session.connect();

            String command;
            while(true) {

                Channel channel = session.openChannel("exec");

                PrintUtils.printlnColor("Enter query and press ENTER.", PrintUtils.Color.BLUE, System.out);
                PrintUtils.printlnColor("Enter 'QUIT' to exit program.", PrintUtils.Color.BLUE, System.out);
                command = sc.nextLine();
                if(command.toLowerCase().equals("quit")) {

                    break;

                }

                System.out.println("Executing query " + "\"" + command + "\"" + " ...");
                ((ChannelExec) channel).setCommand(command);
                InputStream in = channel.getInputStream();
                channel.connect();

                byte[] tmp = new byte[bufferSize];
                while(true) {

                    while(in.available() > 0) {

                        int i = in.read(tmp, 0, bufferSize);
                        if(i < 0) break;
                        PrintUtils.printlnColor(new String(tmp, 0, i), PrintUtils.Color.GREEN, System.out);

                    }

                    if(channel.isClosed()) {

                        if(in.available() > 0) continue;

                        int status = channel.getExitStatus();
                        if(status != 0) {

                            PrintUtils.printlnColor("Channel error! Query might not have been completed succesfully",
                                    PrintUtils.Color.RED, System.out);

                        }

                        break;

                    }

                    try {

                        Thread.sleep(100);

                    } catch(Exception ee) {

                        System.out.println("ee");

                    }
                }

                channel.disconnect();
            }

            session.disconnect();

        } catch(Exception e) {

            System.out.println("An exception occurred! Check console.");
            e.printStackTrace();

        }

        System.exit(0);
    }
}