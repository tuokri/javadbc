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

/**
 * JDBCClient act as the GUI/CLI client running at the user's home location.
 * This is basically a filthy trick to get remote access to TOLDB database from outside of UO network.
 * <p>
 * How this works:
 *  1. Connect via SSH to st-cnXXXX.oulu.fi remote server.<p>
 *  2. Create directory on remote server. Example: /home/user/remotedb<p>
 *  3. Transfer responder.jar (which is {@link JDBCResponder} packaged) to remote directory via SCP.<p>
 *  4. Begin user input while loop for database queries.<p>
 *  5. Execute queries by executing responder.jar on remote location via SSH:<p>
 *  -java -jar /home/user/remotedb/responder.jar CONNSTR DBUNAME DPASSW "SELECT * FROM X"<p>
 *  6. Receive response from responder and print it.<p>
 *
 * @see JDBCResponder
 */
public class JDBCClient {

    private static final int bufferSize = 1024;
    private static final int scpBufferSize = 1024;
    private static final Scanner sc = new Scanner(System.in);
    private static final String[] hosts = {"st-cn0001.oulu.fi", "st-cn0002.oulu.fi", "st-cn0003.oulu.fi"};
    private static final String dbConnStr = "jdbc:oracle:thin:@(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=toldb.oulu.fi) (PORT=1521))(CONNECT_DATA=(SID=toldb11)))";
    private static final String info = "PLEASE READ!\n" + "You need to use your ITEE login.\n" +
            "Your ITEE login is the one you use in ITEE computer classes.\n" + "You will also need a TOLDB username and password.\n" +
            "TOLDB database usernames and passwords are provided by course staff.";
    private static final String lfile = "responder.jar";

