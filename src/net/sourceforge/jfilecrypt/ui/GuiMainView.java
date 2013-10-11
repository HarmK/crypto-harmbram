package net.sourceforge.jfilecrypt.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.sourceforge.jfilecrypt.Application;
import net.sourceforge.jfilecrypt.GuiMainController;

import net.sourceforge.jfilecrypt.algorithms.FileList;

/**
 * This class is the GUI of jFileCrypt.
 */
public class GuiMainView extends JFrameExtended {
    private static final long serialVersionUID = 1L;

    private ResourceBundle bundle;
    private GuiMainController controller;
    
    private JButton        btChoose = new JButton();
    private JProgressBar   pbCryptProgress = new JProgressBar();
    private JLabel         lbAlgorithm = new JLabel();
    private JLabel         lbSource = new JLabel();
    private JTextField     tfPath = new JTextField();
    private JComboBox      cmbAlgorithm = new JComboBox();
    private JComboBox      cmbCompressionLevel = new JComboBox();
    private JButton        btDecrypt = new JButton();
    private JButton        btEncrypt = new JButton();
    private JButton        btStop = new JButton();
    private JFileChooser   fchooser = new JFileChooser();
    private JButton        btAbout = new JButton();
    private JButton        btAlgInfo = new JButton();
    private JButton        btStegano = new JButton();
    private JLabel         lbCmpLevel = new JLabel();
    
    /**
     * This is the main GUI frame. It communicates
     * with the @see GuiMainController only and
     * the GuiMainController is the only one who
     * communicates with this class.
     */
    public GuiMainView(GuiMainController controller) {
        super();
        this.controller = controller;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        bundle = Application.getResourceBundle();
        
        init();
    }

    public void setProgress(final int progress) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    if(progress >= 0)
                        pbCryptProgress.setValue(progress);
                    else
                        pbCryptProgress.setIndeterminate(true);

