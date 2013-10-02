package net.sourceforge.jfilecrypt.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 * Extends javax.swing.JFrame by some very useful methods.
 * Right now it is just the possibility to center a window. 
 */
public class JFrameExtended extends JFrame {
    private static final long serialVersionUID = 1L;
    
    /**
     * Moves this frame to the center of the screen.
     */
    public void centerScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        setLocation(
            (screenSize.width-frameSize.width)/2,
            (screenSize.height-frameSize.height)/2
        );
    }
}