    /**
     * The main method for user interaction and I/O handling.
     */
    public static void main(String[] args) {

        JSch jsch = new JSch();
        UserInfo userInfo = null;
        String sshUname = null;
        String sshHost = null;
        String dbUname = null;
        String dbPassw = null;
        FileInputStream fis = null;
        Session session = null;
        Channel mkdirChannel = null;
        Channel scpChannel = null;
        Channel queryChannel = null;
        OutputStream scpOut = null;
        InputStream scpIn = null;
        InputStream queryChannelIn = null;

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

        try {

            session = jsch.getSession(sshUname, sshHost, 22);
            userInfo = new SshUserInfo();
            session.setUserInfo(userInfo);
            session.connect();
            PrintUtils.printlnColor("ssh " + sshUname + "@" + sshHost, PrintUtils.Color.YELLOW, System.out);

            String mkdirCommand = "mkdir " + "/home/" + sshUname + "/jdbcresponder";
            mkdirChannel = session.openChannel("exec");
            ((ChannelExec) mkdirChannel).setCommand(mkdirCommand);
            mkdirChannel.connect();
            mkdirChannel.disconnect();
            PrintUtils.printlnColor(mkdirCommand, PrintUtils.Color.YELLOW, System.out);

            String rfile = "/home/" + sshUname + "/jdbcresponder/" + lfile;
            boolean ptimestamp = true;
            String scpCommand = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
            scpChannel = session.openChannel("exec");
            ((ChannelExec) scpChannel).setCommand(scpCommand);
            scpOut = scpChannel.getOutputStream();
            scpIn = scpChannel.getInputStream();
            scpChannel.connect();

            if(checkAck(scpIn) != 0) {

                System.out.print("I/O error! (ack)");

            }

            File _lfile = new File(lfile);

            if(ptimestamp) {

                scpCommand = "T" + (_lfile.lastModified() / 1000) + " 0";
                scpCommand += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                scpOut.write(scpCommand.getBytes());
                scpOut.flush();

                if(checkAck(scpIn) != 0) {

                    System.out.print("I/O error! (ack)");

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

            }

            fis = new FileInputStream(lfile);
            byte[] scpBuf = new byte[scpBufferSize];
            while(true) {

                int len = fis.read(scpBuf, 0, scpBuf.length);
                if(len <= 0) break;
                scpOut.write(scpBuf, 0, len);

            }
            fis.close();
            // send '\0'
            scpBuf[0] = 0;
            scpOut.write(scpBuf, 0, 1);
            scpOut.flush();

            if(checkAck(scpIn) != 0) {

                System.out.print("I/O error! (ack)");

            }

            scpOut.close();
            scpChannel.disconnect();
            PrintUtils.printlnColor("SCP complete, responder delivered.", PrintUtils.Color.YELLOW, System.out);

            JFrame dbConnStrFrame = new JFrame("Database Connection");
            dbUname = JOptionPane.showInputDialog(dbConnStrFrame, "Enter your database username (STUXX).");
            if(dbUname == null) System.exit(0);
            JPanel pwPanel = new JPanel();
            JLabel pwLabel = new JLabel("Enter your database password: ");
            JPasswordField pass = new JPasswordField(25);
            pwPanel.add(pwLabel);
            pwPanel.add(pass);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(dbConnStrFrame, pwPanel, "Database Password",
                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if(option == 0)
            {

                dbPassw = new String(pass.getPassword());

            } else {

                dbPassw = "";

            }

            while(true) {

                PrintUtils.printlnColor("Input database query and press ENTER. Input 'quit' to exit program.",
                        PrintUtils.Color.BLUE, System.out);
                String query = sc.nextLine();
                if(query.toLowerCase().trim().equals("quit")) break;

                String command = "java -jar " + rfile + " \"" + dbConnStr + "\" " + dbUname + " " + dbPassw + " \"" + query + "\"";
                queryChannel = session.openChannel("exec");
                ((ChannelExec) queryChannel).setCommand(command);
                queryChannelIn = queryChannel.getInputStream();
                queryChannel.connect();

                String response = null;
                byte[] tmp = new byte[bufferSize];
                while(true) {

                    while(queryChannelIn.available() > 0) {

                        int i = queryChannelIn.read(tmp, 0, bufferSize);
                        if(i < 0) break;
                        response = new String(tmp, 0, i);
                        PrintUtils.printlnColor(response, PrintUtils.Color.GREEN, System.out);
                        if(response.contains("virheellinen käyttäjätunnus")) {

                            dbUname = JOptionPane.showInputDialog(dbConnStrFrame, "There was an error. Please re-enter your database credentials.\n" +
                                    "Your database username (STUXX).");

                            option = JOptionPane.showOptionDialog(dbConnStrFrame, pwPanel, "Database Password",
                                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                            if(option == 0) // pressing OK button
                            {

                                dbPassw = new String(pass.getPassword());

                            } else {

                                dbPassw = "";

                            }
                        }
                    }

                    if(queryChannel.isClosed()) {

                        if(queryChannelIn.available() > 0) continue;

                        int status = queryChannel.getExitStatus();
                        if(status != 0) {

                            PrintUtils.printlnColor("Channel exit-status non-zero, possible error (" + status + ").",
                                    PrintUtils.Color.RED, System.out);

                        }

                        break;

                    }

                    try {

                        Thread.sleep(500);

                    } catch(Exception ee) {}
                }

                if(queryChannel != null) queryChannel.disconnect();
            }

        } catch(Exception e) {

            System.out.println("An exception occurred! Check console.");
            e.printStackTrace();

        } finally {

            if(mkdirChannel != null) mkdirChannel.disconnect();
            if(scpChannel != null) scpChannel.disconnect();
            if(queryChannel != null) queryChannel.disconnect();
            if(session != null) session.disconnect();

            try {
                fis.close();
            } catch(Exception ignore) {
            }
            try {
                scpOut.close();
            } catch(Exception ignore) {
            }
            try {
                scpIn.close();
            } catch(Exception ignore) {
            }
            try {
                queryChannelIn.close();
            } catch(Exception ignore) {
            }

        }

        System.exit(0);
    }

    private static int checkAck(InputStream in) throws IOException {

        int b = in.read();
        if(b == 0) return b;
        if(b == -1) return b;

        if(b == 1 || b == 2) {

            StringBuilder sb = new StringBuilder();
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