package org.atac.ant.gui.elements;

import org.apache.tools.ant.BuildException;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Label element represented by a JLabel.
 *
 * @ant.type name="glabel" category="gui" ignore="false"
 */
public class LabelElement extends AbstractGuiElement {
  String text;
  private Integer alignment = JLabel.LEFT;

  public JComponent getPane() {
    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());
    JLabel label = new JLabel(text, alignment);
    label.setFont(getFont("Label.font"));
    pane.add(label, BorderLayout.CENTER);
    return pane;
  }

  /**
   * Label content
   *
   * @ant.not-required
   */
  public void setText(String text) {
    this.text = text;
  }

  static Map<String, Integer> aligns;

  static {
    aligns = new HashMap<String, Integer>();
    aligns.put("center", JLabel.CENTER);
    aligns.put("left", JLabel.LEFT);
    aligns.put("right", JLabel.RIGHT);

  }

  /**
   * Label alignment (left|center|right)
   *
   * @ant.not-required  Default is: left
   */
  public void setAlignment(String align) {
    alignment = aligns.get(align);
    if (alignment == null) {
      throw new BuildException("'alignment' attribute should be in: " + aligns.keySet());
    }
  }
}
