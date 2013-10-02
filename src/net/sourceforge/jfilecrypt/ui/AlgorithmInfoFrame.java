package net.sourceforge.jfilecrypt.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import net.sourceforge.jfilecrypt.Application;
import net.sourceforge.jfilecrypt.Controller;

import net.sourceforge.jfilecrypt.algorithms.Algorithm;

/**
 * Shows some information on the given algorithm in a new window.
 */
public class AlgorithmInfoFrame extends JFrameExtended {
    private static final long serialVersionUID = 1L;
    
    private ResourceBundle bundle;
    
    public AlgorithmInfoFrame(Controller controller, Algorithm algorithm) {
        super();
        bundle = Application.getResourceBundle();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        try {
            setIconImage(new ImageIcon(AboutFrame.class.getResource("icon.png")).getImage());
        } catch(NullPointerException e) {
            controller.displayError(bundle.getString("error"),
                    e.getLocalizedMessage());
        }
        
        setTitle(bundle.getString("algorithm") + " " + algorithm.getName());
        setSize(new Dimension(450, 300));
        
        BorderPanel bpAlgorithm =
            new BorderPanel(bundle.getString("algorithm"));
        bpAlgorithm.add(new JLabel(algorithm.getName()));
        
        BorderPanel bpPassphraseLength =
            new BorderPanel(bundle.getString("key_length"));
        String passLengthText = "";
        if(algorithm.getKeyLength() <= 0)
            passLengthText = bundle.getString("any_password_length");
        else
            passLengthText = "" + algorithm.getKeyLength() + " " + bundle.getString("password_will_shorted");
        bpPassphraseLength.add(new JLabel(passLengthText));

        BorderPanel bpAuthor =
            new BorderPanel(bundle.getString("author"));
        bpAuthor.add(new JLabel(algorithm.getAuthor()));

        BorderPanel bpLicense =
            new BorderPanel(bundle.getString("license"));
        bpLicense.add(new JLabel(algorithm.getLicense()));
        
        BorderPanel bpWebsite =
            new BorderPanel(bundle.getString("website"));
        String url = algorithm.getWebsite();
        String link = "<html><a href='" + url + "'>" + url + "</a></html>";
        bpWebsite.add(new JLabel(link));
        
        getContentPane().setLayout(new GridLayout(5, 1));
        getContentPane().add(bpAlgorithm);
        getContentPane().add(bpPassphraseLength);
        getContentPane().add(bpAuthor);
        getContentPane().add(bpLicense);
        getContentPane().add(bpWebsite);
    }
}