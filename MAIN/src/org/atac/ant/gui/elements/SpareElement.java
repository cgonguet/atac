package org.atac.ant.gui.elements;

import javax.swing.*;

/**
 * Spare element.
 *
 *
 * @ant.type name="gspare" category="gui" ignore="false"
 */
public class SpareElement extends AbstractGuiElement {

  public JComponent getPane() {
    JPanel pane = new JPanel();
    pane.add(new JLabel(""));
    return pane;
  }

}
