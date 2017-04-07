import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import javax.swing.*;

public class DbUserInfo implements UserInfo, UIKeyboardInteractive {

    private String passw;
    JTextField passwordField = (JTextField) new JPasswordField(35);

    public String getPassword() {

        return passw;
    }


    public String getPassphrase() {

        return null;

    }

    public boolean promptPassphrase(String message) {

        return true;

    }

    public boolean promptPassword(String message) {

        Object[] ob = {passwordField};

        int result = JOptionPane.showConfirmDialog(null, ob, message, JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION) {

            passw = passwordField.getText();
            return true;

        } else {

            return false;

        }
    }

    public boolean promptYesNo(String str) {

        Object[] options = {"Yes", "No"};

        int ret = JOptionPane.showOptionDialog(null,
                str,
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);

        return ret == 0;
    }

    public void showMessage(String message) {

        JOptionPane.showMessageDialog(null, message);
    }

    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo) {return new String[1];}
}
