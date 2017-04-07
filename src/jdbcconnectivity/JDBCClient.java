import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

// TODO: send responder.jar to remote server, open inSocket and outSocket, wait for reply from responder, then start SQL query loop

public class JDBCClient {

    private static final int bufferSize = 1024;
    private static final int scpBufferSize = 1024;
    private static final Scanner sc = new Scanner(System.in);
    private static final String[] hosts = {"st-cn0001.oulu.fi", "st-cn0002.oulu.fi", "st-cn0003.oulu.fi"};
    private static final String lfile = "test.txt";
    private static final String info = "You need to use your ITEE login, not Paju login.\n" +
            "Your ITEE credentials are the ones used in ITEE computer classes.";

    public static void main(String[] args) {

        JSch jsch = new JSch();
        String sshUname = null;
        String sshHost = null;
        String dbConnStr = null;
        String dbPassw = null;
        FileInputStream fis = null;

        try {

            // USER INTERFACE
            // -----------------------------------------------------------------------------------------------------

            JFrame welcomeFrame = new JFrame("Info Frame");
            JOptionPane.showMessageDialog(welcomeFrame, info, "Info", JOptionPane.INFORMATION_MESSAGE);

            JFrame sshUnameFrame = new JFrame("Enter ITEE Username Prompt");
            sshUname = JOptionPane.showInputDialog(sshUnameFrame, "Enter ITEE username.",
                    System.getProperty("user.name"));
            if(sshUname == null) System.exit(0);

            JFrame sshHostFrame = new JFrame("Select ITEE Host Prompt");
            sshHost = (String) JOptionPane.showInputDialog(sshHostFrame,
                    "Select ITEE host. If you are uncertain, choose the first option.", "ITEE Host",
                    JOptionPane.QUESTION_MESSAGE, null, hosts, hosts[0]);
            if(sshHost == null) System.exit(0);

            // *****************************************************************************************************

            // SSH SESSION
            // -----------------------------------------------------------------------------------------------------

            Session session = jsch.getSession(sshUname, sshHost, 22);
            UserInfo userInfo = new SshUserInfo();
            session.setUserInfo(userInfo);
            session.connect();
            PrintUtils.printlnColor("ssh " + sshUname + "@" + sshHost, PrintUtils.Color.YELLOW, System.out);

            // *****************************************************************************************************

            // MKDIR
            // -----------------------------------------------------------------------------------------------------

            // SCP FROM LOCAL TO REMOTE
            // IF DIRECTORY DOES NOT EXISTS, CREATE IT
            String mkdirCommand = "mkdir " + "/home/" + sshUname + "/jdbcresponder";
            Channel mkdirChannel = session.openChannel("exec");
            ((ChannelExec) mkdirChannel).setCommand(mkdirCommand);
            mkdirChannel.connect();
            mkdirChannel.disconnect();
            PrintUtils.printlnColor(mkdirCommand, PrintUtils.Color.YELLOW, System.out);

            // *****************************************************************************************************

            // SCP
            // -----------------------------------------------------------------------------------------------------

            String rfile = "/home/" + sshUname + "/jdbcresponder/test.txt";
            boolean ptimestamp = true;
            String scpCommand = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
            Channel scpChannel = session.openChannel("exec");
            ((ChannelExec) scpChannel).setCommand(scpCommand);
            OutputStream scpOut = scpChannel.getOutputStream();
            InputStream scpIn = scpChannel.getInputStream();
            scpChannel.connect();

            if(checkAck(scpIn) != 0) {

                System.out.print("I/O error! (ack)");
                System.exit(0);

            }

            File _lfile = new File(lfile);

            if(ptimestamp) {

                scpCommand = "T " + (_lfile.lastModified() / 1000) + " 0";
                scpCommand += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                scpOut.write(scpCommand.getBytes());
                scpOut.flush();

                if(checkAck(scpIn) != 0) {

                    System.out.print("I/O error! (ack)");
                    System.exit(0);

                }
            }

            long filesize = _lfile.length();
            scpCommand = "C0644 " + filesize + " ";
            if(lfile.lastIndexOf('/') > 0) {

                scpCommand += lfile.substring(lfile.lastIndexOf('/') + 1);

            } else {

                scpCommand += lfile;

            }

            scpCommand += "\n";
            scpOut.write(scpCommand.getBytes());
            scpOut.flush();

            if(checkAck(scpIn) != 0) {

                System.out.print("I/O error! (ack)");
                System.exit(0);

            }

            fis = new FileInputStream(lfile);
            byte[] scpBuf = new byte[scpBufferSize];
            while(true) {

                int len = fis.read(scpBuf, 0, scpBuf.length);
                if(len <= 0) break;
                scpOut.write(scpBuf, 0, len); //out.flush();

            }
            fis.close();
            fis = null;
            // send '\0'
            scpBuf[0] = 0;
            scpOut.write(scpBuf, 0, 1);
            scpOut.flush();

            if(checkAck(scpIn) != 0) {

                System.out.print("I/O error! (ack)");
                System.exit(0);

            }

            scpOut.close();
            scpChannel.disconnect();
            PrintUtils.printlnColor("Payload delivered.", PrintUtils.Color.YELLOW, System.out);

            // *****************************************************************************************************

            //  STU31@"(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=toldb.oulu.fi) (PORT=1521))(CONNECT_DATA=(SID=toldb11)))"
            JFrame dbConnStrFrame = new JFrame("Database Connection String");
            dbConnStr = "jdbc:oracle:thin:@";
            dbConnStr += JOptionPane.showInputDialog(dbConnStrFrame, "Enter your database connection string.");
            if(dbConnStr == null) System.exit(0);
            UserInfo dbUserInfo = new DbUserInfo();
            dbPassw = dbUserInfo.getPassword();

            // LOOP TO PROMPT USER INPUT STARTS HERE
            // -----------------------------------------------------------------------------------------------------
            String command = null;
            while(true) {

                PrintUtils.printlnColor("Enter command.", PrintUtils.Color.BLUE, System.out);
                command = sc.nextLine();
                if(command.toLowerCase().equals("quit")) break;

                Channel channel = session.openChannel("exec");
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

                            PrintUtils.printlnColor("Channel error! Query might not have been completed succesfully.",
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

    private static int checkAck(InputStream in) throws IOException {

        int b = in.read();
        if(b == 0) return b;
        if(b == -1) return b;

        if(b == 1 || b == 2) {

            StringBuffer sb = new StringBuffer();
            int c;

            do {

                c = in.read();
                sb.append((char) c);

            } while(c != '\n');

            if(b == 1) { // error

                System.out.print(sb.toString());

            }

            if(b == 2) { // fatal error

                System.out.print(sb.toString());

            }
        }

        return b;
    }
}