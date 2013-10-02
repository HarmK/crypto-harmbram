package net.sourceforge.jfilecrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

import net.sourceforge.jfilecrypt.algorithms.*;
import ml.options.*;
import static ml.options.Options.*;

/**
 * This class processes the given command line arguments.
 * It starts an en-/decryption process by itself if
 * there is such an argument. If not this class saves
 * the given arguments in the @see Model and starts
 * the @see GuiMainController which can read out the settings
 * from the Model. On this way you can totally control
 * jFileCrypt from the command line or make some just make
 * some presetting for the GUI.
 */
public class CommandLineController implements Controller {

    private Model model;
    private ResourceBundle bundle;
    private boolean verbose = false;
    private PrintStream sysOut = null;

    public CommandLineController(Model model, String[] args) {
        Application.setController(this);
        this.model = model;
        bundle = Application.getResourceBundle();

        try {
            sysOut = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            sysOut = System.out;
        }

        // Start processing of command line arguments
        Options opt = new Options(args, Prefix.DASH);

        // Syntax: -h
        OptionSet hlpset = opt.addSet("hlpset", 0, 0);
        hlpset.addOption("h", Multiplicity.ONCE);

        // Syntax: -v
        OptionSet verset = opt.addSet("verset", 0, 0);
        verset.addOption("v", Multiplicity.ONCE);

        // Syntax: -l [-a Algorithm]
        OptionSet lstset = opt.addSet("lstalg", 0, 0);
        lstset.addOption("l", Multiplicity.ONCE);
        lstset.addOption("a", Separator.BLANK, Multiplicity.ZERO_OR_ONE);

        // Syntax: -e [-p pwd] [-a alg] [-c lvl] [-o out] [-f] plainfile
        OptionSet encset = opt.addSet("encset", 1, 1000);
        encset.addOption("e", Multiplicity.ONCE);
        encset.addOption("p", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        encset.addOption("a", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        encset.addOption("c", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        encset.addOption("o", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        encset.addOption("f", Multiplicity.ZERO_OR_ONE);

        // Syntax: -d [-p pwd] [-a alg] [-o out] [-f] cipherfile
        OptionSet decset = opt.addSet("decset", 1, 1000);
        decset.addOption("d", Multiplicity.ONCE);
        decset.addOption("p", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        decset.addOption("a", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        decset.addOption("o", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        decset.addOption("f", Multiplicity.ZERO_OR_ONE);

        // Syntax: -g [-p pwd] [-a alg] [-c lvl]
        OptionSet defset = opt.addSet("defset", 0, 1);
        defset.addOption("g", Multiplicity.ONCE);
        defset.addOption("p", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        defset.addOption("a", Separator.BLANK, Multiplicity.ZERO_OR_ONE);
        defset.addOption("c", Separator.BLANK, Multiplicity.ZERO_OR_ONE);

        //Syntax: [-verbose] can be used on encrypt & decrypt, 
        //output depends on the chosen set/action
        //verbose text will always be in english, no translation!
        opt.addOptionAllSets("verbose", Multiplicity.ZERO_OR_ONE);

        if (opt.check("lstalg")) {
            if (lstset.isSet("a")) {
                printAlgorithmInfo(lstset.getOption("a").getResultValue(0));
            } else {
                printAlgorithmNames();
            }
        } else if (opt.check("hlpset")) {
            printHelp();
        } else if (opt.check("encset")) {
            verbose = encset.isSet("verbose");
            displayVerbose("Verbose messages enabled!");
            if (!encset.isSet("p")) {
                model.setPassword(readPassword(true));
            } else {
                model.setPassword(encset.getOption("p").getResultValue(0));
            }
            if (encset.isSet("a")) {
                model.setAlgorithmName(encset.getOption("a").getResultValue(0));
            }
            if (encset.isSet("o")) {
                model.setTargetArchive(new File(encset.getOption("o").getResultValue(0)));
            }
            if (encset.isSet("c")) {
                String lvl = encset.getOption("c").getResultValue(0);
                try {
                    byte blvl = Byte.valueOf(lvl);
                    if (blvl > 0 && blvl < 10) {
                        model.setCompressionLevel(blvl);
                    }
                } catch (NumberFormatException nrEx) {
                    displayError(bundle.getString("wrong_compression_level"),
                            "\"" + lvl + "\"");
                    System.exit(Application.EXIT_WRONG_CMP_LEVEL);
                }
            }
            model.setFileList(new FileList(encset.getData()));
            model.encrypt();
        } else if (opt.check("decset")) {
            verbose = decset.isSet("verbose");
            displayVerbose("Verbose messages enabled!");
            if (!decset.isSet("p")) {
                model.setPassword(readPassword(false));
            } else {
                model.setPassword(decset.getOption("p").getResultValue(0));
            }
            if (decset.isSet("a")) {
                model.setAlgorithmName(decset.getOption("a").getResultValue(0));
            }
            if (decset.isSet("o")) {
                model.setTargetDirectory(new File(decset.getOption("o").getResultValue(0)));
            }
            model.setFileList(new FileList(decset.getData()));
            model.decrypt();
        } else if (opt.check("defset")) {
            if (defset.isSet("p")) {
                String pwd = defset.getOption("p").getResultValue(0);
                model.setPassword(pwd);
            }
            if (defset.isSet("a")) {
                String alg = defset.getOption("a").getResultValue(0);
                model.setAlgorithmName(alg);
            }
            if (defset.isSet("c")) {
                String lvl = defset.getOption("c").getResultValue(0);
                try {
                    byte blvl = Byte.valueOf(lvl);
                    if (blvl > 0 && blvl < 10) {
                        model.setCompressionLevel(blvl);
                    }
                } catch (NumberFormatException nrEx) {
                    displayError(bundle.getString("wrong_compression_level"),
                            "\"" + lvl + "\"");
                    System.exit(Application.EXIT_WRONG_CMP_LEVEL);
                }
            }
            if (defset.getData().isEmpty() == false) {
                model.setFileList(new FileList(encset.getData()));
            }
            /* the GUI controller should catch the default settings
             * from the Model by himself */
            new GuiMainController(model);
        } else if (opt.check("verset")) {
            printVersion();
        } else {
            displayError(bundle.getString("error"),
                    bundle.getString("proc_cmd_line_failed"));
            System.exit(Application.EXIT_PROCESSING_CMD_LINE_FAILED);
        }
        // End processing of command line arguments
    }

    /**
     * Reads the password from the command line and returns it.
     * 
     * It is a mess that the password is displayed in plain text. But
     * the only "nice" solution is coming not until Java 1.6.
     */
    private String readPassword(boolean askTwice) {
        sysOut.println(bundle.getString("password_in_plaintext"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                sysOut.print(bundle.getString("password") + ": ");
                String first = br.readLine();
                if (askTwice == false) {
                    return first;
                }
                sysOut.print(bundle.getString("password_repeat") + ": ");
                String second = br.readLine();
                if (first.equals(second)) {
                    return first;
                } else {
                    displayError(bundle.getString("error"),
                            bundle.getString("passwords_do_not_match"));
                }
            }
        } catch (IOException ioEx) {
            displayError(bundle.getString("err_password_read"),
                    ioEx.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Prints out the names of all available algorithms.
     */
    private void printAlgorithmNames() {
        Algorithm[] alg = model.getAlgorithms();
        for (int i = 0; i < alg.length; i++) {
            sysOut.println((i + 1) + ". " + alg[i].getName());
        }
        // print out advice how to get more information about an algorithm
        sysOut.println();
        sysOut.println(bundle.getString("algorithm_info_cmd"));
    }

    /**
     * Show extended information about a specific algorithm.
     * @param name Name of the algorithm.
     */
    private void printAlgorithmInfo(String name) {
        Algorithm[] alg = model.getAlgorithms();
        for (int i = 0; i < alg.length; i++) {
            if (alg[i].getName().equalsIgnoreCase(name)) {
                sysOut.println(bundle.getString("algorithm") + ": " + alg[i].getName());
                int pwLen = alg[i].getKeyLength();
                if (pwLen <= 0) {
                    sysOut.println(bundle.getString("key_length") + ": " + bundle.getString("any_password_length"));
                } else {
                    sysOut.println(bundle.getString("key_length") + ": " + pwLen);
                }
                sysOut.println(bundle.getString("author") + ": " + alg[i].getAuthor());
                sysOut.println(bundle.getString("license") + ": " + alg[i].getLicense());
                sysOut.println(bundle.getString("website") + ": " + alg[i].getWebsite());
                sysOut.println("");
                return;
            }
        }
        // only reached if algorithm name not found
        displayError(bundle.getString("error"),
                bundle.getString("unknown_alg_text") + ": " + name);
        System.exit(Application.EXIT_UNKNOWN_ALGORITHM);
    }

    /**
     * Prints out the help page.
     */
    private void printHelp() {
        sysOut.println(bundle.getString("cmd_help"));
    }

    /**
     * Prints out program version.
     */
    private void printVersion() {
        sysOut.println(Application.getVersionString());
        displayVerbose("SVN Revision: " + Application.SVN_REVISION);
        displayVerbose("Java Version: " + System.getProperty("java.version"));
    }

    /**
     * Displays an error message. The title is needless on
     * the command line.
     */
    public void displayError(String title, String message) {
        System.err.println(title + ": " + message);
    }

    public void notifyProgressStarted() {
        //sysOut.print(bundle.getString("progress") + ": ");
    }

    public void notifyProgressFinished() {
        sysOut.println("");
    }

    public void displayVerbose(String debug) {
        if (verbose) {
            sysOut.println("verbose: " + debug);
        }
    }

    public void notifyProgressUpdated(int progress) {
        if (progress > 0) {
            sysOut.println(bundle.getString("progress") + ": " + progress + "%");
        } else {
            sysOut.println(bundle.getString("processing_wait"));
        }
    }

    public File askForTargetArchive(File def) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String suffix = model.getAlgorithm().getSuffix();
        while (true) {
            try {
                sysOut.println(bundle.getString("path_correct_suffix") + " \"" + suffix + "\".");
                sysOut.print(bundle.getString("enter_archive") + ": [" + def + "] ");
                String input = br.readLine();
                if (input.equalsIgnoreCase("")) {
                    return def;
                }
                if (input.endsWith(suffix)) {
                    return new File(input);
                }
            } catch (IOException ex) {
                displayError(bundle.getString("error_console_read"),
                        ex.getLocalizedMessage());
            }
        }
    }

    public File askForTargetDirectory(File def) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            sysOut.print(bundle.getString("enter_directory") + ": [" + def + "] ");
            String input = br.readLine();
            if (input.equalsIgnoreCase("\n")) {
                return def;
            } else {
                return new File(input);
            }
        } catch (IOException ex) {
            displayError(bundle.getString("error_console_read"),
                    ex.getLocalizedMessage());
            return null;
        }
    }

    public int shallOverwriteFile(File file) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                sysOut.println(bundle.getString("overwrite_help"));
                sysOut.print(bundle.getString("overwrite_this_file") + " " + file.getCanonicalPath() + "? : [n] ");
                String input = br.readLine();
                if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("")) {
                    return Controller.OVERWRITE_ONCE_NO;
                } else if (input.equalsIgnoreCase("an")) {
                    return Controller.OVERWRITE_ALL_NO;
                } else if (input.equalsIgnoreCase("y")) {
                    return Controller.OVERWRITE_ONCE_YES;
                } else if (input.equalsIgnoreCase("ay")) {
                    return Controller.OVERWRITE_ALL_YES;
                }
            } catch (IOException ex) {
                displayError(bundle.getString("error_console_read"),
                        ex.getLocalizedMessage());
            }
        }
    }
}
