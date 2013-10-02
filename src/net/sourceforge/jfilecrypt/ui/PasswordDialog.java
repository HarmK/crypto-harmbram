package net.sourceforge.jfilecrypt.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;
import net.sourceforge.jfilecrypt.Application;

/**
 * This is an input dialog for passwords. It is a modal
 * input box with hidden password input. The window
 * accepts key events, ESC closes the window, ENTER
 * accepts the input.
 */
public class PasswordDialog
  extends JDialog
{
  private static final long serialVersionUID = 1L;
  private static ResourceBundle bundle;
  
  private String sDefaultPassword = "", sPassword = "";
  private boolean bAborted = false;
  
  private JLabel lbText;
  private JPasswordField pfPasswd;
  private JButton btSend, btAbort;
  
  public PasswordDialog(final JFrame parent) {
    super(parent, "", true);
    bundle = Application.getResourceBundle();
    this.setTitle(bundle.getString("password_input"));
    
    // Closing the window is equal to pressing the Abort-Button
    WindowListener windowListener = (new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        pressedAbort();
      }
    });
    addWindowListener(windowListener);
    
    ActionListener actionListener = (new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == btSend)
          pressedSend();
        else if(evt.getSource() == btAbort)
          pressedAbort();
      }
    });
    
    // Enter is equal to pressing the Send-Button and
    // Escape is equal to pressing the Abort-Button
    KeyListener keyListener = (new KeyAdapter() {
            @Override
      public void keyPressed(KeyEvent evt) {
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
          pressedSend();
        else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
          pressedAbort();
      }
    });
    addKeyListener(keyListener);
    
    lbText = new JLabel(bundle.getString("password") + ": ");
    pfPasswd = new JPasswordField("");
    btSend = new JButton(bundle.getString("ok"));
    btAbort = new JButton(bundle.getString("cancel"));
    
    lbText.setHorizontalAlignment(JLabel.RIGHT);
    
    pfPasswd.addKeyListener(keyListener);
    
    btSend.addActionListener(actionListener);
    btSend.addKeyListener(keyListener);
    
    btAbort.addActionListener(actionListener);
    btAbort.addKeyListener(keyListener);
    
    JPanel pnlMain = new JPanel();
    pnlMain.setBorder(BorderFactory.createEmptyBorder(8, 8, 10, 8));
    pnlMain.setLayout(new GridLayout(2, 2, 4, 4));

    pnlMain.add(lbText);
    pnlMain.add(pfPasswd);
    pnlMain.add(btSend);
    pnlMain.add(btAbort);
    
    Container contFrame = getContentPane();
    contFrame.setLayout(new BorderLayout());
    contFrame.add(pnlMain, "Center");
    
    pack();
  }
  
  /**
   * Show this dialog.
   * @param parent
   * @return true if user did not abort
   */
  public boolean showPasswordDialog(JFrame parent) {
    bAborted = false;
    pfPasswd.setText(sDefaultPassword);
    setLocationRelativeTo(parent);
    setVisible(true);
    return !aborted();
  }
  
  /**
   * Called when the Send-Button is pressed.
   */
  private void pressedSend() {
    sPassword = String.copyValueOf(pfPasswd.getPassword());
    pfPasswd.setText("");
    setVisible(false);
  }
  
  /**
   * Called when the Abort-Button is pressed.
   */
  private void pressedAbort() {
    pfPasswd.setText("");
    bAborted = true;
    setVisible(false);
  }
  
  /**
   * @return true if user pressed Abort-Button, false if not
   */
  public boolean aborted() {
    return bAborted;
  }
  
  /**
   * @return returns the password, if user pressed the Send-Button
   */
  public String getPassword() {
    return sPassword;
  }
  
  /**
   * Sets the password which is displayed by default when showing the dialog
   * @param passwd, this is your default password; it's empty by default
   */
  public void setDefaultPassword(String passwd) {
    sDefaultPassword = passwd;
  }
}