                    pbCryptProgress.repaint();
                }
            });
        
    }
    
    /**
     * Setups the GUI.
     */
    private void init() {
        try {
            setIconImage(new ImageIcon(AboutFrame.class.getResource("icon.png")).getImage());
        } catch(NullPointerException e) {
            controller.displayError(bundle.getString("error"),
                    e.getLocalizedMessage());
        }
        
        setTitle(Application.NAME + " " + Application.VERSION);
        setSize(new Dimension(550, 225));
        setResizable(true);
        
        // setup components
        
        btChoose.setText(bundle.getString("choose_file"));
        btChoose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btChoose_actionPerformed(e);
            }
        });
        
        
        btStegano.setText("Steganography");
        btStegano.setActionCommand("Stegano");
        btStegano.addActionListener(controller);
        
        btEncrypt.setText(bundle.getString("encrypt"));
        btEncrypt.setActionCommand("encrypt");
        btEncrypt.addActionListener(controller);
        
        btDecrypt.setText(bundle.getString("decrypt"));
        btDecrypt.setActionCommand("decrypt");
        btDecrypt.addActionListener(controller);
        
        lbSource.setText(bundle.getString("source") + ": ");
        lbSource.setVerticalAlignment(JLabel.CENTER);
        lbSource.setHorizontalAlignment(JLabel.RIGHT);
        
        lbAlgorithm.setText(bundle.getString("algorithm") + ": ");
        lbAlgorithm.setVerticalAlignment(JLabel.CENTER);
        lbAlgorithm.setHorizontalAlignment(JLabel.RIGHT);

        lbCmpLevel.setText(bundle.getString("compression_level")+ ": ");
        lbCmpLevel.setVerticalAlignment(JLabel.CENTER);
        lbCmpLevel.setHorizontalAlignment(JLabel.LEFT);//TODO: fix this, should be RIGHT as the others
        
        for(int i=0; i<10; i++)
            cmbCompressionLevel.addItem(new Integer(i));
        
        fchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        btStop.setText(bundle.getString("stop"));
        btStop.setActionCommand("stop");
        btStop.addActionListener(controller);
        btStop.setEnabled(false);

        btAbout.setText(bundle.getString("about"));
        btAbout.setActionCommand("about");
        btAbout.addActionListener(controller);
        
        btAlgInfo.setText(bundle.getString("algorithm_info"));
        btAlgInfo.setActionCommand("alginfo");
        btAlgInfo.addActionListener(controller);
        
        pbCryptProgress.setMaximum(100);
        
        // setup boxed layouts with panels
        
        JPanel jpInput = new JPanel();
        JPanel jpCompression = new JPanel();
        JPanel jpCryptButtons = new JPanel();
        JPanel jpProgress = new JPanel();
        
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(jpInput, new GridBagConstraints(
                0, 0, 1, 1, 1.0, 3.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,8,8,8), 0, 0
                ));
        getContentPane().add(jpCompression, new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0
                ));
        getContentPane().add(jpCryptButtons, new GridBagConstraints(
                0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,8,0,8), 0, 0
                ));
        getContentPane().add(jpProgress, new GridBagConstraints(
                0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,8,8,8), 0, 0
                ));
        
        // add components to the panels
        
        jpInput.setLayout(new GridBagLayout());
        jpInput.add(lbSource, new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0,0,0,8), 0, 0
                ));
        jpInput.add(tfPath, new GridBagConstraints(
                1, 0, 1, 1, 3.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0,0,0,8), 0, 0
                ));
        jpInput.add(btChoose, new GridBagConstraints(
                2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0
                ));
        jpInput.add(lbAlgorithm, new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,0,0,8), 0, 0
                ));
        jpInput.add(cmbAlgorithm, new GridBagConstraints(
                1, 1, 1, 1, 3.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,0,0,8), 0, 0
                ));
        jpInput.add(btAlgInfo, new GridBagConstraints(
                2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,0,0,0), 0, 0
                ));
        jpInput.add(lbCmpLevel, new GridBagConstraints(
                0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,0,0,-18), 0, 0
                ));
        jpInput.add(cmbCompressionLevel, new GridBagConstraints(
                1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,0,0,8), 0, 0
                ));
        jpInput.add(btStegano, new GridBagConstraints(
        		2, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,0,0,0), 0, 0
                ));
        jpInput.add(Box.createRigidArea(new Dimension(btAlgInfo.getWidth(),5)), new GridBagConstraints(
                2, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(8,0,0,0), 0, 0
                ));
        
        
        GridLayout cryptButtonsGrid = new GridLayout(1, 2);
        cryptButtonsGrid.setHgap(8);
        jpCryptButtons.setLayout(cryptButtonsGrid);
        jpCryptButtons.add(btEncrypt);
        jpCryptButtons.add(btDecrypt);
        
        jpProgress.setLayout(new GridBagLayout());
        jpProgress.add(pbCryptProgress, new GridBagConstraints(
            0, 0, 1, 1, 25.0, 1.0, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0
            ));

        jpProgress.add(btStop, new GridBagConstraints(
            1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
            GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0
            ));

        jpProgress.add(btAbout, new GridBagConstraints(
            2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST,
            GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0
            ));
            
        DropTargetListener dropTargetListener =
         new DropTargetListener() {

          public void dragEnter(DropTargetDragEvent e) {}

          public void dragExit(DropTargetEvent e) {}

          public void dragOver(DropTargetDragEvent e) {}

          public void drop(DropTargetDropEvent e) {
            try {
              Transferable tr = e.getTransferable();
              DataFlavor[] flavors = tr.getTransferDataFlavors();
              for (int i = 0; i < flavors.length; i++)
               if (flavors[i].isFlavorJavaFileListType()) {
                // Zun�chst annehmen
                e.acceptDrop (e.getDropAction());
                java.util.List files = (List)tr.getTransferData(flavors[i]);
                File f = (File) files.get(0);
                tfPath.setText(f.getAbsolutePath());
                e.dropComplete(true);
                return;
               }
            } catch (Throwable t) { t.printStackTrace(); }
            // There was a problem
            e.rejectDrop();
          }
           

          public void dropActionChanged(
                 DropTargetDragEvent e) {}
        };
        DropTarget dropTarget = new DropTarget(tfPath, dropTargetListener);
        tfPath.setDropTarget(dropTarget);
        //jpProgress.setLayout(new GridLayout(1, 1));
        //jpProgress.add(pbCryptProgress);
        //TODO: cmbAlgorithm.addItem(bundle.getString("add_algorithm"));
    }
    
    /**
     * Adds an algorithm name to the alrogithm list.
     */
    public void addAlgorithmItem(String algname) {
        cmbAlgorithm.addItem(algname);
    }
    
    /**
     * Sets the file path by calling @see javax.swing.JFileChooser.
     */
    private void btChoose_actionPerformed(ActionEvent e) {
        try {
            if(fchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                tfPath.setText(fchooser.getSelectedFile().getCanonicalPath());
        } catch (IOException ioex) {
            displayError(bundle.getString("error"),
                    ioex.getLocalizedMessage());
        }
    }
    
    public void displayError(String title, String text) {
      JOptionPane.showMessageDialog(this, text, title,
              JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Sets the currents paths to the given files.
     */
    public void setFileList(FileList paths) {
        StringBuilder sb = new StringBuilder();
        Iterator it = paths.getIterator();
        while(it.hasNext()){
            sb.append( ((File) it.next()).getAbsolutePath() );
        }
        tfPath.setText(sb.toString());
    }
    
    /**
     * Returns the current paths.
     */
    public FileList getFileList() {
        return new FileList(tfPath.getText());
    }
    
    /**
     * Sets the current (chosen) algorithm name
     * in the algorithm list to the given name.
     */
    public void setCurrentAlgorithmName(String algname) {
        cmbAlgorithm.setSelectedItem(algname);
        if(! algname.equals(cmbAlgorithm.getSelectedItem().toString()))
            displayError(bundle.getString("unknown_alg_title"),
                    bundle.getString("unknown_alg_text") + algname);
    }
    
    /**
     * Returns the current (chosen) algorithm name
     * in the algorithm list.
     */
    public String getCurrentAlgorithmName() {
        return (String) cmbAlgorithm.getSelectedItem();
    }
    
    /**
     * Selects the given compression level.
     */
    public void setCompressionLevel(byte level) {
        cmbCompressionLevel.setSelectedItem(new Integer(level));
    }
    
    /**
     * Returns the currently selected compression level.
     */
    public byte getCompressionLevel() {
        return Byte.parseByte(cmbCompressionLevel.getSelectedItem().toString());
    }
    
    /**
     * Depending on whether there is a progress running some
     * buttons may be en-/disabled (e.g. the Encrypt button).
     */
    public void setProgressRunning(boolean running) {
        if(!running){
            pbCryptProgress.setIndeterminate(false);
        }
        pbCryptProgress.setStringPainted(running);
        btEncrypt.setEnabled(! running);
        btDecrypt.setEnabled(! running);
        btStop.setEnabled(running);
        SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        pbCryptProgress.setValue(0);
		        pbCryptProgress.repaint();
		    }
		});
    }
}
