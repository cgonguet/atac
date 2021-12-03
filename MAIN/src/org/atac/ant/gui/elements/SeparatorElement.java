package org.atac.ant.gui.elements;

import javax.swing.*;
import java.awt.*;

/**
 * Separator element represented by a JSeparator.
 *
 * @ant.type name="gseparator" category="gui" ignore="false"
 */
public class SeparatorElement extends AbstractGuiElement {

  public JComponent getPane() {
    return new JSeparator();
  }
}
