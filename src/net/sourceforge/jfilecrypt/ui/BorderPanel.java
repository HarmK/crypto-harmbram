package net.sourceforge.jfilecrypt.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Represents a panel with a visible border.
 */
public class BorderPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  
  public BorderPanel(String title) {
    super();
    Border b = BorderFactory.createEtchedBorder();
    Border tb = BorderFactory.createTitledBorder(b, title);
    setBorder(tb);
  }
}
