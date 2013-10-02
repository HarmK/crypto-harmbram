package net.sourceforge.jfilecrypt.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.*;
import net.sourceforge.jfilecrypt.Application;
import net.sourceforge.jfilecrypt.Controller;

/**
 * This is an input dialog for passwords. It is a modal
 * input box with hidden password input. The window
 * accepts key events, ESC closes the window, ENTER
 * accepts the input.
 */
public class OverwriteDialog
  extends JDialog
{
  private static final long serialVersionUID = 1L;
  private static ResourceBundle bundle;
  
  private boolean forAllfiles = false;
  
  private JLabel lbFile;
  private JCheckBox cbAllFiles;
  private JButton btYes, btNo;
  private boolean overwriteFile = false;
  
  public OverwriteDialog(final JFrame parent) {
    super(parent, "", true);
    bundle = Application.getResourceBundle();
    this.setTitle(bundle.getString("overwrite_title"));
    
    // Closing the window is equal to pressing the No-Button
    WindowListener windowListener = (new WindowAdapter() {
            @Override
      public void windowClosing(WindowEvent evt) {
        pressedNo();
      }
    });
    addWindowListener(windowListener);
    
    ActionListener actionListener = (new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == btYes)
          pressedYes();
        else if(evt.getSource() == btNo)
          pressedNo();
      }
    });
    
    // Enter is equal to pressing the Yes-Button and
    // Escape is equal to pressing the No-Button
    KeyListener keyListener = (new KeyAdapter() {
            @Override
      public void keyPressed(KeyEvent evt) {
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
          pressedYes();
        else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
          pressedNo();
      }
    });
    addKeyListener(keyListener);

    lbFile = new JLabel("");
    cbAllFiles = new JCheckBox();
    cbAllFiles.setSelected(forAllfiles);
    cbAllFiles.setText(bundle.getString("overwrite_all_existing"));
    btYes = new JButton(bundle.getString("yes"));
    btNo = new JButton(bundle.getString("no"));
    btNo.setSelected(true);
    
    cbAllFiles.addKeyListener(keyListener);
    
    btYes.addActionListener(actionListener);
    btYes.addKeyListener(keyListener);
    
    btNo.addActionListener(actionListener);
    btNo.addKeyListener(keyListener);
    
    JPanel pnlMain = new JPanel();
    pnlMain.setBorder(BorderFactory.createEmptyBorder(8, 8, 10, 8));
    //pnlMain.setLayout(new GridBagLayout(3, 2, 4, 4));
    pnlMain.setLayout(new GridBagLayout());

    pnlMain.add(lbFile, new GridBagConstraints(
                0, 0, 1, 1, 1.0, 3.0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
    pnlMain.add(cbAllFiles, new GridBagConstraints(
                0, 1, 1, 1, 1.0, 3.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    pnlMain.add(btNo, new GridBagConstraints(
                0, 2, 1, 1, 1.0, 3.0, GridBagConstraints.LINE_END,
                GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    pnlMain.add(btYes, new GridBagConstraints(
                1, 2, 1, 1, 1.0, 3.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    
    Container contFrame = getContentPane();
    contFrame.setLayout(new BorderLayout());
    contFrame.add(pnlMain, "Center");
    
    pack();
    setSize(new Dimension(500,120));
  }
  
  /**
   * Show this dialog.
   * @param parent
   * @return true if user did not No
   */
  public int showOverwriteDialog(JFrame parent, File file) {
    lbFile.setText(file.getAbsolutePath());
    setLocationRelativeTo(parent);
    setVisible(true);
    return overwriteResult();
  }
  
  /**
   * Called when the Yes-Button is pressed.
   */
  private void pressedYes() {
    overwriteFile = true;
    setVisible(false);
  }
  
  /**
   * Called when the No-Button is pressed.
   */
  private void pressedNo() {
    overwriteFile = false;
    setVisible(false);
  }
  
  /**
   * @return the overwrite mode the user selected
   */
  public int overwriteResult() {
    if(cbAllFiles.isSelected() && overwriteFile){
        return Controller.OVERWRITE_ALL_YES;
    } else if(cbAllFiles.isSelected() && !overwriteFile){
        return Controller.OVERWRITE_ALL_NO;
    } else if(!cbAllFiles.isSelected() && overwriteFile){
        return Controller.OVERWRITE_ONCE_YES;
    } else {
        return Controller.OVERWRITE_ONCE_NO;
    }
  }
}
