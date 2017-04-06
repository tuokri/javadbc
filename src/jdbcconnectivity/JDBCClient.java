import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

public class JDBCClient {

    private static final Scanner sc = new Scanner(System.in);
    private static final String[] hosts = {"st-cn0002.oulu.fi", "st-cn0003.oulu.fi"};

    public static void main(String[] args) {

        JSch jsch = new JSch();
        String sshUname = null;
        String sshPassw = null;
        String sshHost = null;

        try {

            JFrame sshUnameFrame = new JFrame("Enter ITEE Username Prompt");
            sshUname = JOptionPane.showInputDialog(sshUnameFrame, "Enter ITEE username.",
                    System.getProperty("user.name"));
            if(sshUname == null) System.exit(0);

            JFrame sshHostFrame = new JFrame("Select ITEE Host Prompt");
            sshHost = (String)JOptionPane.showInputDialog(sshHostFrame,
                    "Select ITEE host. If you are uncertain, choose the first option.", "ITEE Host",
                    JOptionPane.QUESTION_MESSAGE, null, hosts, hosts[0]);
            if(sshHost == null) System.exit(0);

            Session session = jsch.getSession(sshUname, sshHost, 22);

            // username and password will be given via UserInfo interface.
            UserInfo userInfo = new MyUserInfo();
            session.setUserInfo(userInfo);
            session.connect();

        } catch(Exception e) {

            System.out.println("An exception occurred! Check console.");
            e.printStackTrace();

        }

        System.exit(0);
    }
}