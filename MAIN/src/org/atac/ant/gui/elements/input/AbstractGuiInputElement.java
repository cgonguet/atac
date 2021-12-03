package org.atac.ant.gui.elements.input;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.atac.ant.gui.elements.AbstractGuiElement;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public abstract class AbstractGuiInputElement extends AbstractGuiElement {
  private String label;
  String property;
  private boolean focus = false;
  protected int columns = -1;
  private Component input;

  public boolean requestFocus() {
    if (focus) {
      input.requestFocus();
      return true;
    }
    return false;
  }

  public void precheck() {
    if (label == null) {
      throw new BuildException("'label' attribute is required.");
    }
    if (property == null || property.length() == 0) {
      throw new BuildException("'property' attribute is required.");
    }
  }

  public JComponent getPane() {
    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());
    pane.add(getLabel(), BorderLayout.WEST);
    pane.add(getInput(), BorderLayout.CENTER);
    return pane;
  }

  public Component getLabel()
  {
    return new JLabel(label);
  }

  public Component getInput()
  {
    input = getInputComponent();
    return input;
  }

  public abstract Component getInputComponent();

  public void validate() {
    final String value = getInputValue();
    getProject().setProperty(property, value);
    log("Set '" + property + "' to '" + value + "'", Project.MSG_VERBOSE);
  }

  abstract String getInputValue();

  /**
   * Label description for the text element
   *
   * @ant.not-required
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * The name of a property in which the result should be stored.
   *
   * @ant.required
   */
  public void setProperty(String property) {
    this.property = property;
  }


  /**
   * If true: get the focus on
   *
   * @ant.not-required Default is: false
   */
  public void setFocus(boolean focus) {
    this.focus = focus;
  }

  /**
   * Text content
   *
   * @ant.not-required
   */
  public void setColumns(int columns) {
    this.columns = columns;
  }

    public void load(Properties aInProperties)
    {
        setDefaultValue(aInProperties.getProperty(property, getDefaultValue()));
    }

    public void save(Properties aInProperties)
    {
        aInProperties.setProperty(property, getInputValue());
    }

    abstract String getDefaultValue();
    abstract void setDefaultValue(String value);
}
