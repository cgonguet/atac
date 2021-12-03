package org.atac.ant.gui.elements;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.atac.ant.gui.GuiAggregatorTask;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractGuiElement extends ProjectComponent {

  private String fontName;
  private int fontSize = -1;
  private int fontStyle = -1;

  public void precheck() {
  }

  public abstract JComponent getPane();

  public void validate() {
  }

  public boolean requestFocus() {
    return false;
  }


  /**
   * GUI Font name
   *
   * @ant.not-required Default is: GUI default
   */
  public void setFontName(final String fontName) {
    this.fontName = fontName;
  }

  /**
   * GUI Font style
   *
   * @ant.not-required Default is: GUI default
   */
  public void setFontStyle(final String style) {
    Integer decodeFontStyle = GuiAggregatorTask.fontStyles.get(style);
    if (decodeFontStyle == null) {
      throw new BuildException("'fontStyle' should be in " + GuiAggregatorTask.fontStyles.keySet());
    }
    this.fontStyle = decodeFontStyle;
  }

  /**
   * GUI Font size
   *
   * @ant.not-required Default is: GUI default
   */
  public void setFontSize(final int fontSize) {
    this.fontSize = fontSize;
  }

  protected Font getFont(String componentFont) {
    return new Font(fontName == null ? fontName = UIManager.getFont(componentFont).getName() : fontName,
                    fontStyle == -1 ? fontStyle = UIManager.getFont(componentFont).getStyle() : fontStyle,
                    fontSize == -1 ? fontSize = UIManager.getFont(componentFont).getSize() : fontSize);
  }

  public void postAdd(Component parent)
  {

  }

  public int getFillBehavior()
  {
    return GridBagConstraints.HORIZONTAL;
  }

}
