package net.sourceforge.jfilecrypt;

import java.io.File;

/**
 * A controller tells the model about the properties and
 * says when to start encryption or decryption. After
 * that it can display the progress status of the task.
 * The controller should either communicate itself
 * with the user or (strongly recommended for GUI!)
 * create a new "View" instance (search for MVC) or read this:
 * http://en.wikipedia.org/wiki/Model-view-controller
 */
public interface Controller {

    public static final int TARGET_ENCRYPT = 1;
    public static final int TARGET_DECRYPT = 2;

    public static final int OVERWRITE_ONCE_NO = 4;
    public static final int OVERWRITE_ONCE_YES = 8;
    public static final int OVERWRITE_ALL_NO = 16;
    public static final int OVERWRITE_ALL_YES = 32;

    /**
     * Notify the Controller that the en-/decrypt progress
     * has started.
     */
    public void notifyProgressStarted();

    /**
     * Notifies the controller about a new progress percent value
     * to be displayed. If a percentage cannot be determinated,
     * the model sends -1 once to give the controller the chance to
     * display an indeterminate waiting signal to the user.
     * @param the progress in percent
     */

    public void notifyProgressUpdated(int progress);

    /**
     * Notify the Controller that the en-/decrypt progress
     * has finished.
     */
    public void notifyProgressFinished();
    
    /**
     * Displays an error.
     * @param title the title of the dialog
     * @param message the message
     */
    public void displayError(String title, String message);

    /**
     * Called to display detailed debug information about all processes
     * running during the en-/decryption.
     *
     */
    public void displayVerbose(String debug);


    /**
     * Asks the Controller to return the path of the new archive.
     * Normally, the Controller should ask the user where to store his archive
     * and therefore propose the given default file.
     * This method gets called only by Model.
     * @param def the default location
     * @return the chosen archive location
     */
    public File askForTargetArchive(File def);

    /**
     * Asks the Controller to return the path of the directory
     * in which the decrypted files should be stored.
     * Normally, the Controller should ask the user for a directory
     * and therefore propose the given default file.
     * This method gets called only by Model.
     * @param def the default directory
     * @return the chosen directory
     */
    public File askForTargetDirectory(File def);

    /**
     * Asks the Controller whether the given file should be overwritten.
     * Default should be OVERWRITE_ONCE_NO.
     * @param file to be overwritten
     * @return the user's answer
     */
    public int shallOverwriteFile(File file);
}
