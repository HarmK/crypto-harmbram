package net.sourceforge.jfilecrypt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class starts the whole program. It contains the basic
 * settings which are important for every class (e.g. language setting).
 * It creates one instance of @see Model and one instance of a
 * @see Controller which continues the program by processing
 * the command line arguments or starting a GUI.
 */
public class Application {
    // Information about the application
    // They become extended and localized in the main() method
    // TODO: Update this information before every release or commit!
    public static String NAME = "jFileCrypt";
    public static String VERSION = "0.3.0t";
    public static String RELEASE_DATE = "14.07.2010";
    public static String SVN_REVISION = "$Rev: 29 $";//FIX: only changes when application gets changed
    
    // These codes are used when application exits with an error
    public static final byte EXIT_UNKNOWN_ALGORITHM = 10;
    public static final byte EXIT_PROCESSING_CMD_LINE_FAILED = 11;
    public static final byte EXIT_WRONG_CMP_LEVEL = 12;
    public static final byte EXIT_WRONG_KEY_LENGTH = 13;
    public static final byte EXIT_WRONG_RELEASE_DATE_FORMAT = 14;
    
    private static ResourceBundle bundle;
    private static Controller controller;
    
    /**
     * All classes are expected to receive the ResourceBundle
     * through this application class!
     */
    public static ResourceBundle getResourceBundle() {
        return bundle;
    }

    /**
     * All classes are expected to receive the Controller
     * through this application class!
     */
    public static Controller getController() {
        return controller;
    }
    
    /**
     * The Controller who got called in Application's "Main" method
     * should register here.
     * @return
     */

    public static void setController(Controller contr) {
        controller = contr;
    }
    
    /**
     * This method starts the application. It finds out the language,
     * creates a new Model and starts a Controller. It depends
     * on the given command line arguments which Controller is
     * started, by default (no arguments) it is the GuiMainController.
     */
    public static void main(String[] args) {
        try {
            bundle = ResourceBundle.getBundle("net/sourceforge/jfilecrypt/translations/jfilecrypt", Locale.getDefault());
        } catch(MissingResourceException resEx) {
            bundle = ResourceBundle.getBundle("net/sourceforge/jfilecrypt/translations/jfilecrypt", Locale.ENGLISH);
        }
        
        // Extend the information about the application
        Date releaseDate = null;
        try {
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM,
                    Locale.GERMAN);
            releaseDate = df.parse(RELEASE_DATE);
        } catch (ParseException e) {
            System.err.println("The given release date has wrong format!");
            e.printStackTrace();
            System.exit(EXIT_WRONG_RELEASE_DATE_FORMAT);
            
        }
        SimpleDateFormat format = new SimpleDateFormat("d. MMMM yyyy");
        Application.RELEASE_DATE = format.format(releaseDate);

        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(SVN_REVISION);
        if(matcher.find())
        {
           SVN_REVISION = matcher.group();
        }
        
        // there should be only one Model for all controllers
        Model model = new Model();
        
        /*
         * If no arguments available then just start the GUI.
         * If there are some arguments then start the
         * command line controller (which can start a GUI by himself).
         */
        if(args.length <= 0) {
            new GuiMainController(model);
        } else {
            new CommandLineController(model, args);
        }
    }

    public static String getVersionString() {
        return NAME + " " + VERSION + /*"r" + SVN_REVISION +*/ " (" +
                Application.RELEASE_DATE + ")";
        //SVN_REVISION should get added again when we found a way get a global revision number,
        //not only a single file rev number.
    }
}
