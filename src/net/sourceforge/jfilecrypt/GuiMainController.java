package net.sourceforge.jfilecrypt;

import net.sourceforge.jfilecrypt.ui.AlgorithmInfoFrame;
import net.sourceforge.jfilecrypt.ui.AboutFrame;
import net.sourceforge.jfilecrypt.ui.PasswordDialog;
import net.sourceforge.jfilecrypt.ui.GuiMainView;
import net.sourceforge.jfilecrypt.ui.OverwriteDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import net.sourceforge.jfilecrypt.algorithms.Algorithm;


/**
 * This class manages the communication with the user
 * through a GUI. It opens a GuiMainView
 * and transfers all settings and actions to the Model.
 */
public class GuiMainController
    implements Controller, ActionListener
{
    private static ResourceBundle bundle;
    private Model model;
    private GuiMainView view;
    
    public GuiMainController(Model model) {
        Application.setController(this);
        this.model = model;

        bundle = Application.getResourceBundle();
        
        view = new GuiMainView(this);
        
        // add algorithm names to the View
        Algorithm[] alg = model.getAlgorithms();
        for(int i=0; i<alg.length; i++)
            view.addAlgorithmItem(alg[i].getName());
        
        // set the default settings (catch them from the Model)
        view.setFileList(model.getFileList());
        view.setCurrentAlgorithmName(model.getAlgorithmName());
        view.setCompressionLevel(model.getCompressionLevel());
        
        view.centerScreen();
        view.setVisible(true);
    }
    
    /**
     * Reads out the configuration in the View and stores
     * it in the Model.
     */
    private void saveConfigurationToModel() {
        model.setFileList(view.getFileList());
        model.setAlgorithmName(view.getCurrentAlgorithmName());
        model.setCompressionLevel(view.getCompressionLevel());
    }
    
    /**
     * Displays a PasswordDialog one or more times where the user can type
     * in a password, in case the input was both times the same
     * it returns the password, otherwise it returns null.
     * @param times How often the PasswordDialog should be displayed.
     * @return The typed in password if the passwords matched,
     *     otherwise null. 
     */
    protected String getPassword(final int times) {
        if(times <= 0) {
            throw new IllegalArgumentException("times cannot be lower than 1!");
        }
        String[] pwInput = new String[times];
        // ask password n times
        for(int i=0; i<times; i++) {
        	PasswordDialog pwdDialog = new PasswordDialog(view);
            pwdDialog.showPasswordDialog(view);
            pwInput[i] = pwdDialog.getPassword();
            if(pwdDialog.aborted()) {
                return null;
            }
        }
        // compare passwords and return null if they are not equal
        if(times > 1) {
            for(int i=1; i<times; i++) {
                if(pwInput[i].equals(pwInput[i-1]) == false) {
                    displayError(bundle.getString("password_input_error"),
                        bundle.getString("passwords_do_not_match"));
                    return null;
                }
            }
        }
        // all passwords are equal, return the password
        return pwInput[0];
    }
    
    /**
     * Handles the events of the View.
     */
    public void actionPerformed(final ActionEvent e) {
        String msg = e.getActionCommand();
        if(msg.equals("encrypt")) {
            saveConfigurationToModel();
            String pass = model.getPassword() == null ? getPassword(2) : model.getPassword();
            if(pass != null) {
                model.setPassword(pass);
                model.encrypt();
            }
        } else if(msg.equals("decrypt")) {
            saveConfigurationToModel();
            String pass = model.getPassword() == null ? getPassword(1) : model.getPassword();
            if(pass != null) {
                model.setPassword(pass);
                model.decrypt();
            }
        } else if(msg.equals("stop")) {
            int result = JOptionPane.showConfirmDialog(view, bundle.getString("stop_warning_text"), bundle.getString("stop_warning_title"), JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.OK_OPTION){
                model.stop();
            }
        } else if(msg.equals("alginfo")) {
            String algname = view.getCurrentAlgorithmName();
            Algorithm algorithm = model.getAlgorithmByName(algname);
            if(algorithm != null) {
                AlgorithmInfoFrame algFrame =
                    new AlgorithmInfoFrame(this, algorithm);
                algFrame.centerScreen();
                algFrame.setVisible(true);
            }
        } else if(msg.equals("about")) {
            AboutFrame about = new AboutFrame(this);
            about.centerScreen();
            about.setVisible(true);
        } else {
          displayError(bundle.getString("unknown_action_cmd_title"),
                  bundle.getString("unknown_action_cmd_text") + ": " + msg);
        }
    }
    
    public void notifyProgressStarted() {
        view.setProgressRunning(true);
    }
    
	public void notifyProgressFinished() {
        view.setProgressRunning(false);
    }

    public void notifyProgressUpdated(int progress) {
        view.setProgress(progress);
    }

    /**
     * Displays an error.
     */
    public void displayError(String title, String text) {
        view.displayError(title, text);
        notifyProgressFinished();
    }

    public void displayVerbose(String debug) {
        if(false)
            System.out.println("verbose: " + debug);
    }

    public File askForTargetArchive(File def) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if(f.isDirectory())
                        return true;
                    else{
                        Algorithm algs[] = model.getAlgorithms();
                        for(int i = 0; i < algs.length; i++){
                            if(f.getName().endsWith(algs[i].getSuffix())){
                                return true;
                            }
                        }
                        return false;
                    }
                }
                public String getDescription() {
                    return "jFileCrypt-Archive";
                }
            });
        chooser.setSelectedFile(def);
        int returnVal = chooser.showSaveDialog(view);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public File askForTargetDirectory(File def) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setSelectedFile(def);
        int returnVal = chooser.showSaveDialog(view);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public int shallOverwriteFile(File file) {
        OverwriteDialog owDialog = new OverwriteDialog(view);
        return owDialog.showOverwriteDialog(view, file);
    }
}
