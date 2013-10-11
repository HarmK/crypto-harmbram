package net.sourceforge.jfilecrypt.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.jfilecrypt.Application;
import net.sourceforge.jfilecrypt.Controller;
import net.sourceforge.jfilecrypt.algorithms.Algorithm;
import net.sourceforge.jfilecrypt.algorithms.FileList;
import net.sourceforge.jfilecrypt.algorithms.Steganography;

/**
 * This is
 * 
 * @author Harm
 * 
 */

public class SteganoDialog extends JFrameExtended {
	private static ResourceBundle bundle;

	private JLabel stegInfo, imgPickerTxt, filePickertxt;
	private JPasswordField pfPasswd;
	private JTextField txtImage, txtFile;
	private JButton btEncrypt, btDecrypt, btAbort;
	private JFileChooser imgchooser, fchooser;
	private JButton btImgChoose, btFChoose;

	public void displayError(String title, String text) {
		JOptionPane.showMessageDialog(this, text, title, JOptionPane.ERROR_MESSAGE);
	}

	public SteganoDialog(Controller controller) {
		super();
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		bundle = Application.getResourceBundle();

		setTitle("Steganography");

		// Closing the window is equal to pressing the Abort-Button
		WindowListener windowListener = (new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				pressedAbort();
			}
		});
		addWindowListener(windowListener);

		ActionListener actionListener = (new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == btEncrypt)
					pressedEncrypt();
				else if (evt.getSource() == btDecrypt)
					pressedDecrypt();
				else if (evt.getSource() == btAbort)
					pressedAbort();
			}
		});

		// Enter is equal to pressing the Send-Button and
		// Escape is equal to pressing the Abort-Button
		KeyListener keyListener = (new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
					pressedAbort();
			}
		});
		addKeyListener(keyListener);

		imgPickerTxt = new JLabel("Image:");
		filePickertxt = new JLabel("File:");
		stegInfo = new JLabel("Hide a file in an image!");
		filePickertxt.setHorizontalAlignment(JLabel.RIGHT);

		pfPasswd = new JPasswordField("");
		btEncrypt = new JButton("Encrypt");
		btDecrypt = new JButton("Decrypt");
		btAbort = new JButton(bundle.getString("cancel"));
		imgchooser = new JFileChooser();
		fchooser = new JFileChooser();
		btImgChoose = new JButton("Choose Image");
		btFChoose = new JButton("Choose file");
		txtImage = new JTextField("");
		txtFile = new JTextField("");
		btImgChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (fchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
						txtImage.setText(fchooser.getSelectedFile().getCanonicalPath());
				} catch (IOException ioex) {
					displayError(bundle.getString("error"), ioex.getLocalizedMessage());
				}
			}
		});

		btFChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (fchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
						txtFile.setText(fchooser.getSelectedFile().getCanonicalPath());
				} catch (IOException ioex) {
					displayError(bundle.getString("error"), ioex.getLocalizedMessage());
				}
			}
		});

		imgPickerTxt.setHorizontalAlignment(JLabel.RIGHT);

		btEncrypt.addActionListener(actionListener);
		btDecrypt.addActionListener(actionListener);

		btAbort.addActionListener(actionListener);
		btAbort.addKeyListener(keyListener);

		JPanel pnlMain = new JPanel();
		pnlMain.setBorder(BorderFactory.createEmptyBorder(8, 8, 10, 8));
		pnlMain.setLayout(new GridLayout(4, 1, 4, 4));

		pnlMain.add(stegInfo);
		JPanel imgPickPnl = new JPanel();
		imgPickPnl.setLayout(new GridLayout(1, 3, 2, 2));
		imgPickPnl.add(imgPickerTxt);
		imgPickPnl.add(txtImage);
		imgPickPnl.add(btImgChoose);
		pnlMain.add(imgPickPnl);

		JPanel fPickPanel = new JPanel();
		fPickPanel.setLayout(new GridLayout(1, 3, 2, 2));
		fPickPanel.add(filePickertxt);
		fPickPanel.add(txtFile);
		fPickPanel.add(btFChoose);
		pnlMain.add(fPickPanel);

		JPanel buttonPannel = new JPanel();
		buttonPannel.setLayout(new GridLayout(1, 3, 2, 2));
		buttonPannel.add(btEncrypt);
		buttonPannel.add(btDecrypt);
		buttonPannel.add(btAbort);
		pnlMain.add(buttonPannel);
		Container contFrame = getContentPane();
		contFrame.setLayout(new BorderLayout());
		contFrame.add(pnlMain, "Center");

		pack();

	}

	public File askForTargetFile() {
		JFileChooser chooser = new JFileChooser();

		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	public void pressedEncrypt() {
		String imgPath = txtImage.getText();
		String filePath = txtFile.getText();

		if (imgPath.isEmpty() || filePath.isEmpty()) {
			displayError("Error", "Choose image and file!");
			return;
		}

		String imageSuffix = imgPath.substring(imgPath.lastIndexOf('.'), imgPath.length());
		if (!(imageSuffix.equalsIgnoreCase(".jpg") || imageSuffix.equalsIgnoreCase(".png")
				|| imageSuffix.equalsIgnoreCase(".gif") || imageSuffix.equalsIgnoreCase(".jpeg"))) {
			displayError("Error", "Only .png,.jpg and .gif files are supported!");
			return;
		}
		
		// Optional!
		// TODO check max file size
		// if too big = error
		// {
		// displayError("Error","File is too big for image maximume size = ??");
		// return;
		// }
		// where to save image
		
		File f = askForTargetFile();

		if (f == null) {
			displayError("Error", "Could not save file");
		}
		try {
			Steganography.encrypt(new File(filePath), new File(imgPath), f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pressedAbort() {
		setVisible(false);
	}

	public void pressedDecrypt() {
		String imgPath = txtImage.getText();

		if (imgPath.isEmpty()) {
			displayError("Error", "Choose an image!");
			return;
		}

		String imageSuffix = imgPath.substring(imgPath.lastIndexOf('.'), imgPath.length());
		if (!(imageSuffix.equalsIgnoreCase(".png"))) {
			displayError("Error", "Only .png is supported!");
			return;
		}

		// where to save recovered file
		File f = askForTargetFile();
		if (f == null) {
			displayError("Error", "Could not save file");
		}
		try {
			Steganography.decrypt(new File(imgPath), f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
