package net.sourceforge.jfilecrypt.algorithms;

import java.util.*;
import net.sourceforge.jfilecrypt.Application;


/**
 * AlgorithmFactory manages the Algorithms.
 */

public class AlgorithmFactory {
	private HashMap<String, Algorithm> algorithms;

	private ResourceBundle bundle;

    private static AlgorithmFactory defaultAlgFac = null;

	/**
	 * The only constructor. It also adds the default algorithms.
	 * 
	 * @param manager
	 *            the JFManager which can called using <code>getManager()</code>
	 */
	private AlgorithmFactory() {
		bundle = Application.getResourceBundle();
		algorithms = new HashMap<String, Algorithm>();

        //Add default algorithms.
		addAlgorithm("Blowfish", new JCEAlgorithm("Blowfish"));
		addAlgorithm("DES", new JCEAlgorithm("DES"));
		addAlgorithm("TripleDES", new JCEAlgorithm("TripleDES"));
		addAlgorithm("AES", new JCEAlgorithm("AES"));
		addAlgorithm("RC4", new JCEAlgorithm("RC4"));
		addAlgorithm("TwoFish", new TwoFishAlgorithm());
		addAlgorithm("Skipjack", new SkipjackAlgorithm());
	}

    /**
     * The only way to get an AlgorithmFactory instance is this singleton method,
     * which inits the default factory object if needed and returns it.
     * @return the default AlgorithmFactory
     */

    public static AlgorithmFactory getDefaultAlgorithmFactory(){
        if(defaultAlgFac == null)
            defaultAlgFac = new AlgorithmFactory();
        return defaultAlgFac;
    }

	/**
	 * adds a new Algorithm. This method adds a new Algorithm-object to its
	 * private HashMap using the name as key. The name shouldn't confuseable
	 * with other algorithms.
	 * 
	 * @param name
	 *            the name of the Algorithm, should be clear.
	 * @param alg
	 *            the right Algorithm-Object
	 */
	public void addAlgorithm(String name, Algorithm alg) {
		if (!algorithms.containsKey(name))
			algorithms.put(name, alg);
	}

	/**
	 * returns an Enumeration with the algorithms.
	 * 
	 * @return an enumeration with the Algorithm-objects.
	 */
	public Algorithm[] getAlgorithms() {
		Object alos[] = algorithms.values().toArray();
        Algorithm[] algs = new Algorithm[alos.length];
        for(int i = 0; i < alos.length; i++){
            algs[i] = (Algorithm) alos[i];
        }
        return algs;
	}

	/**
	 * returns the algorithm which is bound to the given key in the HashMap.
	 * 
	 * @param alg
	 * @return the Algorithm
	 */
	public Algorithm getAlgorithm(String alg) {
		Algorithm a = algorithms.get(alg);
		if (a == null) {
			Application.getController().displayError(bundle.getString("unknown_alg_title"), bundle
					.getString("unknown_alg_text")
					+ alg);
			// System.exit(Application.EXIT_UNKNOWN_ALGORITHM);
		}
		return a;
	}
}
